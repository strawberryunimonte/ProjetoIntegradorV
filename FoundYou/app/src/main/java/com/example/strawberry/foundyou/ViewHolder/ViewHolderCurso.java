package com.example.strawberry.foundyou.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;

/**
 * Created by JeffersonGuardia on 04/10/2016.
 */

public class ViewHolderCurso extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mNomeCurso;
    private TextView mDescCurso;
    //private ImageView mImgCurso;
    private InterfaceClick mClickItem;

    public ViewHolderCurso(View itemView) {
        super(itemView);

        mNomeCurso = (TextView) itemView.findViewById(R.id.Txt_nmCurso);
        mDescCurso = (TextView) itemView.findViewById(R.id.Txt_DescCurso);
        //mImgCurso = (ImageView) itemView.findViewById(R.id.img_curso);

        itemView.setOnClickListener(this);

    }

    public TextView getmNomeCurso() {
        return mNomeCurso;
    }

    public void setmNomeCurso(TextView mNomeCurso) {
        this.mNomeCurso = mNomeCurso;
    }

    public TextView getmDescCurso() {
        return mDescCurso;
    }

    public void setmDescCurso(TextView mDescCurso) {
        this.mDescCurso = mDescCurso;
    }

    public  void setOnClickListener(InterfaceClick interfaceClick){
        this.mClickItem = interfaceClick;

    }
    @Override
    public void onClick(View v) {

        mClickItem.onClick(v,getPosition(),false);

    }


}
