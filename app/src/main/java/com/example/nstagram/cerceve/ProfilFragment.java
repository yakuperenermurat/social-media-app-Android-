package com.example.nstagram.cerceve;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nstagram.Adapter.FotografAdapter;
import com.example.nstagram.Model.Gonderi;
import com.example.nstagram.Model.Kullanici;
import com.example.nstagram.ProfilDuzenleActivity;
import com.example.nstagram.R;
import com.example.nstagram.SeceneklerActivity;
import com.example.nstagram.TakipcilerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ProfilFragment extends Fragment {

    ImageView resimSeçenekler,profil_resmi;
    TextView txt_gonderiler , txt_takipçiler,txt_takipEdilenler,txt_Ad,txt_bio,txt_kullaniciAdi;
    Button btn_profili_Duzenle;
    ImageButton imagebtn_Fotograflarim, imagebtn_kaydedilenFotograflar;

    private List<String> kaydettiklerim;

    //Kaydettiğim gönderi recyler
    RecyclerView recyclerViewKaydettiklerim;
    private List<Gonderi> gonderiList_kaydettiklerim;

    FotografAdapter fotografAdapterKaydettiklerim;

    //Fotograflari profilde gorme recyler
    RecyclerView recyclerViewFotograflar;
    FotografAdapter fotografAdapter;
    List<Gonderi> gonderiList;


    FirebaseUser mevcutKullanici;
    String profilId;
    public ProfilFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        profilId = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE).getString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        resimSeçenekler = view.findViewById(R.id.resimSecenekler_profilCercevesi);
        profil_resmi = view.findViewById(R.id.profil_resmi_profilCercevesi);

        txt_gonderiler=view.findViewById(R.id.txt_gonderiler_profilCercevesi);
        txt_takipçiler=view.findViewById(R.id.txt_takipciler_profilCercevesi);
        txt_takipEdilenler=view.findViewById(R.id.txt_takipcEdilenler_profilCercevesi);
        txt_bio=view.findViewById(R.id.txt_bio_profilCercevesi);
        txt_Ad=view.findViewById(R.id.txt_ad_profilCercevesi);
        txt_kullaniciAdi=view.findViewById(R.id.txt_kullaniciadi_profilCerceve);

        btn_profili_Duzenle = view.findViewById(R.id.btn_profiliDuzenle_profilCecevesi);

        imagebtn_Fotograflarim =view.findViewById(R.id.imagebtn_fotograflarim_profilCercevesi);
        imagebtn_kaydedilenFotograflar =view.findViewById(R.id.imagebtn_kaydedilenFotograflarim_profilCercevesi);

        //Fotografları profilde görme recyleri
        recyclerViewFotograflar = view.findViewById(R.id.recyler_view_profilCercevesi);
        recyclerViewFotograflar.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerViewFotograflar.setLayoutManager(linearLayoutManager);
        gonderiList = new ArrayList<>();
        fotografAdapter = new FotografAdapter(getContext(),gonderiList);
        recyclerViewFotograflar.setAdapter(fotografAdapter);

        //Kaydettiğim gönderi recyler
        recyclerViewKaydettiklerim = view.findViewById(R.id.recyler_view_kaydet_profilCercevesi);
        recyclerViewKaydettiklerim.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_Kaydettiklerim = new GridLayoutManager(getContext(),3);
        recyclerViewKaydettiklerim.setLayoutManager(linearLayoutManager_Kaydettiklerim);
        gonderiList_kaydettiklerim = new ArrayList<>();
        fotografAdapterKaydettiklerim = new FotografAdapter(getContext(),gonderiList_kaydettiklerim);
        recyclerViewKaydettiklerim.setAdapter(fotografAdapterKaydettiklerim);

        recyclerViewFotograflar.setVisibility(View.VISIBLE);
        recyclerViewKaydettiklerim.setVisibility(View.GONE);

        //Metotları çağır
        kullaniciBilgisiAl();
        takipcileriAl();
        gonderiSayisiAl();
        fotograflarim();
        kaydettiklerim();


        if(profilId.equals(mevcutKullanici.getUid()))
        {
            btn_profili_Duzenle.setText("Profili Düzenle");
        }
        else
        {
            takipKontrolu();
            imagebtn_kaydedilenFotograflar.setVisibility(View.GONE);
        }
        btn_profili_Duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = btn_profili_Duzenle.getText().toString();
                if (btn.equals("Profili Düzenle"))
                {
                    //profili düzenlemeye gitsin
                    startActivity(new Intent(getContext(), ProfilDuzenleActivity.class));
                }
                else if (btn.equals("takip et"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("Takip Edilenler").child(profilId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("Takipciler").child(mevcutKullanici.getUid()).setValue(true);
                    bildirimleriEkle();
                }
                else if (btn.equals("takip ediliyor"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("Takip Edilenler").child(profilId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("Takipciler").child(mevcutKullanici.getUid()).removeValue();
                }
            }
        });

        //Seçenekler sayfasına git
        resimSeçenekler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SeceneklerActivity.class);
                startActivity(intent);
            }
        });

        imagebtn_Fotograflarim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewFotograflar.setVisibility(View.VISIBLE);
                recyclerViewKaydettiklerim.setVisibility(View.GONE);
            }
        });

        imagebtn_kaydedilenFotograflar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewFotograflar.setVisibility(View.GONE);
                recyclerViewKaydettiklerim.setVisibility(View.VISIBLE);
            }
        });
        txt_takipçiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(getContext(), TakipcilerActivity.class);
              intent.putExtra("id",profilId);
              intent.putExtra("baslik","takipçiler");
              startActivity(intent);
            }
        });
        txt_takipEdilenler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),TakipcilerActivity.class);
                intent.putExtra("id",profilId);
                intent.putExtra("baslik","takip edilenler");
                startActivity(intent);
            }
        });
        return view;

    }
    private void kullaniciBilgisiAl()
    {
        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(profilId);
        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);

                    Glide.with(getContext()).load(kullanici.getResimurl()).into(profil_resmi);
                    txt_kullaniciAdi.setText(kullanici.getKullaniciadi());
                    txt_Ad.setText(kullanici.getAd());
                    txt_bio.setText(kullanici.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void takipKontrolu()
    {
        DatabaseReference takipYolu = FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                .child("Takip Edilenler");
        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profilId).exists())
                {
                    btn_profili_Duzenle.setText("takip ediliyor");
                }
                else
                {
                    btn_profili_Duzenle.setText("takip et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void takipcileriAl()
    {
        //Takipci sayısını alır
        DatabaseReference takipciYolu = FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                .child("Takipciler");
        takipciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_takipçiler.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Takip edilen sayısını alır
        DatabaseReference takipedilenYolu = FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(profilId)
                .child("Takip Edilenler");
        takipedilenYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_takipEdilenler.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void gonderiSayisiAl()
    {
        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Gonderi gonderi = snapshot.getValue(Gonderi.class);
                    if (gonderi.getGonderen().equals(profilId))
                    {
                        i++;
                    }

                }
                txt_gonderiler.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fotograflarim()
    {
        DatabaseReference fotografYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");
        fotografYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Gonderi gonderi = snapshot.getValue(Gonderi.class);
                    if (gonderi.getGonderen().equals(profilId))
                    {
                        gonderiList.add(gonderi);
                    }
                }
                Collections.reverse(gonderiList);
                fotografAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void kaydettiklerim ()
    {
        kaydettiklerim = new ArrayList<>();
        DatabaseReference kaydettiklerimYolu = FirebaseDatabase.getInstance().getReference("Kaydedilenler")
                .child(mevcutKullanici.getUid());
        kaydettiklerimYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    kaydettiklerim.add(snapshot.getKey());
                }
                    kaydettiklerimiOku();
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kaydettiklerimiOku()
    {
        DatabaseReference gonderidenOku = FirebaseDatabase.getInstance().getReference("Gonderiler");
                gonderidenOku.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        gonderiList_kaydettiklerim.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            Gonderi gonderi = snapshot.getValue(Gonderi.class);

                            for (String id : kaydettiklerim)
                            {
                                if (gonderi.getGonderiId().equals(id))
                                {
                                    gonderiList_kaydettiklerim.add(gonderi);
                                }
                            }
                            fotografAdapterKaydettiklerim.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void bildirimleriEkle()
    {
        DatabaseReference bildirimEklemeYolu = FirebaseDatabase.getInstance().getReference("Bildirimler").child(profilId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciid",mevcutKullanici.getUid());
        hashMap.put("text","seni takip etmeye başladı");
        hashMap.put("gonderiid","");
        hashMap.put("ispost",false);

        bildirimEklemeYolu.push().setValue(hashMap);
    }
}