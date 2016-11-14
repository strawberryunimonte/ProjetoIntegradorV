package com.example.strawberry.foundyou.FragmentosAcitivityBase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.strawberry.foundyou.ActivityListaAlunosCurso;
import com.example.strawberry.foundyou.Dominio.Curso;
import com.example.strawberry.foundyou.Interfaces.InterfaceClick;
import com.example.strawberry.foundyou.R;
import com.example.strawberry.foundyou.ViewHolder.ViewHolderCurso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ListaCursoFragmento extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cursos");
    static final String TAG = "SymmetricAlgorithmAES";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragmento_cursos, container, false);

        //region RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //endregion

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Curso, ViewHolderCurso>(Curso.class, R.layout.list_item_curso, ViewHolderCurso.class, reference) {

            @Override
            protected void populateViewHolder(ViewHolderCurso viewHolder, final Curso model, int position) {

                viewHolder.setOnClickListener(new InterfaceClick() {
                    @Override
                    public void onClick(View view, int postion, boolean isLongClick) {
                        Intent intent = new Intent(getActivity(), ActivityListaAlunosCurso.class);
                        intent.putExtra("nome_curso",model.getNomeCurso());
                        intent.putExtra("foto_curso",model.getFotoCurso());
                        startActivity(intent);
                    }
                });

                viewHolder.mNomeCurso.setText(model.getNomeCurso());
                viewHolder.mDescCurso.setText(model.getTipoCurso());
                viewHolder.mImgCurso.setImageResource(model.getFotoCurso());

                //EncriptionTest(model);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    public void EncriptionTest(Curso curso){


        System.out.println("Esse é o Curso a ser criptografado:  "+curso.getNomeCurso());

        // Set up secret key spec for 128-bit AES encryption and decryption
        SecretKey secretKey = null;

        try{
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed("any data used as random seed".getBytes());

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128,secureRandom);
            secretKey = new SecretKeySpec((keyGenerator.generateKey()).getEncoded(),"AES");

        }catch (Exception e){
            Log.e(TAG, "AES secret key spec error");
        }


        // Encode the original data with AES
        byte[] encodeBytes = null;

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            encodeBytes = cipher.doFinal(curso.getNomeCurso().getBytes());
        }catch (Exception e){
            Log.e(TAG, "AES encryption error");
        }

        //TextView tvEncoded = (TextView) findViewById(R.id.tEncoded);
        System.out.println("[ENCODED]:\n" + Base64.encodeToString(encodeBytes,Base64.DEFAULT)+ "\n");



        // Decode the encoded data with AES

        byte[] decodeBytes = null;

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            decodeBytes = cipher.doFinal(encodeBytes);
        }catch (Exception e){
            Log.e(TAG, "AES decryption error");
        }

        //TextView tvDecoded = (TextView) findViewById(R.id.tvDecoded);
        System.out.println("[DECODED]:\n" + new String(decodeBytes)+ "\n");


    }

}
