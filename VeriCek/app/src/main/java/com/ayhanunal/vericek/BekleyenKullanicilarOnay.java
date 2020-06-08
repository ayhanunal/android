package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BekleyenKullanicilarOnay extends AppCompatActivity {

    TextView isimText;
    TextView soyisimText;
    TextView tcText;
    TextView mailText;
    TextView sifreText;

    String listViewAd;
    String listViewSoyad;
    String listViewSifre;
    String listViewTc;
    String listViewMail;

    ArrayList<String> documanIdler;

    Button kabulButton;
    Button redButton;

    private FirebaseFirestore firebaseFirestore;

    String detayGelmeAmaci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bekleyen_kullanicilar_onay);

        isimText = findViewById(R.id.onayAdText);
        soyisimText = findViewById(R.id.onaySoyadText);
        tcText = findViewById(R.id.onayTcText);
        mailText = findViewById(R.id.onayMailText);
        sifreText = findViewById(R.id.onaySifreText);

        kabulButton = findViewById(R.id.kulOnayKabulButton);
        redButton = findViewById(R.id.kulOnayRedButton);

        documanIdler = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        listViewAd = intent.getStringExtra("listViewAd");
        listViewSoyad = intent.getStringExtra("listViewSoyad");
        listViewSifre = intent.getStringExtra("listViewSifre");
        listViewMail = intent.getStringExtra("listViewMail");
        listViewTc = intent.getStringExtra("listViewTc");

        detayGelmeAmaci = intent.getStringExtra("detayGelmeAmaci");

        if(detayGelmeAmaci.matches("topKul")){
            kabulButton.setText("Kullanıcıyı Sil");
            redButton.setText("Yetkilerini Al");

            kabulButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    kulSil();
                }
            });

            redButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    yetkiAl();

                }
            });
        }


        isimText.setText("İsim : "+listViewAd);
        soyisimText.setText("Soyisim : "+listViewSoyad);
        tcText.setText("Tc : "+listViewTc);
        sifreText.setText("Şifre : "+listViewSifre);
        mailText.setText("Email : "+listViewMail);

        idVeriCek();


    }

    public void reddet(View view){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kullanıcı Silinecektir.");
        alert.setMessage("Emin misiniz?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DocumentReference documentReference = firebaseFirestore.collection("Kullanici").document(documanIdler.get(0));
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,"İstek Silindi",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BekleyenKullanicilarOnay.this,BekleyenKullanicilar.class);
                        intent.putExtra("gelmeAmaci","onayKul");
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                });

            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(BekleyenKullanicilarOnay.this,"İstek Silinmedi",Toast.LENGTH_LONG).show();

            }
        });
        alert.show();



    }

    public void onayla(View view){


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kullanıcı Onaylanacaktır.");
        alert.setMessage("Emin misiniz?");

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(BekleyenKullanicilarOnay.this,"Onaylanmadı",Toast.LENGTH_LONG).show();

            }
        }).setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                DocumentReference documentReference = firebaseFirestore.collection("Kullanici").document(documanIdler.get(0));

                documentReference.update("onay","1").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,"onaylanamadı",Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,"onaylandı",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BekleyenKullanicilarOnay.this,BekleyenKullanicilar.class);
                        intent.putExtra("gelmeAmaci","onayKul");
                        startActivity(intent);
                        finish();

                    }
                });






            }
        });
        alert.show();

    }

    public void yetkiAl(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kullanıcının Yetkileri Alınacaktır.");
        alert.setMessage("Emin misiniz?");

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(BekleyenKullanicilarOnay.this,"Vazgeçildi",Toast.LENGTH_SHORT).show();

            }
        }).setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                DocumentReference documentReference = firebaseFirestore.collection("Kullanici").document(documanIdler.get(0));

                documentReference.update("onay","0").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,"onaylandı",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BekleyenKullanicilarOnay.this,BekleyenKullanicilar.class);
                        intent.putExtra("gelmeAmaci","topKul");
                        startActivity(intent);
                        finish();

                    }
                });



            }
        });
        alert.show();

    }


    public void kulSil(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kullanıcı Silinecektir.");
        alert.setMessage("Emin misiniz?");


        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DocumentReference documentReference = firebaseFirestore.collection("Kullanici").document(documanIdler.get(0));
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,"Kullanıcı Silindi",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BekleyenKullanicilarOnay.this,BekleyenKullanicilar.class);
                        intent.putExtra("gelmeAmaci","topKul");
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(BekleyenKullanicilarOnay.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                });

            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(BekleyenKullanicilarOnay.this,"Kullanıcı Silinmedi",Toast.LENGTH_LONG).show();

            }
        });
        alert.show();

    }

    public void idVeriCek(){
        CollectionReference collectionReference = firebaseFirestore.collection("Kullanici");
        collectionReference.whereEqualTo("email",listViewMail).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null){
                    Toast.makeText(BekleyenKullanicilarOnay.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null){

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        String documantId = snapshot.getId();
                        documanIdler.add(documantId);

                    }

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(BekleyenKullanicilarOnay.this,BekleyenKullanicilar.class);
        if (detayGelmeAmaci.matches("topKul")){
            intent.putExtra("gelmeAmaci","topKul");
        }else if (detayGelmeAmaci.matches("onayKul")){
            intent.putExtra("gelmeAmaci","onayKul");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
