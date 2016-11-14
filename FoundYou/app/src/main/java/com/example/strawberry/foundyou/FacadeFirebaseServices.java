package com.example.strawberry.foundyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.FirebaseSuporte.AcessoHTTP;
import com.example.strawberry.foundyou.FirebaseSuporte.MyFirebaseInstanceIdService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Leonardo Menezes on 05/11/2016.
 */

public class FacadeFirebaseServices {
    protected FirebaseAuth auth;
    protected DatabaseReference reference;
    protected DatabaseReference reference2;
    protected StorageReference storageReference;
    protected FirebaseDatabase database;
    protected FirebaseStorage firebaseStorage;


    public void iniciarServicosFirebase(){
        auth  = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference2 = database.getReference().child("Alunos");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://foundyou-6ae4b.appspot.com");

    }

    public void salvarDadosRealTimeDataBase(Usuario usuario, Curso curso){
        usuario.setSenha(null);
        usuario.setUid(auth.getCurrentUser().getUid());
        reference.child("Cursos").child(usuario.getCurso()).setValue(curso);
        reference2.child(usuario.getCurso()).child(usuario.getUid()).setValue(usuario);
        reference.child("Usuarios").child(usuario.getUid()).setValue(usuario);
    }

    public void salvarDadosRealTimeDataBaseComFoto(final Usuario usuario, final Curso curso, byte[] bytesFoto, Uri uriFotoCortada,final Context context ){

        StorageReference storageReferenceIconeUser = storageReference.child("IconeUser").child(uriFotoCortada.getLastPathSegment());
        UploadTask uploadTask = storageReferenceIconeUser.putBytes(bytesFoto);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                usuario.setFoto(taskSnapshot.getDownloadUrl().toString());
                salvarTokenBandcoDeDadosMySQL(usuario,context);
                salvarDadosRealTimeDataBase(usuario,curso);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                System.out.println("Erro ao realizar upload da foto!");

            }
        });

    }

    public void salvarTokenBandcoDeDadosMySQL(final Usuario usuario,final Context context){
        final String UPLOAD_URL = "http://foundyou.esy.es/registrarUserToken.php";
        final String UPLOAD_KEY = "nome";
        final String UPLOAD_KEY1 = "token";
        final String UPLOAD_KEY2 = "uid";

        System.out.println("Passou por aqui!");

        new Thread(new Runnable() {
            @Override
            public void run() {
                AcessoHTTP requisicaoPost = new AcessoHTTP();
                MyFirebaseInstanceIdService.tinyDB = new TinyDB(context);
                String token = MyFirebaseInstanceIdService.tinyDB.getString("token");
                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, usuario.getNome());
                data.put(UPLOAD_KEY1,token);
                data.put(UPLOAD_KEY2, usuario.getUid());

                String result = requisicaoPost.sendPostRequest(UPLOAD_URL, data);

                System.out.println(result);
                System.out.println(token);

            }
        }).start();
    }



}
