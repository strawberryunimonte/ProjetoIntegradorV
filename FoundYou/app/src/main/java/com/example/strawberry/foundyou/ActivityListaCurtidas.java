package com.example.strawberry.foundyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.strawberry.foundyou.Dominio.Post;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderAluno;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityListaCurtidas extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos_curso);

        Intent intent = getIntent();
        String postCurtida = intent.getStringExtra("post_curtida");

        recyclerView = (RecyclerView) findViewById(R.id.listaAlunosCursos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("TimeLine").child(postCurtida).child("Curtidas "+postCurtida);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolderAluno>(Post.class, R.layout.list_item_usuario, ViewHolderAluno.class, reference) {

            @Override
            protected void populateViewHolder(ViewHolderAluno viewHolder, final Post model, int position) {

                viewHolder.nome_usuario.setText(model.getNomeUser());
            //    Glide.with(ActivityListaCurtidas.this).load(model.getFoto()).into(viewHolder.foto_usuario);


              //  viewHolder.setOnClickListener(new InterfaceClick() {
                 //   @Override
                 //   public void onClick(View view, int postion, boolean isLongClick) {
                    //    Toast.makeText(ActivityListaCurtidas.this,"O nome do aluno é :"+model.getNome(),Toast.LENGTH_SHORT).show();
                  //  }

              //  });


            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}
