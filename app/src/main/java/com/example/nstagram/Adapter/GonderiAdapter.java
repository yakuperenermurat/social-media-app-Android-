package com.example.nstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nstagram.Model.Gonderi;
import com.example.nstagram.Model.Kullanici;
import com.example.nstagram.R;
import com.example.nstagram.TakipcilerActivity;
import com.example.nstagram.YorumlarActivity;
import com.example.nstagram.cerceve.GonderiDetayiFragment;
import com.example.nstagram.cerceve.ProfilFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class GonderiAdapter extends RecyclerView.Adapter<GonderiAdapter.ViewHolder> {
    public Context mContext;
    public List<Gonderi> mGonderi;
    private FirebaseUser mevcutFirebaseUser;

    public GonderiAdapter(Context mContext, List<Gonderi> mGonderi) {
        this.mContext = mContext;
        this.mGonderi = mGonderi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.gonderi_ogesi,viewGroup,false);

        return new GonderiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        mevcutFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Gonderi gonderi = mGonderi.get(i);
        Glide.with(mContext).load(gonderi.getGonderiResmi()).into(viewHolder.gonderi_resmi);
        if (gonderi.getGonderiHakkinda().equals(""))
        {
            viewHolder.txt_gonderiHakkinda.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.txt_gonderiHakkinda.setVisibility(View.VISIBLE);
            viewHolder.txt_gonderiHakkinda.setText(gonderi.getGonderiHakkinda());
        }

        //Metotları çağırma

        gonderenBilgileri(viewHolder.profil_resmi,viewHolder.txt_kullanici_adi,viewHolder.txt_gonderen,gonderi.getGonderen());
        begenildi(gonderi.getGonderiId(),viewHolder.begeni_resmi);
        begeniSayisi(viewHolder.txt_begeni,gonderi.getGonderiId());
        yorumlariAl(gonderi.getGonderiId(),viewHolder.txt_yorumlar);
        kaydedildi(gonderi.getGonderiId(),viewHolder.kaydetme_resmi);

        //Profil resmine tıklama olayı
        viewHolder.profil_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
            }
        });
        //Kullanici adina tıklandığında
        viewHolder.txt_kullanici_adi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
            }
        });

        // Gonderene tiklandiğinda
        viewHolder.txt_gonderen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
            }
        });

        //Gonderi resmine tıklandiğinda
        viewHolder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",gonderi.getGonderiId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new GonderiDetayiFragment()).commit();
            }
        });

        //Kaydetme resmi tıklama olayı
        viewHolder.kaydetme_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.kaydetme_resmi.getTag().equals("kaydet"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Kaydedilenler").child(mevcutFirebaseUser.getUid())
                            .child(gonderi.getGonderiId()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Kaydedilenler").child(mevcutFirebaseUser.getUid())
                            .child(gonderi.getGonderiId()).removeValue();
                }
            }
        });

        //Beğeni resmi tıklama olayı
        viewHolder.begeni_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.begeni_resmi.getTag().equals("beğen"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Begeniler")
                            .child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).setValue(true);
                    bildirimleriEkle(gonderi.getGonderen(),gonderi.getGonderiId());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Begeniler")
                            .child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).removeValue();
                }
            }
        });

        //Yorum resmi tıklama olayı
        viewHolder.yorum_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, YorumlarActivity.class);
                intent.putExtra("gonderiId", gonderi.getGonderiId());
                mContext.startActivity(intent);
            }
        });

        viewHolder.txt_yorumlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, YorumlarActivity.class);
                intent.putExtra("gonderiId", gonderi.getGonderiId());
                mContext.startActivity(intent);
            }
        });
        //Begeniler textine tıkladığında
        viewHolder.txt_begeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TakipcilerActivity.class);
                intent.putExtra("id",gonderi.getGonderiId());
                intent.putExtra("baslik","begeniler");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGonderi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profil_resmi, gonderi_resmi, begeni_resmi, yorum_resmi, kaydetme_resmi;
        public TextView txt_kullanici_adi, txt_begeni, txt_gonderen, txt_gonderiHakkinda, txt_yorumlar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profil_resmi = itemView.findViewById(R.id.profil_resmi_Gonderi_Ogesi);
            gonderi_resmi = itemView.findViewById(R.id.gonderi_resmi_Gonderi_Ogesi);
            begeni_resmi = itemView.findViewById(R.id.begeni_Gonderi_Ogesi);
            yorum_resmi = itemView.findViewById(R.id.yorum_Gonderi_Ogesi);
            kaydetme_resmi = itemView.findViewById(R.id.kaydet_Gonderi_Ogesi);

            txt_kullanici_adi = itemView.findViewById(R.id.txt_kullaniciadi_Gonderi_Ogesi);
            txt_begeni = itemView.findViewById(R.id.txt_begeniler_Gonderi_Ogesi);
            txt_gonderen = itemView.findViewById(R.id.txt_gonderen_Gonderi_Ogesi);
            txt_gonderiHakkinda = itemView.findViewById(R.id.txt_gonderiHakkinda_Gonderi_Ogesi);
            txt_yorumlar = itemView.findViewById(R.id.txt_yorum_Gonderi_Ogesi);
        }
    }
    private void yorumlariAl(String gonderiId, final TextView yorumlar) {
        DatabaseReference yorumlariAlmaYolu = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);
        yorumlariAlmaYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("YorumSayısı", "Gonderi ID: " + gonderiId + ", Yorum Sayısı: " + dataSnapshot.getChildrenCount());

                yorumlar.setText(dataSnapshot.getChildrenCount() + " yorumun hepsini gör...");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void begenildi (String gonderiId ,ImageView imageView)
    {
        FirebaseUser mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference begeniVeriTabanıYolu = FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);
        begeniVeriTabanıYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mevcutKullanici.getUid()).exists())
                {
                    imageView.setImageResource(R.drawable.ic_begenildi);
                    imageView.setTag("beğenildi");
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_begeni);
                    imageView.setTag("beğen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void begeniSayisi (final TextView begeniler, String gonderiId)
    {
        DatabaseReference begeniSayisiVeriTabanıYolu = FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);
        begeniSayisiVeriTabanıYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                begeniler.setText(dataSnapshot.getChildrenCount()+"  beğeni");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void gonderenBilgileri(final ImageView profil_resmi, final TextView kullaniciadi, final TextView gonderen,String kullaniciId) {
        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(kullaniciId);

        veriYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                Glide.with(mContext).load(kullanici.getResimurl()).into(profil_resmi);
                kullaniciadi.setText(kullanici.getKullaniciadi());
                gonderen.setText(kullanici.getKullaniciadi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void kaydedildi(final String gonderiId, final ImageView imageView)
    {
        FirebaseUser mevcutkullanici = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference kaydetmeYolu = FirebaseDatabase.getInstance().getReference().child("Kaydedilenler")
                .child(mevcutkullanici.getUid());

        kaydetmeYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(gonderiId).exists())
                {
                    imageView.setImageResource(R.drawable.ic_kaydedildi);
                    imageView.setTag("kaydedildi");
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_kaydet_siyah);
                    imageView.setTag("kaydet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void bildirimleriEkle(String kullaniciId , String gonderiId )
    {
        DatabaseReference bildirimEklemeYolu = FirebaseDatabase.getInstance().getReference("Bildirimler").child(kullaniciId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciid",mevcutFirebaseUser.getUid());
        hashMap.put("text","gönderini beğendi");
        hashMap.put("gonderiid",gonderiId);
        hashMap.put("ispost",true);

        bildirimEklemeYolu.push().setValue(hashMap);
    }
}