package com.br.opet.inter;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class SaloesProfissional extends AppCompatActivity {


    private FirebaseAuth auth;
    private ListView listarSalao;
    private FirebaseFirestore db;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

    private List<ObjSalao> listSalao = new ArrayList<ObjSalao>();
    private ArrayAdapter<ObjSalao> arrayAdapterSalao;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userLogado = user.getUid();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saloes_profissional);
        auth = FirebaseAuth.getInstance();
        iniciarFirebase();
        eventoDatabase();

        listarSalao = findViewById(R.id.listSalao);

        listarSalao.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ObjSalao itemSelecionado = arrayAdapterSalao.getItem(position);

                referencia.child("Salao").child(userLogado).child(itemSelecionado.getUid()).removeValue();
            alerta("Salao" + itemSelecionado.getNome()+"Deletado com sucesso!");

                return false;
            }
        });

        listarSalao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ObjSalao itemSelecionado = arrayAdapterSalao.getItem(position);

                Intent i = new Intent(SaloesProfissional.this, SalaoSelecionado.class);
                i.putExtra("Uid", itemSelecionado.getUid());
                i.putExtra("senhaChamada",itemSelecionado.getNumero());
                i.putExtra("Nome", itemSelecionado.toString());
                startActivity(i);
            }
        });

    }

    private void eventoDatabase() {
        referencia.child("Salao").child(userLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listSalao.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    ObjSalao s = objSnapshot.getValue(ObjSalao.class);
                    listSalao.add(s);

                }
                arrayAdapterSalao = new ArrayAdapter<ObjSalao>(SaloesProfissional.this,
                        android.R.layout.simple_list_item_1, listSalao);
                listarSalao.setAdapter(arrayAdapterSalao);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void iniciarFirebase() {

        db = FirebaseFirestore.getInstance();
        FirebaseApp.initializeApp(SaloesProfissional.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        referencia = firebaseDatabase.getReference();
    }

    private void alerta(String msg) {
        Toast.makeText(SaloesProfissional.this, msg, Toast.LENGTH_SHORT).show();
    }

}
