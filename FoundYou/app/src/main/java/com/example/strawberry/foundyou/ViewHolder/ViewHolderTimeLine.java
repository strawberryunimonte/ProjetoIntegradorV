package com.example.strawberry.foundyou.ViewHolder;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;


/**
 * Created by Leonardo on 26/09/2016.
 */

public class ViewHolderTimeLine extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    public TextView nomeUserTxt,dataTxt, mensagemTxt,localTxt,idPost,contadorCurtidas;
    public ImageView fotoPost,fotoUser;
    public Button curtirBtn,comentarBtn;
    public ProgressBar progressDialog;
    private InterfaceClick itemClick;

    public ViewHolderTimeLine(View itemView) {
        super(itemView);

        nomeUserTxt = (TextView)itemView.findViewById(R.id.nomeUserTxt);
        contadorCurtidas = (TextView)itemView.findViewById(R.id.contador_curtidas);
        idPost = (TextView)itemView.findViewById(R.id.id_post);
        progressDialog = (ProgressBar) itemView.findViewById(R.id.progressBarFotoPost);
        dataTxt = (TextView)itemView.findViewById(R.id.dataTxt);
        mensagemTxt = (TextView)itemView.findViewById(R.id.mensagemTxt);
        localTxt = (TextView)itemView.findViewById(R.id.localTxt);
        fotoPost = (ImageView)itemView.findViewById(R.id.fotoPost);
        fotoUser = (ImageView)itemView.findViewById(R.id.fotoUser);
        curtirBtn = (Button)itemView.findViewById(R.id.curtirBtn);
        comentarBtn = (Button)itemView.findViewById(R.id.comentarBtn);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setOnClickListener(InterfaceClick interfaceClick){
        this.itemClick = interfaceClick;
    }

    @Override
    public void onClick(View v) {
        itemClick.onClick(v,getPosition(),false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClick.onClick(v,getPosition(),true);
        return true;
    }

}
