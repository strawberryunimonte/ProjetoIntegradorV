package com.example.strawberry.foundyou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.FirebaseSuporte.AcessoHTTP;
import com.example.strawberry.foundyou.FirebaseSuporte.MyFirebaseInstanceIdService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ActivityCadastro extends AppCompatActivity {

    private DatabaseReference reference;
    private StorageReference storageReferenceRaiz;
    private EditText nomeUser, emailUser, senhaUser;
    private String curso;
    private FirebaseAuth auth;
    private Button cadastrar;
    private ImageView fotoUser;
    private Spinner cursos;
    private ProgressDialog dialog;
    private Usuario usuario;
    private byte [] bytesFoto;
    private Uri uriFotoCortada;
    public final static String PREF = "PREF";
    public static final String UPLOAD_URL = "http://foundyou.esy.es/registrarUserToken.php";
    public static final String UPLOAD_KEY = "nome";
    public static final String UPLOAD_KEY1 = "token";
    public static final String UPLOAD_KEY2 = "uid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        reference = database.getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReferenceRaiz = storage.getReferenceFromUrl("gs://foundyou-6ae4b.appspot.com");

        nomeUser = (EditText) findViewById(R.id.nomeEdt);
        emailUser = (EditText) findViewById(R.id.emailEdt);
        senhaUser = (EditText) findViewById(R.id.senha);
        cursos = (Spinner) findViewById(R.id.cursosSpinner);
        cadastrar = (Button) findViewById(R.id.cadastrarBtn);
        fotoUser = (ImageView) findViewById(R.id.fotoPerfil);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Cursos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cursos.setAdapter(adapter);

        cursos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

             curso = String.valueOf(adapterView.getItemAtPosition(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fotoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.startPickImageActivity(ActivityCadastro.this);

            }
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(ActivityCadastro.this);
                dialog.setMessage("Cadastrando Usuário");
                dialog.setCancelable(false);
                dialog.show();

                validarCampos();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {

           Uri uriFotoDispositvo = CropImage.getPickImageResultUri(this,data);

            CropImage.activity(uriFotoDispositvo)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                fotoUser.setImageURI(result.getUri());
                fotoUser.setDrawingCacheEnabled(true);
                fotoUser.buildDrawingCache();
                Bitmap bitmap = fotoUser.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
                bytesFoto = stream.toByteArray();

                uriFotoCortada = result.getUri();

            }

        } else {

            snackBar("Nenhuma foto selecionada.");
        }

    }

    public void validarCampos(){

        String nome = nomeUser.getText().toString().trim();
        String email = emailUser.getText().toString().trim();
        String senha = senhaUser.getText().toString().trim();

        if (nome.equals("") || email.equals("") || senha.equals("")) {

            snackBar("Por favor preencha todos os campos!");
            dialog.dismiss();

        }else if (senha.length()<6){

            snackBar("A senha deve contar o mínimo de 6 caracteres.");
            dialog.dismiss();

        }else if(curso.equals("Escolha seu Curso")){

            snackBar("Por Favor selecione seu curso!");
            dialog.dismiss();

        }else {

            usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setCurso(curso);
            usuario.setEmail(email);
            usuario.setSenha(senha);

            criarUsuario();

        }

    }

    public void criarUsuario(){

        auth.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (!verificaConexao()){

                    snackBar("Verifique sua conexão com internet!");
                    dialog.dismiss();

                }else if (task.isSuccessful()){

                    if (uriFotoCortada==null){

                        usuario.setFoto("Sem Foto");
                        salvarTokenBdExterno();
                        salvarDadosFirebase();
                        salvarDadosSessaoUser();
                        dialog.dismiss();

                        Toast.makeText(ActivityCadastro.this,"Usuário sem foto!",Toast.LENGTH_SHORT).show(); // teste

                    } else {

                        uploadFotoUser();
                        dialog.dismiss();

                        Toast.makeText(ActivityCadastro.this,"Usuário com foto!!",Toast.LENGTH_SHORT).show(); //teste

                    }

                } else  if (!task.isSuccessful() && verificaConexao()){

                    snackBar("Email de usuário já existe!");
                    dialog.dismiss();

                }

            }
        });

    }

    public void snackBar(String mensagem){

        Snackbar.make(nomeUser,mensagem,Snackbar.LENGTH_LONG).setAction("Action",null).show();

    }

    public boolean verificaConexao() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            return true;

        } else {

            return false;

        }

    }

    public void salvarDadosFirebase(){

        usuario.setSenha(null);
        usuario.setUid(auth.getCurrentUser().getUid());
        reference.child("Cursos").child(usuario.getCurso()).child(usuario.getUid()).setValue(usuario);

    }

    public void uploadFotoUser(){

       StorageReference storageReferenceIconeUser = storageReferenceRaiz.child("IconeUser").child(uriFotoCortada.getLastPathSegment());

        UploadTask uploadTask = storageReferenceIconeUser.putBytes(bytesFoto);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                usuario.setFoto(taskSnapshot.getDownloadUrl().toString());
                salvarTokenBdExterno();
                salvarDadosFirebase();
                salvarDadosSessaoUser();

                Toast.makeText(ActivityCadastro.this,"Usuário criado com sucesso!",Toast.LENGTH_LONG).show(); //teste

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ActivityCadastro.this,"Erro ao realizar upload da foto!",Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void salvarTokenBdExterno(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                AcessoHTTP requisicaoPost = new AcessoHTTP();

                MyFirebaseInstanceIdService.tinyDB = new TinyDB(ActivityCadastro.this);

                String token = MyFirebaseInstanceIdService.tinyDB.getString("token");

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, usuario.getNome());
                data.put(UPLOAD_KEY1,token);
                data.put(UPLOAD_KEY2, usuario.getUid());

                String result = requisicaoPost.sendPostRequest(UPLOAD_URL, data);

            }
        }).start();

    }

    public void salvarDadosSessaoUser() {

        SharedPreferences.Editor editor = getSharedPreferences(PREF, MODE_PRIVATE).edit();
        editor.putString("nome", usuario.getNome());
        editor.putString("uid", usuario.getUid());
        editor.putString("foto", usuario.getFoto());
        editor.apply();

    }

}
