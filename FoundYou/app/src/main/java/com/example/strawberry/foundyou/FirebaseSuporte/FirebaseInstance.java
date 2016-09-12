package com.example.strawberry.foundyou.FirebaseSuporte;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Leonardo on 12/09/2016.
 * Classe Responsavél por pegar uma instância do Firebase para todo aplicativo,
 * também persiste os dados mesmo estando offline.
 */

public class FirebaseInstance extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.getApps(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
