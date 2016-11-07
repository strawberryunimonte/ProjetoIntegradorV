package com.example.strawberry.foundyou.Dominio;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Leonardo on 03/10/2016.
 */

@IgnoreExtraProperties
public class Curso {

    private String nomeCurso;
    private String tipoCurso;
    private int fotoCurso;

    public Curso() {}

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

    public int getFotoCurso() {
        return fotoCurso;
    }

    public void setFotoCurso(int fotoCurso) {
        this.fotoCurso = fotoCurso;
    }
}


