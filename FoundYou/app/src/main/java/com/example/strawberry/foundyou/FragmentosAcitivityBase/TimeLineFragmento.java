package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.strawberry.foundyou.ActivityBase;
import com.example.strawberry.foundyou.ActivityComentariosPost;
import com.example.strawberry.foundyou.ActivityListaCurtidas;
import com.example.strawberry.foundyou.Dominio.Post;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderTimeLine;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



/**
 * Created by Leonardo on 15/09/2016.
 */
public class TimeLineFragmento extends android.support.v4.app.Fragment {

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    private DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragmento_timeline, container, false);
        reference = FirebaseDatabase.getInstance().getReference().child("TimeLine");
        recyclerView = (RecyclerView) view.findViewById(R.id.listaTimeLine);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        vaiParaOUltimoPost();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolderTimeLine>(Post.class, R.layout.layout_lista_timeline, ViewHolderTimeLine.class, reference) {
            @Override
            protected void populateViewHolder(final ViewHolderTimeLine viewHolder, final Post model, final int position) {
                viewHolder.curtirBtn.setTextColor(Color.GRAY);
                viewHolder.comentarBtn.setTextColor(Color.GRAY);
                viewHolder.progressDialog.setVisibility(View.VISIBLE);
                viewHolder.nomeUserTxt.setText(model.getNomeUser());
                viewHolder.mensagemTxt.setText(model.getMensagem());
                viewHolder.dataTxt.setText(model.getHoraData());
                viewHolder.mensagemTxt.setText(model.getMensagem());
                viewHolder.localTxt.setText(model.getLocal());
                viewHolder.uid_user_post.setText(model.getUidUser());

                if ("".equals(model.getFotoUser())) {
                    viewHolder.fotoUser.setImageResource(R.drawable.ic_foto_default_perfil);
                }else if ("Sem Foto".equals(model.getFotoUser())){
                    viewHolder.fotoUser.setImageResource(R.drawable.ic_user_sem_foto);
                } else {
                    Glide.with(getContext()).load(model.getFotoUser()).centerCrop().into(viewHolder.fotoUser);
                }

                if ("".equals(model.getFoto())) {
                    viewHolder.fotoPost.setImageResource(R.drawable.marca_dagua);
                    viewHolder.progressDialog.setVisibility(View.GONE);

                } else {
                    Glide.with(getContext()).load(model.getFoto()).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
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
                }

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {

                    }
                });

              viewHolder.btnOpcoes.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      PopupMenu popupMenu = new PopupMenu(getActivity(),v);
                      popupMenu.inflate(R.menu.menu_popup);
                      popupMenu.show();
                      popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                          @Override
                          public boolean onMenuItemClick(MenuItem item) {
                              switch (item.getItemId()) {
                                  case R.id.denunciar:
                                      break;
                                  case R.id.deletar:
                                      if (model.getUidUser().equals(ActivityBase.usuarioAtualUid)){

                                          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                          builder.setTitle("Mensagem");
                                          builder.setMessage("Deseja deletar esta Postagem ?");
                                          builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                              public void onClick(DialogInterface dialog, int which) {
                                                  reference.child(model.getIdPost()).removeValue();
                                              };

                                          });
                                          builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                              @Override
                                              public void onClick(DialogInterface dialog, int which) {

                                              }
                                          });
                                          builder.setIcon(R.mipmap.ic_launcher);
                                          builder.show();
                                      }else {
                                          Toast.makeText(getActivity(),"Você não é o dono da postagem",Toast.LENGTH_SHORT).show();
                                      }
                                      break;
                                  case R.id.editar:
                                      break;
                              }
                              return false;
                          }
                      });

                  }
              });
                
                viewHolder.curtirBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (viewHolder.curtirBtn.getCurrentTextColor() == Color.GRAY) {
                            Post post = new Post();
                            post.setNomeUser(ActivityBase.usuarioAtualNome);
                            post.setUidUser(ActivityBase.usuarioAtualUid);
                            reference.child(model.getIdPost()).child("Curtidas " + model.getIdPost()).child(ActivityBase.usuarioAtualUid).setValue(post);
                            viewHolder.curtirBtn.setTextColor(Color.GREEN);
                        } else {
                            notifyItemChanged(position);
                            Post post = new Post();
                            post.setNomeUser(ActivityBase.usuarioAtualNome);
                            reference.child(model.getIdPost()).child("Curtidas " + model.getIdPost()).child(ActivityBase.usuarioAtualUid).removeValue();
                            viewHolder.curtirBtn.setTextColor(Color.GRAY);
                        }
                    }
                });

                viewHolder.contadorComentarios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActivityComentariosPost.class);
                        intent.putExtra("post_curtida", model.getIdPost());
                        startActivity(intent);
                    }
                });

                viewHolder.contadorCurtidas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActivityListaCurtidas.class);
                        intent.putExtra("post_curtida", model.getIdPost());
                        startActivity(intent);
                    }
                });

                viewHolder.comentarBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActivityComentariosPost.class);
                        intent.putExtra("post_curtida", model.getIdPost());
                        startActivity(intent);
                    }
                });

                Query query = reference.child(model.getIdPost()).child("Comentarios " + model.getIdPost());
                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getKey().equals("Comentarios " + model.getIdPost())){
                            if (dataSnapshot.getChildrenCount() == 1) {
                                viewHolder.contadorComentarios.setVisibility(View.VISIBLE);
                                viewHolder.contadorComentarios.setText(dataSnapshot.getChildrenCount() + " comentário");
                            } else if (dataSnapshot.getChildrenCount() >= 2) {
                                viewHolder.contadorComentarios.setVisibility(View.VISIBLE);
                                viewHolder.contadorComentarios.setText(dataSnapshot.getChildrenCount() + " comentários");
                            }else {
                                viewHolder.contadorCurtidas.setVisibility(View.VISIBLE);
                                viewHolder.contadorComentarios.setText("nenhum comentário");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                    reference.child(model.getIdPost()).child("Curtidas " + model.getIdPost()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getKey().equals("Curtidas " + model.getIdPost())) {
                                if (dataSnapshot.getChildrenCount() == 1) {
                                    viewHolder.contadorCurtidas.setVisibility(View.VISIBLE);
                                    viewHolder.contadorCurtidas.setText(dataSnapshot.getChildrenCount() + " pessoa curtiu");
                                } else if (dataSnapshot.getChildrenCount() >= 2) {
                                    viewHolder.contadorCurtidas.setVisibility(View.VISIBLE);
                                    viewHolder.contadorCurtidas.setText(dataSnapshot.getChildrenCount() + " pessoas curtiram");
                                } else {
                                    viewHolder.contadorCurtidas.setVisibility(View.VISIBLE);
                                    viewHolder.contadorCurtidas.setText("nenhuma curtida");
                                }
                            }

                            Query query = reference.child(model.getIdPost()).child("Curtidas " + model.getIdPost());
                            query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    if (dataSnapshot.getChildrenCount() >= 1) {
                                        DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                                        if (snapshot.getValue().equals(ActivityBase.usuarioAtualNome)) {
                                            viewHolder.curtirBtn.setTextColor(Color.GREEN);
                                        }
                                    }
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

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                }



        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.cleanup();
    }

    public void vaiParaOUltimoPost(){
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount()-1);
            }
        });
    }
}
