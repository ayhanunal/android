package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText emailText;
    EditText sifreText;

    ArrayList<String> mailler;
    ArrayList<String> sifreler;
    ArrayList<String> IDler;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailText = findViewById(R.id.mainEmailText);
        sifreText = findViewById(R.id.mainSifreText);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); //içeri giriş yapmış biri varsa
        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this,YetkiliSayfaActivity.class);
            intent.putExtra("icerdeki_email",firebaseUser.getEmail().toString());
            startActivity(intent);
            finish();
        }

        mailler = new ArrayList<>();
        sifreler = new ArrayList<>();
        IDler = new ArrayList<>();
        kullanicilariAl();







    }

    public void girisYap(View view){

        firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(),sifreText.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Intent yetSayfaIntent = new Intent(MainActivity.this,YetkiliSayfaActivity.class);
                startActivity(yetSayfaIntent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (mailler.indexOf(emailText.getText().toString()) != -1 && sifreler.indexOf(sifreText.getText().toString()) != -1 && mailler.indexOf(emailText.getText().toString()) == sifreler.indexOf(sifreText.getText().toString())){

                    Intent kulSayfaIntent = new Intent(MainActivity.this,KullaniciSayfaActivity.class);
                    kulSayfaIntent.putExtra("icerdekiKullaniciID",IDler.get(mailler.indexOf(emailText.getText().toString())));
                    kulSayfaIntent.putExtra("icerdekiKullaniciSifre",sifreler.get(mailler.indexOf(emailText.getText().toString())));
                    startActivity(kulSayfaIntent);
                    finish();

                }else {
                    Toast.makeText(MainActivity.this,"Giriş Başarısız",Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    public void yeniKayit(View view){

        Intent intent = new Intent(MainActivity.this,YeniKayitActivity.class);
        startActivity(intent);

    }

    public void kullanicilariAl(){

        CollectionReference collectionReference = firebaseFirestore.collection("Kullanici");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(queryDocumentSnapshots != null){

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String,Object> gelenVeri = snapshot.getData();

                        String gelenMail = (String) gelenVeri.get("email");
                        String gelenSifre = (String) gelenVeri.get("sifre");
                        String gelenDurum = (String) gelenVeri.get("onay");

                        if(gelenDurum.matches("1")){

                            mailler.add(gelenMail);
                            sifreler.add(gelenSifre);
                            IDler.add(snapshot.getId());

                        }


                    }

                }

            }
        });

    }
}
