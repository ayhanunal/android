package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class YeniKayitActivity extends AppCompatActivity {

    EditText isimText;
    EditText soyisimText;
    EditText emailText;
    EditText tcText;
    EditText sifreText;
    EditText sifre2Text;

    TextView denkText;

    Button kullaniciEkleButton;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni_kayit);

        isimText = findViewById(R.id.kayitAdText);
        soyisimText = findViewById(R.id.kayitSoyadText);
        emailText = findViewById(R.id.kayitMailText);
        tcText = findViewById(R.id.kayitTcText);
        sifreText = findViewById(R.id.kayitSifreText);
        sifre2Text = findViewById(R.id.kayitSifre2Text);

        denkText = findViewById(R.id.kayitSifreDenk);

        kullaniciEkleButton = findViewById(R.id.yeniKullaniciEkleButton);

        sifreDenklikUyari();

        firebaseFirestore = FirebaseFirestore.getInstance();




    }

    public void kullaniciEkle(View view){

        final String alinanAd = isimText.getText().toString();
        final String alinanSoyad = soyisimText.getText().toString();
        final String alinanEmail = emailText.getText().toString();
        final String alinanTc = tcText.getText().toString();
        final String alinanSifre = sifreText.getText().toString();
        String alinanSifre2 = sifre2Text.getText().toString();

        if (alinanAd.matches("") || alinanSoyad.matches("") || alinanEmail.matches("")
                || alinanTc.matches("") || alinanSifre.matches("") || alinanSifre2.matches("")){

            Toast.makeText(YeniKayitActivity.this,"Alanları Boş Bırakmayın",Toast.LENGTH_LONG).show();

        }else {

            if(alinanSifre.matches(alinanSifre2)){

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Kaydet");
                alert.setMessage("Emin misin?");

                alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {

                        HashMap<String,Object> kaydedilecekVeri = new HashMap<>();
                        kaydedilecekVeri.put("isim",alinanAd);
                        kaydedilecekVeri.put("soyisim",alinanSoyad);
                        kaydedilecekVeri.put("tc",alinanTc);
                        kaydedilecekVeri.put("email",alinanEmail);
                        kaydedilecekVeri.put("sifre",alinanSifre);
                        kaydedilecekVeri.put("onay","0");

                        firebaseFirestore.collection("Kullanici").add(kaydedilecekVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Toast.makeText(YeniKayitActivity.this,"İsteğiniz İletildi. Onay Bekleniyor",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(YeniKayitActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(YeniKayitActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                            }
                        });



                    }
                });

                alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(getApplicationContext(),"Kaydedilmedi",Toast.LENGTH_LONG).show();

                    }
                });

                alert.show();

            }else {
                Toast.makeText(YeniKayitActivity.this,"Şifreler Eşit Değil",Toast.LENGTH_LONG).show();
            }
        }

    }


    public void sifreDenklikUyari(){
        sifre2Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (sifre2Text.getText().toString().matches(sifreText.getText().toString())){
                    denkText.setText("Şifreler Aynı");
                    kullaniciEkleButton.setVisibility(View.VISIBLE);
                }else {
                    denkText.setText("Şifreler Aynı Değil");
                    kullaniciEkleButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (sifre2Text.getText().toString().matches(sifreText.getText().toString())){
                    denkText.setText("Şifreler Aynı");
                    kullaniciEkleButton.setVisibility(View.VISIBLE);
                }else {
                    denkText.setText("Şifreler Aynı Değil");
                    kullaniciEkleButton.setVisibility(View.INVISIBLE);
                }

            }
        });
    }
}
