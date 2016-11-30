package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.strawberry.foundyou.ActivityBase;
import com.example.strawberry.foundyou.ActivityChat;
import com.example.strawberry.foundyou.ActivityListaAlunosCurso;
import com.example.strawberry.foundyou.ActivityListaCurtidas;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderConversasPrivado;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderCurso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class ListaConvesasFragmento extends Fragment {

    private DatabaseReference reference;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView aviso;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragmento_conversas, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(auth.getCurrentUser().getUid()).child("Conversas");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.lista_conversas);
        aviso = (TextView) rootView.findViewById(R.id.info_usuario);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new FirebaseRecyclerAdapter<Usuario, ViewHolderConversasPrivado>(Usuario.class, R.layout.lista_conversas_chat_privado, ViewHolderConversasPrivado.class, reference) {
            @Override
            protected void populateViewHolder(final ViewHolderConversasPrivado viewHolder, final Usuario model, final int position) {
                viewHolder.nome.setText(model.getNome());
                viewHolder.mensagem.setText(model.getMensagem());
                Glide.with(getActivity()).load(model.getFoto()).centerCrop().into(viewHolder.foto_perfil_chat_privado);

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (isLongClick) {
                            Toast.makeText(getContext(), "Foi clique longo : " + position + " " + model.getNome(), Toast.LENGTH_SHORT).show();
                        } else {
                            ActivityBase.NOME_USUARIO_RECEPTOR = model.getNome();
                            ActivityBase.UID_USUARIO_RECEPTOR = model.getUid();
                            Intent intent = new Intent(getActivity(), ActivityChat.class);
                            startActivity(intent);
                        }
                    }
                });
                if (adapter.getItemCount() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    aviso.setVisibility(View.VISIBLE);
                }
            }
        };
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup(); // limpa o adapater quando passa pelo metodo onDestroy.
    }


}
