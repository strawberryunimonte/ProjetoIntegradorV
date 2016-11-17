package com.example.strawberry.foundyou.Dominio;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ThrowOnExtraProperties;

/**
 * Created by LeonardoMenezes on 16/06/2016.
 */
@IgnoreExtraProperties
public class Mensagem {


    public String msg_conteudo;
    public String msg_nome;
    public String user_uid;
    public String msg_hora;
    public String msg_data;


    public Mensagem(){};


    public String getMsg_nome() {
        return msg_nome;
    }

    public void setMsg_nome(String msg_nome) {
        this.msg_nome = msg_nome;
    }

    public String getMsg_conteudo() {
        return msg_conteudo;
    }

    public void setMsg_conteudo(String msg_conteudo) {
        this.msg_conteudo = msg_conteudo;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getMsg_hora() {
        return msg_hora;
    }

    public void setMsg_hora(String msg_hora) {
        this.msg_hora = msg_hora;
    }

    public String getMsg_data() {
        return msg_data;
    }

    public void setMsg_data(String msg_data) {
        this.msg_data = msg_data;
    }

}
