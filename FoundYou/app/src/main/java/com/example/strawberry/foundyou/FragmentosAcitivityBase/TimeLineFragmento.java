package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.strawberry.foundyou.ActivityPublicacao;
import com.example.strawberry.foundyou.Dominio.Post;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderTimeLine;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Leonardo on 15/09/2016.
 */
public class TimeLineFragmento extends android.support.v4.app.Fragment {


    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragmento_timeline, container, false);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("TimeLine");
        recyclerView = (RecyclerView)view.findViewById(R.id.listaTimeLine);
        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.btnPostar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        handler = new Handler();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityPublicacao.class);
                startActivity(intent);
            }
        });




        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolderTimeLine>(Post.class,R.layout.layout_lista_timeline,ViewHolderTimeLine.class,reference ) {
            @Override
            protected void populateViewHolder(final ViewHolderTimeLine viewHolder, final Post model, int position) {

                viewHolder.progressDialog.setVisibility(View.VISIBLE);

          viewHolder.setOnClickListener(new InterfaceClick() {
              @Override
              public void onClick(View view, int postion, boolean isLongClick) {

                  Toast.makeText(getActivity(),"Quem postou esta foto foi o "+model.getNomeUser(),Toast.LENGTH_SHORT).show();
              }
          });

            new Thread(new Runnable() {
                @Override
                public void run() {

                    final String nomeUser = model.getNomeUser();
                    final String fotoUser = model.getFotoUser();
                    final String data = model.getHoraData();
                    final String mensagem = model.getMensagem();
                    final String local = model.getLocal();
                    final String foto = model.getFoto();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            viewHolder.nomeUserTxt.setText(nomeUser);
                            viewHolder.mensagemTxt.setText(mensagem);
                            viewHolder.dataTxt.setText(data);
                            viewHolder.mensagemTxt.setText(mensagem);
                            viewHolder.localTxt.setText(local);





                                Glide.with(getContext()).load(foto).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        viewHolder.progressDialog.setVisibility(View.GONE);

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        viewHolder.progressDialog.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(viewHolder.fotoPost);


                            Glide.with(getContext()).load(fotoUser).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(viewHolder.fotoUser);

                        }
                    });

                }

            }).start();

            }

        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

        return view;

    }
}
