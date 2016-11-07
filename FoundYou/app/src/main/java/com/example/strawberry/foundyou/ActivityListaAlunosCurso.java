package com.example.strawberry.foundyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

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

        Intent intent = getIntent();
        String nome_curso = intent.getStringExtra("nome_curso");
        System.out.println(nome_curso);

        recyclerView = (RecyclerView) findViewById(R.id.listaAlunosCursos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("Alunos").child(nome_curso);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Usuario, ViewHolderAluno>(Usuario.class, R.layout.list_item_usuario, ViewHolderAluno.class, reference) {

            @Override
            protected void populateViewHolder(ViewHolderAluno viewHolder, final Usuario model, int position) {

                viewHolder.nome_usuario.setText(model.getNome());
               

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        Toast.makeText(ActivityListaAlunosCurso.this,"O nome do aluno é :"+model.getNome(),Toast.LENGTH_SHORT).show();
                    }

                });


            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}
