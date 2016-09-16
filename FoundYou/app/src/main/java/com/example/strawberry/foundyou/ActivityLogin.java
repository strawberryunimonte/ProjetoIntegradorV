package com.example.strawberry.foundyou;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityLogin extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private String email;
    private String senha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btnLimparLogin = (Button) findViewById(R.id.btnLimparLogin);
        Button btnLogarLogin = (Button) findViewById(R.id.btnLogarLogin);
        final EditText edtNomeLogin = (EditText) findViewById(R.id.edtNomeLogin);
        final EditText edtSenhaLogin = (EditText) findViewById(R.id.edtSenhaLogin);



        btnLimparLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtNomeLogin.setText("");
                edtSenhaLogin.setText("");
                edtNomeLogin.requestFocus();
            }
        });

        btnLogarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = edtNomeLogin.getText().toString();
                senha = edtSenhaLogin.getText().toString();

                if(isEmpty(edtNomeLogin) || (isEmpty(edtSenhaLogin))){
                    Toast.makeText(ActivityLogin.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{

                    LoginFirebaseAuth(email,senha);
                }
            }
        });


    }

    public void LoginFirebaseAuth(String email, String senha){

        mFirebaseAuth.signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            Toast.makeText(ActivityLogin.this, "Login feito com sucesso",
                                    Toast.LENGTH_SHORT).show();

                            Intent it = new Intent(ActivityLogin.this,ActivityBase.class);
                            startActivity(it);

                        }else if (!task.isSuccessful()){
                            Toast.makeText(ActivityLogin.this, "Login Falhou!", Toast.LENGTH_SHORT).show();
                        }
                    }


                });
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }
}
