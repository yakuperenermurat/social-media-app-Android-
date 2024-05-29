package com.example.nstagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nstagram.Adapter.KullaniciAdapter;
import com.example.nstagram.Model.Kullanici;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TakipcilerActivity extends AppCompatActivity {

    String id;
    String baslik;

    List<String> idListesi;
    RecyclerView recyclerView;
    KullaniciAdapter kullaniciAdapter;
    List<Kullanici> kullaniciListesi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takipciler);

        Intent intent =getIntent();
        id = intent.getStringExtra("id");
        baslik = intent.getStringExtra("baslik");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_takipciler);
        Toolbar toolbar =findViewById(R.id.toolbar_TakipcilerActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(baslik);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Recyler ayarları
        recyclerView = findViewById(R.id.recyler_view_TakipcilerActivity);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        kullaniciListesi = new ArrayList<>();
        kullaniciAdapter = new KullaniciAdapter(this,kullaniciListesi,false);
        recyclerView.setAdapter(kullaniciAdapter);

        idListesi = new ArrayList<>();

        if ("begeniler".equals(baslik)) {
            begenileriAl();
        } else if ("takip edilenler".equals(baslik)) {
            takipEdilenleriAl();
        } else if ("takipçiler".equals(baslik)) {
            takipcileriAl();
        }

    }

    private void begenileriAl() {
        DatabaseReference begeniYolu = FirebaseDatabase.getInstance().getReference("Begeniler").child(id);

        begeniYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    idListesi.add(snapshot.getKey());
                }
                kullanicilariGoster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void takipEdilenleriAl()
    {
        DatabaseReference takipEdilenlerYolu = FirebaseDatabase.getInstance().getReference("Takip").child(id).child("Takip Edilenler");
        takipEdilenlerYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idListesi.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    idListesi.add(snapshot.getKey());
                }
                kullanicilariGoster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void takipcileriAl()
    {
        DatabaseReference takipciYolu = FirebaseDatabase.getInstance().getReference("Takip").child(id).child("Takipciler");
        takipciYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    idListesi.add(snapshot.getKey());
                }
                kullanicilariGoster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void kullanicilariGoster ()
    {
        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanıcılar");
        kullaniciYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kullaniciListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Kullanici kullanici =snapshot.getValue(Kullanici.class);
                    for (String id : idListesi)
                    {
                        if (kullanici.getId().equals(id))
                        {
                            kullaniciListesi.add(kullanici);
                        }
                    }
                }

                kullaniciAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}