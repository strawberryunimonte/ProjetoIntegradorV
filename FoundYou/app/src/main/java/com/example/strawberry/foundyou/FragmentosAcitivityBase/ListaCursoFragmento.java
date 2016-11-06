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
    private final List<ParentListItem> ListaExpandable = new ArrayList<>();
    private final List<Usuario> ListaSubtitulo = new ArrayList<>();
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cursos");
    private final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Alunos");
    private Curso cursoGlobal;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragmento_cursos,container,false);

        //region RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //endregion


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                cursoGlobal = dataSnapshot.getValue(Curso.class);
                ListaExpandable.add(cursoGlobal);


                mExpandableAdapter = new ExpandableAdapter(getActivity(),ListaExpandable);

                mExpandableAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                    @Override
                    public void onListItemExpanded(int position) {
                        ListListener(position, ListaExpandable);
                    }

                    @Override
                    public void onListItemCollapsed(int position) {
                        Toast.makeText(getActivity(), "Testou", Toast.LENGTH_SHORT).show();

                    }
                });

                recyclerView.setAdapter(mExpandableAdapter);

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
        });

        return view;
    }


    public void ListListener(int position, List<ParentListItem> lista){
        cursoGlobal = (Curso) lista.get(position);

        reference2.child(cursoGlobal.getNomeCurso()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                System.out.println(dataSnapshot);
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                ListaSubtitulo.add(usuario);

                cursoGlobal.setmChildItemCurso(ListaSubtitulo);



                Toast.makeText(getActivity(), "Expandiu "+ ListaSubtitulo.get(0).getNome() , Toast.LENGTH_SHORT).show();
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
        });
    }


}
