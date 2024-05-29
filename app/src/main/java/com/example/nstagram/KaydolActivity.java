package com.example.nstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.KeyAttributes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class KaydolActivity extends AppCompatActivity {
    EditText edt_kullaniciAdi,edt_Ad,edt_Email,edt_Sifre;

    Button btn_Kaydol;
    TextView txt_GirisSayfasınaGit;

    FirebaseAuth yetki;
    DatabaseReference yol;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaydol);
        edt_kullaniciAdi=findViewById(R.id.edt_kullaniciAdi);
        edt_Ad=findViewById(R.id.edt_Ad);
        edt_Email=findViewById(R.id.edt_Email);
        edt_Sifre=findViewById(R.id.edt_Sifre);
        edt_kullaniciAdi=findViewById(R.id.edt_kullaniciAdi);

        btn_Kaydol=findViewById(R.id.btn_Kaydol_activity);

        txt_GirisSayfasınaGit=findViewById(R.id.txt_GirisSayfasina_git);

        yetki=FirebaseAuth.getInstance();

        txt_GirisSayfasınaGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KaydolActivity.this,GirisActivity.class));
            }
        });
        btn_Kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd =new ProgressDialog(KaydolActivity.this);
                pd.setMessage("Lütfen bekleyin..");
                pd.show();

                String str_kullaniciAdi=edt_kullaniciAdi.getText().toString();
                String str_Ad=edt_Ad.getText().toString();
                String str_Email=edt_Email.getText().toString();
                String str_Sifre=edt_Sifre.getText().toString();
                if(TextUtils.isEmpty(str_kullaniciAdi)||TextUtils.isEmpty(str_Ad)||TextUtils.isEmpty(str_Email)
                        ||TextUtils.isEmpty(str_Sifre))
                {
                            Toast.makeText(KaydolActivity.this,"Lütfen bütün alanları doldurunuz...",Toast.LENGTH_SHORT).show();
                }
                else if(str_Sifre.length()<6)
                {
                    Toast.makeText(KaydolActivity.this,"Şifreniz minumum 6 karakter olmalı...",Toast.LENGTH_SHORT).show();
                }
                else
                { //yeni kullanıcı kaydetme kodlarını çağır
                    kaydet(str_kullaniciAdi,str_Ad,str_Email,str_Sifre);
                }
            }
        });

    }
    private void kaydet(String kullaniciadi,String ad,String email,String sifre)
    {
        //yeni kullanıcı kaydetme kodları
        yetki.createUserWithEmailAndPassword(email,sifre)
                .addOnCompleteListener(KaydolActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseKullanici = yetki.getCurrentUser();

                            String kullaniciId = firebaseKullanici.getUid();

                            yol = FirebaseDatabase.getInstance().getReference().child("Kullanıcılar").child(kullaniciId);

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("id",kullaniciId);
                            hashMap.put("kullaniciadi",kullaniciadi.toLowerCase());
                            hashMap.put("ad",ad);
                            hashMap.put("bio","");
                            hashMap.put("resimurl","https://firebasestorage.googleapis.com/v0/b/my-project-4dddb.appspot.com/o/placeholder.jpeg?alt=media&token=a6318b17-711a-4d99-879e-1c1b10aab4cf");

                            yol.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        pd.dismiss();

                                        Intent intent = new Intent(KaydolActivity.this,AnaSayfaActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);


                                    }
                                }
                            });
                        }

                        else
                        {
                            pd.dismiss();
                            Toast.makeText(KaydolActivity.this, "Bu mail veya şifre ile kayıt başarısız...",Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}