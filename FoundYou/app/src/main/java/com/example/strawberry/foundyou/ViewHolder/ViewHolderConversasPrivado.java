package com.example.strawberry.foundyou.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;


/**
 * Created by Leonardo on 20/06/2016.
 * Classe responsavél por pegar a View dos itens da lista de Chat Privado.
 */

public class ViewHolderConversasPrivado extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView nome, mensagem;
    public ImageView foto_perfil_chat_privado;
    private InterfaceClick itemClick;


    public ViewHolderConversasPrivado(View itemView) {
        super(itemView);

        nome = (TextView) itemView.findViewById(R.id.nome_user_chat);
        mensagem = (TextView) itemView.findViewById(R.id.mensagem_chat);
        foto_perfil_chat_privado = (ImageView) itemView.findViewById(R.id.foto_chat_user);

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
