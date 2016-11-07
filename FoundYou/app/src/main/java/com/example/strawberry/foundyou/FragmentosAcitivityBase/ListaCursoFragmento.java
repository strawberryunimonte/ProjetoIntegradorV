package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.strawberry.foundyou.ActivityListaAlunosCurso;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderCurso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListaCursoFragmento extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cursos");

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragmento_cursos, container, false);

        //region RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //endregion

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Curso, ViewHolderCurso>(Curso.class, R.layout.list_item_curso, ViewHolderCurso.class, reference) {

            @Override
            protected void populateViewHolder(ViewHolderCurso viewHolder, final Curso model, int position) {

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        Intent intent = new Intent(getActivity(), ActivityListaAlunosCurso.class);
                        intent.putExtra("nome_curso",model.getNomeCurso());
                        startActivity(intent);
                    }
                });

                viewHolder.mNomeCurso.setText(model.getNomeCurso());
                viewHolder.mDescCurso.setText(model.getTipoCurso());
                viewHolder.mImgCurso.setImageResource(model.getFotoCurso());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
    }
}
