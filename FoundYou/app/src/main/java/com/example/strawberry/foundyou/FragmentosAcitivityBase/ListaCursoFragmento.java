package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.strawberry.foundyou.Adapter.ExpandableAdapter;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ListaCursoFragmento extends Fragment {

    private RecyclerView recyclerView;
    private ExpandableAdapter mExpandableAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragmento_cursos,container,false);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cursos");

        //region RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //endregion

        reference.addValueEventListener(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,String> map = (Map)dataSnapshot.getValue();
                final Set<String> nomeCurso = map.keySet();
                final List<ParentListItem> ListaExpandable = new ArrayList<>();
                final List<Usuario> ListaSubtitulo = new ArrayList<>();

                String tipoCurso = "Exatas";

                //region Titulo Principal

                for (String TituloCurso: nomeCurso) {

                    Curso ItemTitulo = new Curso(TituloCurso,tipoCurso);


                    /*for (int i = 0; i < 5; i++){
                        ListaSubtitulo.add(new Usuario());
                    }*/

                    ItemTitulo.setmChildItemCurso(ListaSubtitulo);
                    ListaExpandable.add(ItemTitulo);
                }
                //endregion

                mExpandableAdapter = new ExpandableAdapter(getActivity(),ListaExpandable);

                mExpandableAdapter.setExpandCollapseListener
                        (new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                    @Override
                    public void onListItemExpanded(int position) {

                        Curso c = (Curso) ListaExpandable.get(position);

                        reference.child(c.getNomeCurso()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot teste : dataSnapshot.getChildren()) {
                                        String teste01 = (String) teste.child("nome").getValue();
                                        String teste02 = (String) teste.child("curso").getValue();

                                    if(teste01 != null && teste02 != null){

                                        Usuario usuario = new Usuario(teste01,teste02);
                                        ListaSubtitulo.add(usuario);

                                        Toast.makeText(getActivity(),
                                                "Aluno :" + teste01 + " Curso :" + teste02,
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //endregion
                    }

                    @Override
                    public void onListItemCollapsed(int position) {

                        Toast.makeText(getActivity(),"Colapse",Toast.LENGTH_SHORT).show();
                    }
                });

                recyclerView.setAdapter(mExpandableAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return view;
    }
}
