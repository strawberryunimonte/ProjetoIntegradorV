package com.example.strawberry.foundyou.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;

/**
 * Created by LeonardoMenezes on 16/11/2016.
 */

public class ViewHolderChat extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView nome, mensagem, hora_msg, data, nome1, mensagem1, hora_msg2, data1;
    private InterfaceClick itemClick;

    public ViewHolderChat(View itemView) {
        super(itemView);

        nome = (TextView) itemView.findViewById(R.id.nome_user_chat);
        mensagem = (TextView) itemView.findViewById(R.id.mensagem_chat);
        hora_msg = (TextView) itemView.findViewById(R.id.hora_msg);
        data = (TextView) itemView.findViewById(R.id.data);
        nome1 = (TextView) itemView.findViewById(R.id.nome2);
        mensagem1 = (TextView) itemView.findViewById(R.id.mensagem2);
        hora_msg2 = (TextView) itemView.findViewById(R.id.hora_msg1);
        data1 = (TextView) itemView.findViewById(R.id.data1);

    }

        public void setOnClickListener(InterfaceClick interfaceClick){
            this.itemClick = interfaceClick;
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(v,getPosition(),false);
        }

        public boolean onLongClick(View v) {
            itemClick.onClick(v,getPosition(),true);
            return true;
        }

}
