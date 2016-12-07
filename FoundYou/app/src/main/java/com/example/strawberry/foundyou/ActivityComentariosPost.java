package com.example.strawberry.foundyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.strawberry.foundyou.Dominio.Post;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderComentarios;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityComentariosPost extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private Toolbar toolbar;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios_post);

        Intent intent = getIntent();
        String refComentarios = intent.getStringExtra("post_curtida");

        recyclerView = (RecyclerView) findViewById(R.id.listaComentarios);
        toolbar = (Toolbar)findViewById(R.id.toolbar_privado);
        editText = (EditText)findViewById(R.id.comentario);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("TimeLine").child(refComentarios).child("Comentarios "+refComentarios);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolderComentarios>(Post.class, R.layout.list_item_comentarios, ViewHolderComentarios.class, reference) {

            @Override
            protected void populateViewHolder(ViewHolderComentarios viewHolder, final Post model, int position) {
                viewHolder.nome_usuario_comentario.setText(model.getNomeUser());
                viewHolder.curso_usuario_comentario.setText(model.getCurso());
                viewHolder.usuario_comentario.setText(model.getMensagem());
                viewHolder.comentario_horario.setText(model.getHoraData());

                if (model.getFotoUser().equals("Sem Foto")){
                    viewHolder.foto_usuario_comentario.setImageResource(R.drawable.ic_user_sem_foto);
                }else{
                    Glide.with(ActivityComentariosPost.this).load(model.getFotoUser()).into(viewHolder.foto_usuario_comentario);
                }

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        ActivityBase.NOME_USUARIO_RECEPTOR = model.getNomeUser();
                        ActivityBase.UID_USUARIO_RECEPTOR = model.getUidUser();
                        ActivityBase.FOTO_USUARIO_RECEPTOR = model.getFotoUser();
                        Intent intent1 = new Intent(ActivityComentariosPost.this, ActivityChat.class);
                        startActivity(intent1);

                    }

                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.enviar:
                        comentar();
                        break;
                }

                return true;
            }
        });

        toolbar.inflateMenu(R.menu.toolbar_comentarios);
    }

    public void comentar(){

        if (editText.getText().toString().trim().equals("")){
            Toast.makeText(this,"Por favor digite alguma mensagem",Toast.LENGTH_SHORT).show();
        }else {
            Post post = new Post();
            post.setNomeUser(ActivityBase.usuarioAtualNome);
            post.setCurso(ActivityBase.usuarioAtualCurso);
            post.setFotoUser(ActivityBase.usuarioAtualFoto);
            post.setHoraData(dataAtual());
            post.setMensagem(editText.getText().toString());
            String referencia = reference.push().getKey();
            reference.child(referencia).setValue(post);
        }
    }

    public String dataAtual(){
        String dataHora;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'ás' HH:mm");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        dataHora = dateFormat.format(date);

        return dataHora;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.cleanup();
    }
}

