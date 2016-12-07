package com.example.strawberry.foundyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderAluno;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityListaAlunosCurso extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos_curso);

        ImageView ImgCurso_ilustracao = (ImageView) findViewById(R.id.ImgCurso_ilustracao);

        Intent intent = getIntent();
        String nome_curso = intent.getStringExtra("nome_curso");
        int foto_curso = intent.getIntExtra("foto_curso",0);

        ImgCurso_ilustracao.setImageResource(foto_curso);

        recyclerView = (RecyclerView) findViewById(R.id.listaAlunosCursos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("Alunos").child(nome_curso);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Usuario, ViewHolderAluno>
                (Usuario.class, R.layout.list_item_usuario, ViewHolderAluno.class, reference) {

            @Override
            protected void populateViewHolder(ViewHolderAluno viewHolder,
                                              final Usuario model, int position) {

                viewHolder.nome_usuario.setText(model.getNome());
                viewHolder.email_usuario.setText(model.getEmail());

                if(model.getFoto().equals("Sem Foto")){
                    viewHolder.foto_usuario.setImageResource(R.drawable.ic_user_sem_foto);
                }else{
                    Glide.with(ActivityListaAlunosCurso.this).load(model.getFoto())
                            .into(viewHolder.foto_usuario);
                }

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        ActivityBase.NOME_USUARIO_RECEPTOR = model.getNome();
                        ActivityBase.UID_USUARIO_RECEPTOR = model.getUid();
                        ActivityBase.FOTO_USUARIO_RECEPTOR = model.getFoto();
                        Intent intent1 = new Intent(ActivityListaAlunosCurso.this, ActivityChat.class);
                        startActivity(intent1);
                        finish();
                    }

                });


            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
