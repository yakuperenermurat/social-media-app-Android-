package com.example.nstagram;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.net.Uri;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.nstagram.cerceve.AramaFragment;
import com.example.nstagram.cerceve.BildirimFragment;
import com.example.nstagram.cerceve.ProfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.example.nstagram.cerceve.HomeFragment;


public class AnaSayfaActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    BottomNavigationView bottomNavigationView;
    Fragment seciliCerceve = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent!= null)
        {
            String gonderen = intent.getString("gonderenId");
            SharedPreferences.Editor editor = getSharedPreferences("PREPS",MODE_PRIVATE).edit();
            editor.putString("profileid",gonderen);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, new ProfilFragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, new HomeFragment()).commit();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new  BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if (id == R.id.nav_home) {
                        seciliCerceve = new HomeFragment();
                    } else if (id == R.id.nav_arama) {
                        seciliCerceve = new AramaFragment();
                    } else if (id == R.id.nav_ekle) {
                        gonderiYuklemeSecenekleriniGoster();
                    } else if (id == R.id.nav_kalp) {
                        seciliCerceve = new BildirimFragment();
                    } else if (id == R.id.nav_profil) {
                        SharedPreferences.Editor editor = getSharedPreferences("PREPS", MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        seciliCerceve = new ProfilFragment();
                    }

                    if (seciliCerceve != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, seciliCerceve).commit();
                    }
                    return true;
                }
            };
    private void gonderiYuklemeSecenekleriniGoster() {
        CharSequence secenekler[] = new CharSequence[]{"Kamera", "Galeri"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AnaSayfaActivity.this); // 'this' yerine 'AnaSayfaActivity.this' kullanın
        builder.setTitle("Bir Resim Seçin");
        builder.setItems(secenekler, (dialog, item) -> {
            if (item == 0) {
                // Kamera seçeneği için kod buraya
            } else if (item == 1) {
                // Galeri seçeneği
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            // Galeriden seçilen resmi kırpma aktivitesine gönder
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Resim URI'sini GonderiActivity'e gönderin
                Intent intent = new Intent(AnaSayfaActivity.this, GonderiActivity.class);
                intent.setData(imageUri);
                startActivity(intent); // GonderiActivity başlatılıyor
            }
        }
    }
}