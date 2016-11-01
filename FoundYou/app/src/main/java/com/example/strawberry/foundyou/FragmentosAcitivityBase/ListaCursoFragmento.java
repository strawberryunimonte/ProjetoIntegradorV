package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.strawberry.foundyou.Adapter.ExpandableAdapter;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ListaCursoFragmento extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragmento_cursos,container,false);

        //reference = FirebaseDatabase.getInstance().getReference().child("Cursos");
        reference = FirebaseDatabase.getInstance().getReference().child("Cursos").child("Análise e Desenvolvimento de Sistemas");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,String> map = (Map)dataSnapshot.getValue();
                String nomeCurso = map.get("nomeCurso");
                String tipoCurso = map.get("tipoCurso");
                Log.i("MainActivity", dataSnapshot.getKey());

                List<Curso> ListaTitulo = new ArrayList<>();

                for (int i = 0; i < 5; i++){
                    Curso ItemTitulo = new Curso(nomeCurso,tipoCurso);
                    ListaTitulo.add(ItemTitulo);
                }

                List<ParentListItem> ListaExpandable = new ArrayList<>();

                for (Curso item: ListaTitulo) {

                    List<Usuario> ListaSubtitulo = new ArrayList<>();

                    for (int i = 0; i < 5; i++){
                        ListaSubtitulo.add(new Usuario());
                    }

                    item.setmChildItemCurso(ListaSubtitulo);
                    ListaExpandable.add(item);
                }

                recyclerView.setAdapter(new ExpandableAdapter(getActivity(),ListaExpandable));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
