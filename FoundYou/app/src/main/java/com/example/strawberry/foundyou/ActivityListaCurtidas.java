package com.example.strawberry.foundyou;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ActivityListaCurtidas extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference reference,reference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_curtidas);

        Intent intent = getIntent();
        String postCurtida = intent.getStringExtra("post_curtida");

        recyclerView = (RecyclerView) findViewById(R.id.listaAlunosCursos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("TimeLine").child(postCurtida).child("Curtidas " + postCurtida);
        reference2 = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolderAluno>(Post.class, R.layout.list_item_usuario_curtida, ViewHolderAluno.class, reference) {

            @Override
            protected void populateViewHolder(final ViewHolderAluno viewHolder, final Post model, int position) {
                viewHolder.nome_usuario.setText(model.getNomeUser());
                viewHolder.uid_usuario.setText(model.getUidUser());


                    Query query = reference2.child(model.getUidUser());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String url_foto = dataSnapshot.getValue(Usuario.class).getFoto();
                            if ("Sem Foto".equals(url_foto)){
                                viewHolder.foto_usuario.setImageResource(R.drawable.ic_user_sem_foto);
                            }else{
                                Glide.with(ActivityListaCurtidas.this).load(url_foto).into(viewHolder.foto_usuario);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        ActivityBase.NOME_USUARIO_RECEPTOR = model.getNomeUser();
                        ActivityBase.UID_USUARIO_RECEPTOR = model.getUidUser();
                        ActivityBase.FOTO_USUARIO_RECEPTOR = model.getFotoUser();
                        Intent intent1 = new Intent(ActivityListaCurtidas.this, ActivityChat.class);
                        startActivity(intent1);
                    }


                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
