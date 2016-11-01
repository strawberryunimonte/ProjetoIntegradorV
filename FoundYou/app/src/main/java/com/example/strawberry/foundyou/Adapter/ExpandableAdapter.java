package com.example.strawberry.foundyou.Adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.R;

import java.util.List;

/**
 * Created by JeffersonGuardia on 01/11/2016.
 */

public class ExpandableAdapter extends ExpandableRecyclerAdapter<ExpandableAdapter.MyParentItem,
        ExpandableAdapter.MyChildItem> {

    private LayoutInflater mInflater;
    private Handler handler;


    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */


    //region Construtor
    public ExpandableAdapter(Context context, List<ParentListItem> parentItemList) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
    }
    //endregion

    //region Inflando o Header (Titulo Principal)
    @Override
    public MyParentItem onCreateParentViewHolder(ViewGroup parentViewGroup) {

        View view = mInflater.inflate(R.layout.list_item_curso,parentViewGroup,false);
        return new MyParentItem(view);
    }
    //endregion

    //region Inflando o Child (Sub Titulo)
    @Override
    public MyChildItem onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.list_item_usuario,childViewGroup,false);
        return new MyChildItem(view);
    }
    //endregion

    //region Populando o Header (Titulo Principal)
    @Override
    public void onBindParentViewHolder(final MyParentItem viewHolder, int position, final ParentListItem model) {

        final Curso mCursoParentItem = (Curso) model;
        handler = new Handler();


        new Thread(new Runnable() {
            @Override
            public void run() {
                final String NomeCurso = mCursoParentItem.getNomeCurso();
                final String descCurso = mCursoParentItem.getTipoCurso();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.Txt_nmCurso.setText(NomeCurso);
                        viewHolder.Txt_DescCurso.setText(descCurso);
                    }
                });
            }
        }).start();

    }
    //endregion

    //region Populando o Child (Sub Titulo)
    @Override
    public void onBindChildViewHolder(MyChildItem childViewHolder, int position, Object childListItem) {
        Usuario mCursoChildItem = (Usuario) childListItem;
        childViewHolder.nome_usuario.setText(mCursoChildItem.nome);
    }
    //endregion

    //region Classe interna para associar os componentes do Parentview
    class MyParentItem extends ParentViewHolder {

        TextView Txt_nmCurso,Txt_DescCurso;

        MyParentItem(View itemView) {
            super(itemView);

            Txt_nmCurso = (TextView) itemView.findViewById(R.id.Txt_nmCurso);
            Txt_DescCurso = (TextView) itemView.findViewById(R.id.Txt_DescCurso);

        }
    }
    //endregion

    //region Classe interna para associar os componentes do ChildView
    class MyChildItem extends ChildViewHolder {

        TextView nome_usuario;

        MyChildItem(View itemView) {
            super(itemView);
            nome_usuario = (TextView) itemView.findViewById(R.id.nome_usuario);
        }
    }
    //endregion
}
