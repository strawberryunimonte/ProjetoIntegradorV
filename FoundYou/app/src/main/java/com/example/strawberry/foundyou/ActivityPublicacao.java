package com.example.strawberry.foundyou;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.strawberry.foundyou.Dominio.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityPublicacao extends Activity {

    private EditText edtMensagem;
    private ImageView imgFotoPost;
    private Button btnPostar;
    private RadioGroup radioGroup;
    private Spinner spinner;
    private DatabaseReference reference;
    private byte[] bytesFoto;
    private Post post;
    private Uri uriFotoCortada;
    private StorageReference storageReferenceRaiz;
    private String local;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacao);

        reference = FirebaseDatabase.getInstance().getReference().child("TimeLine");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReferenceRaiz = storage.getReferenceFromUrl("gs://foundyou-6ae4b.appspot.com");
        edtMensagem = (EditText) findViewById(R.id.edtMensagem);
        imgFotoPost = (ImageView) findViewById(R.id.imgFotoPost);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        spinner = (Spinner) findViewById(R.id.spinnerLocal);
        btnPostar = (Button) findViewById(R.id.btnPostar);
        radioGroup.check(R.id.radioPrivadoBtn);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.BlocosFacul, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

       spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               local = (String.valueOf(parent.getItemAtPosition(position)));
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

            imgFotoPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(ActivityPublicacao.this);
            }
        });

        btnPostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(ActivityPublicacao.this);
                dialog.setMessage("Publicando...");
                dialog.setCancelable(false);
                dialog.show();
                validarDados();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
            Uri uriFotoDispositvo = CropImage.getPickImageResultUri(this, data);
            CropImage.activity(uriFotoDispositvo).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                imgFotoPost.setImageURI(result.getUri());
                imgFotoPost.setDrawingCacheEnabled(true);
                imgFotoPost.buildDrawingCache();
                Bitmap bitmap = imgFotoPost.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                bytesFoto = stream.toByteArray();
                uriFotoCortada = result.getUri();
            }

        } else {
            snackBar("Nenhuma foto selecionada.");
        }
    }

    public void snackBar(String mensagem) {
        Snackbar.make(edtMensagem, mensagem, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void validarDados() {

        int idRadioButton = radioGroup.getCheckedRadioButtonId();

        if (edtMensagem.getText().toString().trim().equals("") && uriFotoCortada == null) {
            snackBar("Por favor escreva uma mensagem ou insira uma foto em sua postagem !");
            dialog.dismiss();
        }else if(edtMensagem.length() == 0 && uriFotoCortada != null){
            Toast.makeText(ActivityPublicacao.this,"Por favor comente algo sobre a foto !",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }else {
            post = new Post();
            if (idRadioButton == R.id.radioPrivadoBtn) {
                post.setNomeUser("Anônimo");
                post.setFotoUser("");
            } else  {
                post.setNomeUser(ActivityBase.usuarioAtualNome);
                post.setFotoUser(ActivityBase.usuarioAtualFoto);
            }

            if (local.equals("Adicionar local")) {
                post.setLocal("");
            } else  {
                post.setLocal(local);
            }

            if (edtMensagem.length() > 0 && uriFotoCortada != null) {
                post.setMensagem(edtMensagem.getText().toString().trim());
                uploadFotoPost();
            } else {
                post.setMensagem(edtMensagem.getText().toString().trim());
                post.setFoto("");
                salvarDadosFirebase();
            }
        }

    }

    public void uploadFotoPost(){
        StorageReference referenceFotosPost = storageReferenceRaiz.child("FotosPost").child(uriFotoCortada.getLastPathSegment());
        UploadTask uploadTask = referenceFotosPost.putBytes(bytesFoto);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                post.setFoto(taskSnapshot.getDownloadUrl().toString());
                salvarDadosFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                snackBar("Erro ao realizar upload da foto!");
            }
        });
    }

    public void salvarDadosFirebase(){
        post.setHoraData(dataAtual());
        post.setUidUser(ActivityBase.usuarioAtualUid);
        post.setIdPost(reference.push().getKey());
        reference.child(post.getIdPost()).setValue(post);
        reference.child(post.getIdPost()).child("Curtidas "+post.getIdPost()).setValue("default");
        reference.child(post.getIdPost()).child("Comentarios "+post.getIdPost()).setValue("default");
        dialog.dismiss();
        finish();
    }

    public String dataAtual(){
        String dataHora;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'ás' HH:mm");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        dataHora = dateFormat.format(date);

        return dataHora;
    }

}
