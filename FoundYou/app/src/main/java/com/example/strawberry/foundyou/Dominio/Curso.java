package com.example.strawberry.foundyou.Dominio;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Leonardo on 03/10/2016.
 */
@IgnoreExtraProperties
public class Curso {

    String nomeCurso;
    String tipoCurso;

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public String getTipoCurso() {
        return tipoCurso;
    }

    public void setTipoCurso(String tipoCurso) {
        this.tipoCurso = tipoCurso;
    }
}
