package com.example.nstagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nstagram.Adapter.YorumAdapter;
import com.example.nstagram.Model.Kullanici;
import com.example.nstagram.Model.Yorum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YorumlarActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private YorumAdapter yorumAdapter;
    private List<Yorum> yorumListesi;
    EditText edt_yorum_ekle;
    ImageView profil_resmi;
    TextView txt_gonder;
    String gonderiId;
    String gonderenId;

    FirebaseUser mevcutKullanici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yorumlar);

        Toolbar toolbar = findViewById(R.id.toolbar_yorumlarActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yorumlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView =findViewById(R.id.recyler_view_yorumlarActivity);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        yorumListesi = new ArrayList<>();
        yorumAdapter= new YorumAdapter(this,yorumListesi);
        recyclerView.setAdapter(yorumAdapter);


        edt_yorum_ekle=findViewById(R.id.edt_yorumEkle_yorumlarActivity);
        profil_resmi=findViewById(R.id.profil_resmi_yorumlarActivity);
        txt_gonder=findViewById(R.id.txt_gonder_yorumlarActivity);

        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();

        gonderiId =intent.getStringExtra("gonderiId");
        gonderenId =intent.getStringExtra("gonderenId");
        
        txt_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_yorum_ekle.getText().toString().equals(""))
                {
                    Toast.makeText(YorumlarActivity.this,"Boş yorum gönderemezsiniz...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                 yorumEkle();
                }
            }
        });
        //METOTLARI ÇAĞIRMA
        resimAl();
        yorumlarıOku();
    }
    private void yorumEkle(){
        // Burada 'gonderiId' kullanmanız gerekiyor, 'gonderenId' değil.
        DatabaseReference yorumYolu = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yorum", edt_yorum_ekle.getText().toString());
        hashMap.put("gonderen", mevcutKullanici.getUid());

        yorumYolu.push().setValue(hashMap);
        //Yorum ekleme metodu
        bildirimleriEkle();

        edt_yorum_ekle.setText("");
    }
    private void resimAl()
    {
        DatabaseReference resimAlmaYolu =FirebaseDatabase.getInstance().getReference("Kullanıcılar")
                .child(mevcutKullanici.getUid());
        resimAlmaYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(profil_resmi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void yorumlarıOku()
    {
        DatabaseReference yorumlarıOkumaYolu = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);
        yorumlarıOkumaYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                yorumListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Yorum yorum = snapshot.getValue(Yorum.class);
                    yorumListesi.add(yorum);

                }
                yorumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void bildirimleriEkle()
    {
        DatabaseReference bildirimEklemeYolu = FirebaseDatabase.getInstance().getReference("Bildirimler").child(gonderiId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciid",mevcutKullanici.getUid());
        hashMap.put("text","yorum yaptı: "+edt_yorum_ekle.getText().toString());
        hashMap.put("gonderiid",gonderiId);
        hashMap.put("ispost",true);

        bildirimEklemeYolu.push().setValue(hashMap);
    }
}