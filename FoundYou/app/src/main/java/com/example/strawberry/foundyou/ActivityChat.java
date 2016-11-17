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
import android.widget.TextView;
import android.widget.Toast;

import com.example.strawberry.foundyou.Dominio.Mensagem;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.FirebaseSuporte.AcessoHTTP;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderChat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ActivityChat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText pega_mensagem;
    private Toolbar toolbar_chat, toolbar_action;
    private TextView titulo;
    private DatabaseReference reference, reference2, reference3, reference4;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter adapter;
    private ChildEventListener childEventListener;
    private String data_atual, hora_atual;
    public static final String UPLOAD_URL = "http://foundyou.esy.es/push_notification.php";
    public static final String UPLOAD_KEY = "nome";
    public static final String UPLOAD_KEY1 = "mensagem";
    public static final String UPLOAD_KEY2 = "uid_receptor";
    public static final String UPLOAD_KEY3 = "uid_atual";
    private Mensagem mensagem;
    private String uidUsuarioReceptor;
    private String nomeUsuarioReceptor;
    private String fotoUsuarioReceptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        uidUsuarioReceptor =  intent.getStringExtra("uid_usuario_receptor");
        nomeUsuarioReceptor =  intent.getStringExtra("nome_usuario_receptor");
        fotoUsuarioReceptor = intent.getStringExtra("foto_usuario_receptor");

        if (ActivityBase.usuarioAtualNome == null || ActivityBase.usuarioAtualUid == null || ActivityBase.usuarioAtualFoto == null || ActivityBase.usuarioAtualCurso == null){
            preencheUsuarioAtual();
        }
        reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uidUsuarioReceptor).child("ChatPrivado").child(ActivityBase.usuarioAtualUid);
        reference2 = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(ActivityBase.usuarioAtualUid).child("ChatPrivado").child(uidUsuarioReceptor);
        reference3 = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(ActivityBase.usuarioAtualUid).child("Conversas");
        reference4 = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uidUsuarioReceptor).child("Conversas");

        pega_mensagem = (EditText) findViewById(R.id.mensagem_privado);
        titulo = (TextView) findViewById(R.id.titulo);
        toolbar_chat = (Toolbar) findViewById(R.id.toolbar_privado);
        toolbar_action = (Toolbar) findViewById(R.id.toolbar_action);
        recyclerView = (RecyclerView) findViewById(R.id.lista_chat_privado);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        setSupportActionBar(toolbar_action);
        toolbar_action.setNavigationIcon(R.mipmap.ic_launcher);
        titulo.setText(nomeUsuarioReceptor);

        adapter = new FirebaseRecyclerAdapter<Mensagem, ViewHolderChat>(Mensagem.class, R.layout.lista_chat_privado, ViewHolderChat.class, reference) {

            @Override
            protected void populateViewHolder(ViewHolderChat viewHolder, Mensagem model, int position) {

                if (ActivityBase.usuarioAtualNome.equals(model.getMsg_nome())) {

                    viewHolder.nome.setText(model.getMsg_nome());
                    viewHolder.mensagem.setText(model.getMsg_conteudo());
                    viewHolder.hora_msg.setText(model.getMsg_hora());
                    viewHolder.data.setText(model.getMsg_data());
                    viewHolder.nome.setVisibility(View.VISIBLE);
                    viewHolder.mensagem.setVisibility(View.VISIBLE);
                    viewHolder.hora_msg.setVisibility(View.VISIBLE);
                    viewHolder.data.setVisibility(View.VISIBLE);
                    viewHolder.nome1.setVisibility(View.INVISIBLE);
                    viewHolder.mensagem1.setVisibility(View.INVISIBLE);
                    viewHolder.hora_msg2.setVisibility(View.INVISIBLE);
                    viewHolder.data1.setVisibility(View.INVISIBLE);

                } else {

                    viewHolder.nome1.setText(model.getMsg_nome());
                    viewHolder.mensagem1.setText(model.getMsg_conteudo());
                    viewHolder.hora_msg2.setText(model.getMsg_hora());
                    viewHolder.data1.setText(model.getMsg_data());
                    viewHolder.nome1.setVisibility(View.VISIBLE);
                    viewHolder.mensagem1.setVisibility(View.VISIBLE);
                    viewHolder.hora_msg2.setVisibility(View.VISIBLE);
                    viewHolder.data1.setVisibility(View.VISIBLE);
                    viewHolder.nome.setVisibility(View.INVISIBLE);
                    viewHolder.mensagem.setVisibility(View.INVISIBLE);
                    viewHolder.hora_msg.setVisibility(View.INVISIBLE);
                    viewHolder.data.setVisibility(View.INVISIBLE);

                }

            }

        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                scrollUltimoItem();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference3.addChildEventListener(childEventListener);



        toolbar_action.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ActivityChat.this, ActivityBase.class);
                startActivity(intent);
                finish();

            }
        });

        toolbar_chat.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.enviar:

                        enviarMensagem();
                        scrollUltimoItem();

                        break;

                }

                return true;
            }
        });
        toolbar_chat.inflateMenu(R.menu.menu_toolbar_chat);



    }



    public void enviarMensagem() {



        if (pega_mensagem.getText().toString().trim().equals("")) {

            Toast.makeText(this, "Por favor digite uma mensagem.", Toast.LENGTH_SHORT).show();

        } else {

            String msg_conteudo = pega_mensagem.getText().toString().trim();

            // Data e Hora atual.

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            hora_atual = dateFormat.format(date);
            data_atual = dateFormat1.format(date);

            // Objeto Mensagem

            mensagem = new Mensagem();
            mensagem.setMsg_conteudo(msg_conteudo);
            mensagem.setMsg_nome(ActivityBase.usuarioAtualNome);
            mensagem.setMsg_hora(hora_atual);
            mensagem.setMsg_data(data_atual);

            // Objeto User -> Nó Conversas Usuário Atual

            Usuario usuario = new Usuario();
            usuario.setNome(nomeUsuarioReceptor);
            usuario.setMensagem(msg_conteudo);
            usuario.setFoto(fotoUsuarioReceptor);
            usuario.setUid(uidUsuarioReceptor);

            // Objeto User1 -> Nó Conversas Usuário Receptor

            Usuario usuario1 = new Usuario();
            usuario1.setNome(ActivityBase.usuarioAtualNome);
            usuario1.setMensagem(msg_conteudo);
            usuario1.setFoto(ActivityBase.usuarioAtualFoto);
            usuario1.setUid(ActivityBase.usuarioAtualUid);


            if (ActivityBase.usuarioAtualUid.equals(uidUsuarioReceptor)) {

                reference.push().setValue(mensagem);
                reference3.child(uidUsuarioReceptor).setValue(usuario);
                reference4.child(ActivityBase.usuarioAtualUid).setValue(usuario1);
                pega_mensagem.setText("");

            } else {

                enviaNotificacao();
                reference.push().setValue(mensagem);
                reference2.push().setValue(mensagem);
                reference3.child(uidUsuarioReceptor).setValue(usuario);
                reference4.child(ActivityBase.usuarioAtualUid).setValue(usuario1);
                pega_mensagem.setText("");

            }

        }

    }

    public void scrollUltimoItem() {

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(adapter.getItemCount());

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
        reference.removeEventListener(childEventListener);
    }

    public void enviaNotificacao() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                AcessoHTTP rh = new AcessoHTTP();

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, ActivityBase.usuarioAtualNome);
                data.put(UPLOAD_KEY1, mensagem.getMsg_conteudo());
                data.put(UPLOAD_KEY2, uidUsuarioReceptor);
                data.put(UPLOAD_KEY3, ActivityBase.usuarioAtualUid);

                String result = rh.sendPostRequest(UPLOAD_URL, data);

            }

        }).start();

    }
    public void   preencheUsuarioAtual(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String uidUsuarioAtual = auth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        reference.child(uidUsuarioAtual).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActivityBase.usuarioAtualCurso = (dataSnapshot.getValue(Usuario.class).getCurso());
                ActivityBase.usuarioAtualNome = (dataSnapshot.getValue(Usuario.class).getNome());
                ActivityBase.usuarioAtualFoto = (dataSnapshot.getValue(Usuario.class).getFoto());
                ActivityBase.usuarioAtualUid = uidUsuarioAtual;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}
