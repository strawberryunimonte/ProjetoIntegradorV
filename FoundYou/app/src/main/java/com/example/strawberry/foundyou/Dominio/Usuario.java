package com.example.strawberry.foundyou.Dominio;


import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Leonardo on 12/09/2016.
 */

@IgnoreExtraProperties
public class Usuario {

    private String senha;
    private String email;
    private String foto;
    private String curso;
    private String uid;
    private String status;
    private String nome;


    public Usuario (){
    }

    public Usuario(String nome, String curso) {
        this.nome = nome;
        this.curso = curso;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
