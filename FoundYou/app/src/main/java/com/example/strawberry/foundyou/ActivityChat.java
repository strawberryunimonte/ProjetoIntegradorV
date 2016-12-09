package com.example.strawberry.foundyou;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.strawberry.foundyou.FirebaseSuporte.MyFirebaseMessagingService;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderChat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    public final static String PREF = "PREF";
    private Mensagem mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        verificaRecuperaDadosUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(ActivityBase.UID_USUARIO_RECEPTOR).child("ChatPrivado").child(ActivityBase.usuarioAtualUid);
        reference2 = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(ActivityBase.usuarioAtualUid).child("ChatPrivado").child(ActivityBase.UID_USUARIO_RECEPTOR);
        reference3 = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(ActivityBase.usuarioAtualUid).child("Conversas");
        reference4 = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(ActivityBase.UID_USUARIO_RECEPTOR).child("Conversas");

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
        toolbar_action.setNavigationIcon(R.drawable.ic_voltar);
        titulo.setText(ActivityBase.NOME_USUARIO_RECEPTOR);

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
                Intent intent = new Intent(ActivityChat.this,ActivityBase.class);
                startActivity(intent);
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
            usuario.setNome(ActivityBase.NOME_USUARIO_RECEPTOR);
            usuario.setMensagem(msg_conteudo);
            usuario.setFoto(ActivityBase.FOTO_USUARIO_RECEPTOR);
            usuario.setUid(ActivityBase.UID_USUARIO_RECEPTOR);

            // Objeto User1 -> Nó Conversas Usuário Receptor

            Usuario usuario1 = new Usuario();
            usuario1.setNome(ActivityBase.usuarioAtualNome);
            usuario1.setMensagem(msg_conteudo);
            usuario1.setFoto(ActivityBase.usuarioAtualFoto);
            usuario1.setUid(ActivityBase.usuarioAtualUid);

            if (ActivityBase.usuarioAtualUid.equals(ActivityBase.UID_USUARIO_RECEPTOR)) {
                reference.push().setValue(mensagem);
                reference3.child(ActivityBase.UID_USUARIO_RECEPTOR).setValue(usuario);
                reference4.child(ActivityBase.usuarioAtualUid).setValue(usuario1);
                pega_mensagem.setText("");
            } else {
                enviaNotificacao();
                reference.push().setValue(mensagem);
                reference2.push().setValue(mensagem);
                reference3.child(ActivityBase.UID_USUARIO_RECEPTOR).setValue(usuario);
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
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getSharedPreferences(PREF, MODE_PRIVATE).edit();
        editor.putString("UID_RECEPTOR_SHARED", ActivityBase.UID_USUARIO_RECEPTOR);
        editor.putString("NOME_USUARIO_RECEPTOR_SHARED", ActivityBase.NOME_USUARIO_RECEPTOR);
        editor.putString("NOME_USUARIO_ATUAL_SHARED", ActivityBase.usuarioAtualNome);
        editor.putString("UID_USUARIO_ATUAL_SHARED", ActivityBase.usuarioAtualUid);
        editor.apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
        reference.removeEventListener(childEventListener);
    }

    public void enviaNotificacao() {
        final String UPLOAD_URL = "http://foundyou.esy.es/push_notification.php";
        final String UPLOAD_KEY = "nome";
        final String UPLOAD_KEY1 = "mensagem";
        final String UPLOAD_KEY2 = "uid_receptor";
        final String UPLOAD_KEY3 = "uid_atual";

        new Thread(new Runnable() {
            @Override
            public void run() {
                AcessoHTTP rh = new AcessoHTTP();
                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, ActivityBase.usuarioAtualNome);
                data.put(UPLOAD_KEY1, mensagem.getMsg_conteudo());
                data.put(UPLOAD_KEY2, ActivityBase.UID_USUARIO_RECEPTOR);
                data.put(UPLOAD_KEY3, ActivityBase.usuarioAtualUid);
                String result = rh.sendPostRequest(UPLOAD_URL, data);
            }
        }).start();
    }

    public void verificaRecuperaDadosUser() {
        if (ActivityBase.usuarioAtualNome == null || ActivityBase.usuarioAtualUid == null || ActivityBase.UID_USUARIO_RECEPTOR == null || ActivityBase.NOME_USUARIO_RECEPTOR == null) {
            SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);
            ActivityBase.UID_USUARIO_RECEPTOR = preferences.getString("UID_RECEPTOR_SHARED", "");
            ActivityBase.NOME_USUARIO_RECEPTOR = preferences.getString("NOME_USUARIO_RECEPTOR_SHARED", "");
            ActivityBase.usuarioAtualNome = preferences.getString("NOME_USUARIO_ATUAL_SHARED", "");
            ActivityBase.usuarioAtualUid = preferences.getString("UID_USUARIO_ATUAL_SHARED", "");
        }
        if (MyFirebaseMessagingService.pref_notificacao != null) {
            MyFirebaseMessagingService.pref_notificacao.clear();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        limparSharedPreferecences();
    }

    public void limparSharedPreferecences(){
        SharedPreferences preferences = getSharedPreferences(PREF,MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}
