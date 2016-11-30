package com.example.strawberry.foundyou.FirebaseSuporte;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.strawberry.foundyou.ActivityBase;
import com.example.strawberry.foundyou.ActivityChat;
import com.example.strawberry.foundyou.R;
import com.example.strawberry.foundyou.TinyDB;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonardo on 17/11/2016.
 * Classe Responsavél pela verificação do recebimento de Push Notification e por apresentar a notificação
 * para o usuário.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 0;
    private ArrayList<String> linhas;
    private String nova_mensagem;
    public static TinyDB pref_notificacao;


    public Intent intent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        ActivityBase.NOME_USUARIO_RECEPTOR = (remoteMessage.getData().get("usuario"));
        ActivityBase.UID_USUARIO_RECEPTOR = (remoteMessage.getData().get("uid_atual"));


        linhas = new ArrayList<>();
        pref_notificacao = new TinyDB(this);
        linhas = pref_notificacao.getListString("notificacao");

        if (linhas.size() >= 1) {
            nova_mensagem = "Novas Mensagens";
        } else {
            nova_mensagem = "Nova Mensagem";
        }

        linhas.add(remoteMessage.getData().get("usuario") + " : " + remoteMessage.getData().get("mensagem"));

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle().setSummaryText(linhas.size() + " " + nova_mensagem);

        for (int i = 0; i < linhas.size(); i++) {
            style.addLine(linhas.get(i));
        }

        long[] vibrar = {0, 100, 200, 350};
        intent = new Intent(this, ActivityChat.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notificacao = new NotificationCompat.Builder(this)
                .setContentTitle("Found You")
                .setContentText(linhas.size() + " " + nova_mensagem)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setVibrate(vibrar)
                .setStyle(style)
                .setContentIntent(contentIntent)
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificacao);

        pref_notificacao.putListString("notificacao", linhas);

    }

}
