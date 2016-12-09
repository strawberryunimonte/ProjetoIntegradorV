package com.example.strawberry.foundyou;

import android.app.Activity;
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
import android.widget.Toast;

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

import static com.example.strawberry.foundyou.ActivityChat.PREF;

public class ActivityBase extends AppCompatActivity {

    private ViewPager mViewPager;
    private PagerAdapter adapter;
    public static String usuarioAtualNome;
    public static String usuarioAtualFoto;
    public static String usuarioAtualUid;
    public static String usuarioAtualCurso;
    public static String UID_USUARIO_RECEPTOR;
    public static String NOME_USUARIO_RECEPTOR;
    public static String FOTO_USUARIO_RECEPTOR;


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
                switch (item.getItemId()) {

                    case R.id.action_settings :
                        Alerta();
                        break;
                    case R.id.botao_post :
                        Intent intent = new Intent(ActivityBase.this, ActivityPublicacao.class);
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(ActivityBase.this,"Opção inválida",Toast.LENGTH_SHORT).show();
                }
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
                    signOutEFecharActivityBaseLimpandoParametrosUsuarioAtual();

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
            negativo.setTextColor(getResources().getColor(R.color.verde));
            Button positivo = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            positivo.setTextColor(getResources().getColor(R.color.verde));
        }

        return true;
    }

    public void Alerta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBase.this);
        builder.setTitle("Mensagem");
        builder.setMessage("Deseja fazer o logout");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                signOutEFecharActivityBaseLimpandoParametrosUsuarioAtual();
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

    public void  preencheUsuarioAtual(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String uidUsuarioAtual = auth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        reference.child(uidUsuarioAtual).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActivityBase.usuarioAtualCurso = (dataSnapshot.getValue(Usuario.class).getCurso());
                ActivityBase.usuarioAtualNome = (dataSnapshot.getValue(Usuario.class).getNome());
                ActivityBase.usuarioAtualFoto = (dataSnapshot.getValue(Usuario.class).getFoto());
                ActivityBase.usuarioAtualUid = uidUsuarioAtual;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void signOutEFecharActivityBaseLimpandoParametrosUsuarioAtual(){
        usuarioAtualUid = null;
        usuarioAtualNome = null;
        usuarioAtualFoto = null;
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ActivityBase.this,ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
