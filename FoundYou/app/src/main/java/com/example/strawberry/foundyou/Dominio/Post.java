package com.example.strawberry.foundyou.Dominio;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Leonardo on 26/09/2016.
 */

@IgnoreExtraProperties
public class Post {

    String nomeUser;
    String fotoUser;
    String idPost;
    String local;
    String foto;
    String horaData;
    String mensagem;
    String curtida;
    String curso;


    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getHoraData() {
        return horaData;
    }

    public void setHoraData(String horaData) {
        this.horaData = horaData;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCurtida() {
        return curtida;
    }

    public void setCurtida(String curtida) {
        this.curtida = curtida;
    }

    public String getFotoUser() {
        return fotoUser;
    }

    public void setFotoUser(String fotoUser) {
        this.fotoUser = fotoUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}
