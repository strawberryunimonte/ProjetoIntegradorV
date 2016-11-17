package com.example.strawberry.foundyou;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.strawberry.foundyou.Dominio.Usuario;
import com.example.strawberry.foundyou.FragmentosAcitivityBase.ListaConvesasFragmento;
import com.example.strawberry.foundyou.FragmentosAcitivityBase.ListaCursoFragmento;
import com.example.strawberry.foundyou.FragmentosAcitivityBase.TimeLineFragmento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityBase extends AppCompatActivity {

    private ViewPager mViewPager;
    private PagerAdapter adapter;
    public static String usuarioAtualNome;
    public static String usuarioAtualFoto;
    public static String usuarioAtualUid;
    public static String usuarioAtualCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

       if (usuarioAtualNome == null || usuarioAtualUid == null || usuarioAtualFoto == null || usuarioAtualCurso == null){
         preencheUsuarioAtual();
       }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Alerta();
                return true;
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("CURSOS"));
        tabLayout.addTab(tabLayout.newTab().setText("TIMELINE"));
        tabLayout.addTab(tabLayout.newTab().setText("CONVERSAS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setCurrentItem(1);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Funcionalidades do Menu serão inseridas aqui.

        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        int numeroTelas;

        public SectionsPagerAdapter(FragmentManager fm, int numeroTelas) {
            super(fm);
            this.numeroTelas = numeroTelas;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ListaCursoFragmento();
                case 1:
                    return new TimeLineFragmento();
                case 2:
                    return  new ListaConvesasFragmento();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numeroTelas;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aviso");
            builder.setMessage("Deseja encerrar o aplicativo ?");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    finish();

                }

            });

            builder.setNegativeButton("Nao", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            Button negativo = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativo.setTextColor(Color.GRAY);
            Button positivo = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            positivo.setTextColor(Color.GRAY);

        }

        return true;

    }

    public void Alerta(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBase.this);
        builder.setTitle("Mensagem");
        builder.setMessage("Deseja fazer o logout");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ActivityBase.this,ActivityLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            };

        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setIcon(R.mipmap.ic_launcher);
        builder.show();
    }

    public void   preencheUsuarioAtual(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String uidUsuarioAtual = auth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        reference.child(uidUsuarioAtual).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println( ActivityBase.usuarioAtualCurso = (dataSnapshot.getValue(Usuario.class).getCurso()));
                System.out.println(   ActivityBase.usuarioAtualNome = (dataSnapshot.getValue(Usuario.class).getNome()));
                System.out.println(    ActivityBase.usuarioAtualFoto = (dataSnapshot.getValue(Usuario.class).getFoto()));
                System.out.println(   ActivityBase.usuarioAtualUid = uidUsuarioAtual);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
