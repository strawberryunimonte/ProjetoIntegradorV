package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.strawberry.foundyou.R;

/**
 * Created by Leonardo on 15/09/2016.
 */
public class TimeLineFragmento extends android.support.v4.app.Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragmento_timeline,container,false);

        Toast.makeText(getContext(),"Olá",Toast.LENGTH_LONG).show();

        return view;
    }
}
