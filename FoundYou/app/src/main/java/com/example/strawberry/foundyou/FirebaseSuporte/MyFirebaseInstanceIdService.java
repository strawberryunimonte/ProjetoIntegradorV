package com.example.strawberry.foundyou.FirebaseSuporte;

import com.example.strawberry.foundyou.TinyDB;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Leonardo on 13/09/2016.
 */

public class MyFirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {


    public static TinyDB tinyDB;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        tinyDB = new TinyDB(this);
        tinyDB.putString("token",token);

    }
}
