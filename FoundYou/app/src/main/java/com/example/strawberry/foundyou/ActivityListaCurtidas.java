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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ActivityListaCurtidas extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos_curso);

        final Intent intent = getIntent();
        String postCurtida = intent.getStringExtra("post_curtida");

        recyclerView = (RecyclerView) findViewById(R.id.listaAlunosCursos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("TimeLine").child(postCurtida).child("Curtidas " + postCurtida);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolderAluno>(Post.class, R.layout.list_item_usuario, ViewHolderAluno.class, reference) {

            @Override
            protected void populateViewHolder(final ViewHolderAluno viewHolder, final Post model, int position) {
                viewHolder.nome_usuario.setText(model.getNomeUser());
                Glide.with(ActivityListaCurtidas.this).load(model.getFotoUser()).into(viewHolder.foto_usuario);

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        ActivityBase.NOME_USUARIO_RECEPTOR = model.getNomeUser();
                        ActivityBase.UID_USUARIO_RECEPTOR = model.getUidUser();
                        Intent intent1 = new Intent(ActivityListaCurtidas.this, ActivityChat.class);
                        startActivity(intent1);
                    }


                });
            }


        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
