package com.example.strawberry.foundyou.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;


/**
 * Created by Leonardo on 26/09/2016.
 */

public class ViewHolderAluno extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    public TextView nome_usuario;
    private InterfaceClick itemClick;

    public ViewHolderAluno(View itemView) {
        super(itemView);

        nome_usuario = (TextView)itemView.findViewById(R.id.nome_usuario);

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
