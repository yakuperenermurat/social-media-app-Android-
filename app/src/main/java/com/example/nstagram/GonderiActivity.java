package com.example.nstagram;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class GonderiActivity extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000; // Galeri için request code

    Uri resimUri;
    String benimUrim = "";

    ImageView image_Kapat, image_Eklendi;
    TextView txt_Gonder;
    EditText edt_Gonderi_Hakkinda;

    private StorageReference resimYukleYolu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gonderi);

        image_Kapat = findViewById(R.id.close_Gonderi);
        image_Eklendi = findViewById(R.id.eklenen_Resim);
        txt_Gonder = findViewById(R.id.txt_Gonder);
        edt_Gonderi_Hakkinda = findViewById(R.id.edt_Gonderi_Hakkinda);

        resimYukleYolu = FirebaseStorage.getInstance().getReference("gonderiler");
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            resimUri = intent.getData(); // Galeriden alınan resim URI'si
            CropImage.activity(resimUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        image_Kapat.setOnClickListener(v -> {
            startActivity(new Intent(GonderiActivity.this, AnaSayfaActivity.class));
            finish();
        });

        txt_Gonder.setOnClickListener(v -> {
            if (resimUri != null) {
                resimYukle();
            } else {
                Toast.makeText(GonderiActivity.this, "Lütfen bir resim seçin", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            // Galeriden seçilen resmi kırpma aktivitesine gönder
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resimUri = result.getUri(); // Kırpılan resim URI'si
                image_Eklendi.setImageURI(resimUri); // ImageView'da gösterin
                // Gerekli olan diğer UI güncellemelerini yapın
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Kırpma hatası: " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String dosyaUzantisiAl(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void resimYukle() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Gönderiliyor...");
        progressDialog.show();

        if (resimUri != null) {
            final StorageReference dosyaYolu = resimYukleYolu.child(System.currentTimeMillis() + "." + dosyaUzantisiAl(resimUri));

            dosyaYolu.putFile(resimUri).addOnSuccessListener(taskSnapshot -> dosyaYolu.getDownloadUrl().addOnSuccessListener(uri -> {
                benimUrim = uri.toString();
                DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");

                String gonderiId = veriYolu.push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("gonderiId", gonderiId);
                hashMap.put("gonderiResmi", benimUrim);
                hashMap.put("gonderiHakkinda", edt_Gonderi_Hakkinda.getText().toString());
                hashMap.put("gonderen", FirebaseAuth.getInstance().getCurrentUser().getUid());

                veriYolu.child(gonderiId).setValue(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        startActivity(new Intent(GonderiActivity.this, AnaSayfaActivity.class));
                        finish();
                    } else {
                        Toast.makeText(GonderiActivity.this, "Gönderi kaydedilemedi.", Toast.LENGTH_SHORT).show();
                    }
                });
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(GonderiActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Seçilen resim yok", Toast.LENGTH_SHORT).show();
        }
    }
}