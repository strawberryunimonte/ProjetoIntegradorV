package com.example.strawberry.foundyou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener ;
    private String email;
    private String senha;
    private EditText edtNomeLogin,edtSenhaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btnLimparLogin = (Button) findViewById(R.id.btnCadastrar);
        Button btnLogarLogin = (Button) findViewById(R.id.btnLogarLogin);
        edtNomeLogin = (EditText) findViewById(R.id.edtNomeLogin);
        edtSenhaLogin = (EditText) findViewById(R.id.edtSenhaLogin);

        btnLimparLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogin.this, ActivityCadastro.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtNomeLogin.getText().toString().trim();
                senha = edtSenhaLogin.getText().toString().trim();
                if(isEmpty(edtNomeLogin) || (isEmpty(edtSenhaLogin))){
                    snackBar("Preencha todos os campos");
                }else{
                    LoginFirebaseAuth(email,senha);
                }
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    Toast.makeText(ActivityLogin.this, "Bem vindo: " + firebaseUser.getEmail(),
                            Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(ActivityLogin.this,ActivityBase.class);
                    startActivity(it);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void LoginFirebaseAuth(String email, String senha){

        mFirebaseAuth.signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!verificaConexao()){
                            snackBar("Verificar a conexão com a internet");
                        } else if(task.isSuccessful()) {

                            Intent it = new Intent(ActivityLogin.this,ActivityBase.class);
                            startActivity(it);
                            finish();
                        } else if (!task.isSuccessful()){
                            snackBar("Dados Incorretos");
                        }
                    }


                });
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    public boolean verificaConexao() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) if (networkInfo.isConnectedOrConnecting()) return true;
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aviso");
            builder.setMessage("Deseja encerrar o aplicativo ?");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            Button negativo = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativo.setTextColor(Color.GRAY);
            Button positivo = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            positivo.setTextColor(Color.GRAY);
        }
        return true;
    }

    public void snackBar(String mensagem) {
        Snackbar.make(edtNomeLogin, mensagem, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}
