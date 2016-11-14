package com.example.strawberry.foundyou.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;


/**
 * Created by Leonardo on 13/11/2016.
 */

public class ViewHolderComentarios extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    public TextView nome_usuario_comentario;
    public TextView curso_usuario_comentario;
    public TextView usuario_comentario;
    public TextView comentario_horario;
    public ImageView foto_usuario_comentario;
    private InterfaceClick itemClick;

    public ViewHolderComentarios(View itemView) {
        super(itemView);

        nome_usuario_comentario = (TextView)itemView.findViewById(R.id.nome_usuario_comnetario);
        comentario_horario = (TextView)itemView.findViewById(R.id.comentario_horario);
        curso_usuario_comentario = (TextView)itemView.findViewById(R.id.curso_usuario_comentario);
        usuario_comentario = (TextView)itemView.findViewById(R.id.comentario_usuario);
        foto_usuario_comentario = (ImageView)itemView.findViewById(R.id.foto_usuario_comentario);

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
