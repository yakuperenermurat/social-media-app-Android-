package com.example.nstagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class SeceneklerActivity extends AppCompatActivity {

    TextView txt_cikisYap,txt_ayarlar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secenekler);

        txt_cikisYap=findViewById(R.id.txt_cikisyap_seceneklerActivity);
        txt_ayarlar = findViewById(R.id.txt_ayarlar_seceneklerActivity);

        //Toolbar ayarları
        Toolbar toolbar = findViewById(R.id.toolbar_seceneklerActivity);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Seçenekler");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        txt_cikisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SeceneklerActivity.this, BaslangicActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        txt_ayarlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ayarlar activity'e gitmek için bir intent oluşturun
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Geri butonuna basıldığında ne yapılacağını belirt
            finish(); // Bu metod, activity'i kapatır ve önceki activity'e döner
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}