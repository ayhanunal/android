package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class KullaniciBilgileriGoruntule extends AppCompatActivity {

    String icerdekiKullaniciID;
    String icerdekiKullaniciSifre;

    EditText email;
    EditText isim;
    EditText soyisim;
    EditText tc;
    EditText yeniSifre;
    EditText eskiSifre;
    Button guncelleButton;

    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_bilgileri_goruntule);

        firebaseFirestore = FirebaseFirestore.getInstance();

        guncelleButton = findViewById(R.id.kulBilDuzKaydetButton);
        email = findViewById(R.id.kulBilDuzEmailText);
        isim = findViewById(R.id.kulBilDuzAdText);
        soyisim = findViewById(R.id.kulBilDuzSoyadText);
        tc = findViewById(R.id.kulBilDuzTcText);
        yeniSifre = findViewById(R.id.kulBilDuzYeniSifreText);
        eskiSifre = findViewById(R.id.kulBilDuzEskiSifre);

        eskiSifre.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();
        icerdekiKullaniciID = intent.getStringExtra("icerdekiKullaniciID");
        icerdekiKullaniciSifre = intent.getStringExtra("icerdekiKullaniciSifre");

        bilgileriAl();

        yeniSifre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!yeniSifre.getText().toString().matches("")){

                    eskiSifre.setVisibility(View.VISIBLE);

                }


            }
        });

    }

    public void bilgilerimiGuncelle(View view){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Güncelle");
        alert.setMessage("bilgiler güncellenecek emin misiniz ?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                HashMap<String,Object> kaydedilecek = new HashMap<>();

                if(isim.getText().toString().matches("") || soyisim.getText().toString().matches("")){

                    if (yeniSifre.getText().toString().matches("")){

                        Toast.makeText(KullaniciBilgileriGoruntule.this,"boş alanları doldurun.",Toast.LENGTH_SHORT).show();

                    }else {

                        if (eskiSifre.getText().toString().matches(icerdekiKullaniciSifre)){

                            kaydedilecek.put("sifre",yeniSifre.getText().toString());

                        }
                        else {
                            Toast.makeText(KullaniciBilgileriGoruntule.this,"eski şifre yanlış.",Toast.LENGTH_SHORT).show();
                        }

                    }

                }else
                {
                    if (yeniSifre.getText().toString().matches("")){

                        kaydedilecek.put("isim",isim.getText().toString());
                        kaydedilecek.put("soyisim",soyisim.getText().toString());

                    }else {

                        if (eskiSifre.getText().toString().matches(icerdekiKullaniciSifre)){

                            kaydedilecek.put("isim",isim.getText().toString());
                            kaydedilecek.put("soyisim",soyisim.getText().toString());
                            kaydedilecek.put("sifre",yeniSifre.getText().toString());

                        }
                        else {
                            Toast.makeText(KullaniciBilgileriGoruntule.this,"eski şifre yanlış.",Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                firebaseFirestore.collection("Kullanici").document(icerdekiKullaniciID).update(kaydedilecek).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(KullaniciBilgileriGoruntule.this,"Güncelleme Başarılı.",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(KullaniciBilgileriGoruntule.this,"Güncelleme Başarısız oldu.",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();

            }
        });
        alert.show();





    }

    public void bilgileriAl(){

        final DocumentReference documentReference = firebaseFirestore.collection("Kullanici").document(icerdekiKullaniciID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot != null){

                    email.setHint(documentSnapshot.get("email").toString());
                    email.setFocusable(false);

                    tc.setHint(documentSnapshot.get("tc").toString());
                    tc.setFocusable(false);

                    isim.setHint(documentSnapshot.get("isim").toString());
                    soyisim.setHint(documentSnapshot.get("soyisim").toString());



                }

            }
        });

    }

}
