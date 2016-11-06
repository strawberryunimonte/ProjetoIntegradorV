package com.example.strawberry.foundyou.Dominio;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.List;

/**
 * Created by Leonardo on 03/10/2016.
 */

@IgnoreExtraProperties
public class Curso implements ParentListItem {

    private String nomeCurso;
    private String tipoCurso;
    private List<Usuario> mChildItemCurso;


    public Curso(String nomeCurso, String tipoCurso) {
        this.nomeCurso = nomeCurso;
        this.tipoCurso = tipoCurso;
    }

    public Curso() {
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public String getTipoCurso() {
        return tipoCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public void setTipoCurso(String tipoCurso) {
        this.tipoCurso = tipoCurso;
    }

    @Override
    public List<Usuario> getChildItemList() {
        return mChildItemCurso;
    }

    public void setmChildItemCurso(List<Usuario> mChildItemCurso) {
        this.mChildItemCurso = mChildItemCurso;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
