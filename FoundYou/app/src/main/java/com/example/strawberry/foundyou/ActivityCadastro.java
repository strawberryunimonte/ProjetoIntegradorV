package com.example.strawberry.foundyou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Leonardo on 15/09/2016.
 */

public class ActivityCadastro extends AppCompatActivity {

    private EditText nomeUser, emailUser, senhaUser;
    private String cursoTipo;
    private FirebaseAuth auth;
    private Button cadastrar;
    private ImageView fotoUser;
    private Spinner cursos;
    private ProgressDialog dialog;
    private Curso curso;
    private byte[] bytesFoto;
    private Uri uriFotoCortada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        auth = FirebaseAuth.getInstance();
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
                cursoTipo = String.valueOf(adapterView.getItemAtPosition(i));
                String array[] = new String[3];

                if (!cursoTipo.equals("Escolha seu Curso")) {
                    array = cursoTipo.split("/");
                    curso = new Curso();
                    curso.setNomeCurso(array[0]);
                    curso.setTipoCurso(array[1]);
                    curso.setFotoCurso(pathFotoCurso(curso.getNomeCurso()));
                }
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
            Uri uriFotoDispositvo = CropImage.getPickImageResultUri(this, data);
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                bytesFoto = stream.toByteArray();
                uriFotoCortada = result.getUri();
            }

        } else {
            snackBar("Nenhuma foto selecionada.");
        }
    }

    public void validarCampos() {

        String nome = nomeUser.getText().toString().trim();
        String email = emailUser.getText().toString().trim();
        String senha = senhaUser.getText().toString().trim();

        if (nome.equals("") || email.equals("") || senha.equals("")) {
            snackBar("Por favor preencha todos os campos!");
            dialog.dismiss();
        } else if (senha.length() < 6) {
            snackBar("A senha deve contar o mínimo de 6 caracteres.");
            dialog.dismiss();
        } else if (cursoTipo.equals("Escolha seu Curso")) {
            snackBar("Por Favor selecione seu cursoTipo!");
            dialog.dismiss();
        } else {
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setCurso(curso.getNomeCurso());
            usuario.setEmail(email);
            usuario.setSenha(senha);
            criarUsuario(usuario, curso);
        }
    }

    public void criarUsuario(final Usuario usuario, final Curso curso) {

        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!verificaConexao()) {
                    snackBar("Verifique sua conexão com internet!");
                    dialog.dismiss();
                } else if (task.isSuccessful()) {

                    if (uriFotoCortada == null) {
                        usuario.setFoto("Sem Foto");
                        Context context = getBaseContext();
                        FacadeFirebaseServices facadeFirebaseServices = new FacadeFirebaseServices();
                        facadeFirebaseServices.iniciarServicosFirebase();
                        facadeFirebaseServices.salvarTokenBandcoDeDadosMySQL(usuario, context);
                        facadeFirebaseServices.salvarDadosRealTimeDataBase(usuario, curso);
                        dialog.dismiss();
                        preencheUsuarioAtualEChamaActivityBase(usuario);
                        Toast.makeText(ActivityCadastro.this, "Usuário Cadastrado com Sucesso!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Context context = getBaseContext();
                        FacadeFirebaseServices facadeFirebaseServices = new FacadeFirebaseServices();
                        facadeFirebaseServices.iniciarServicosFirebase();
                        facadeFirebaseServices.salvarDadosRealTimeDataBaseComFoto(usuario, curso, bytesFoto, uriFotoCortada, context);
                        dialog.dismiss();
                        preencheUsuarioAtualEChamaActivityBase(usuario);
                        Toast.makeText(ActivityCadastro.this, "Usuário Cadastrado com Sucesso!!", Toast.LENGTH_SHORT).show();
                    }

                } else if (!task.isSuccessful() && verificaConexao()) {
                    snackBar("Email de usuário já existe!");
                    dialog.dismiss();
                }
            }
        });
    }

    public void snackBar(String mensagem) {
        Snackbar.make(nomeUser, mensagem, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

    public int pathFotoCurso(String nomecurso) {

        int pathCurso = 0;

        if (("Administração").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_administracao);
        } else if (("Análise e Desenvolvimento de Sistemas").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_ads);
        } else if (("Arquitetura e Urbanismo").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_arquitetura_urbanismo);
        } else if (("Biomedicina").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_biomedicina);
        } else if (("Ciências Contábeis").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_ciencias_contabeis);
        } else if (("Cinema e Audiovisual").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_cinema_e_audiovisual);
        } else if (("Comércio Exterior").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_comercio_exterior);
        } else if (("Design").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_design);
        } else if (("Direito").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_direito);
        } else if (("Enfermagem").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_enfermagem);
        } else if (("Engenharia Ambiental").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_engenharia_ambiental);
        } else if (("Engenharia Civil").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_engenharia_civil);
        } else if (("Engenharia de Petróleo").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_engenharia_petroleo);
        } else if (("Engenharia de Produção").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_engenharia_producao);
        } else if (("Engenharia Mecânica").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_engenharia_mecanica);
        } else if (("Estética e Cosmética").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_estetica);
        } else if (("Gastronomia").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_gastronomia);
        } else if (("Geologia").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_geologia);
        } else if (("Gestão Ambiental").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_gestao_ambiental);
        } else if (("Gestão de Recursos Humanos").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_gestao_rh);
        } else if (("Gestão Portuária").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_gestao_portuaria);
        } else if (("Logística").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_logistica);
        } else if (("Medicina Veterinária").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_medicina_veterinaria);
        } else if (("Oceanografia").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_oceanografia);
        } else if (("Pedagogia").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_pedagogia);
        } else if (("Processos Gerenciais").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_processos_gerenciais);
        } else if (("Psicologia").equals(nomecurso)) {
            pathCurso = (R.drawable.foto_curso_psicologia);
        }

        return pathCurso;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(this,ActivityLogin.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    public void preencheUsuarioAtualEChamaActivityBase(Usuario usuario){
        ActivityBase.usuarioAtualNome = usuario.getNome();
        ActivityBase.usuarioAtualUid = usuario.getUid();
        ActivityBase.usuarioAtualFoto = usuario.getFoto();
        ActivityBase.usuarioAtualCurso = usuario.getCurso();

        Intent intent = new Intent(ActivityCadastro.this,ActivityBase.class);
        startActivity(intent);
    }

}
