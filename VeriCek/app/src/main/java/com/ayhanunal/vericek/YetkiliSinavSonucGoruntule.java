package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransitionImpl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.SecureRandom;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Map;

public class YetkiliSinavSonucGoruntule extends AppCompatActivity {

    Button sonucYayinlaButton;
    TextView bilgiText;
    ListView listView;
    ArrayAdapter arrayAdapter;

    private FirebaseFirestore firebaseFirestore;
    String seciliSinavID;
    String icerdekiKullaniciID;

    ArrayList<String> sinavSonuc;
    ArrayList<String> documertIDler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yetkili_sinav_sonuc_goruntule);

        firebaseFirestore = FirebaseFirestore.getInstance();

        sinavSonuc = new ArrayList<>();
        documertIDler = new ArrayList<>();
        sonucYayinlaButton = findViewById(R.id.sinavSonucYayinlaButton);
        bilgiText = findViewById(R.id.sinavSonuGoruntuleBilgiText);
        listView = findViewById(R.id.sinavSonucListView);


        Intent intent = getIntent();

        if (intent.getStringExtra("geldigimSayfa").matches("topSinavSonucGor")){

            seciliSinavID = intent.getStringExtra("sinavSonucSinavID");
            topKulSonuclarıGetir();
            //burada sorun var


        }else if (intent.getStringExtra("geldigimSayfa").matches("kulSinavSonucGor")){

            sonucYayinlaButton.setVisibility(View.INVISIBLE);
            icerdekiKullaniciID = intent.getStringExtra("icerdekiKullanici");

            kulSinavSonucGetir();



        }




        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sinavSonuc);
        listView.setAdapter(arrayAdapter);








    }


    public void topKulSonuclarıGetir(){

        documentIDGetir();
        for (String aa : documertIDler){
            System.out.println(aa);
        }

        //burda sıkıntı var

        try {



            //DocumentReference documentReference = firebaseFirestore.collection("Kullanici");
            CollectionReference collectionReference = firebaseFirestore.collection("Kullanici");
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (queryDocumentSnapshots != null){



                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                            Map<String,Object> gelenVeri = snapshot.getData();






                        }

                    }

                }
            });

            /*
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (documentSnapshot != null){

                        System.out.println(documentSnapshot);
                        System.out.println(documentSnapshot.get("puan"));


                    }

                }
            });
            */


        }catch (Exception err){
            err.printStackTrace();
            System.out.println("catch");
        }



    }

    public void kulSinavSonucGetir(){

        try {

            CollectionReference collectionReference = firebaseFirestore.collection("Kullanici").document(icerdekiKullaniciID).collection("sinavDers");
            collectionReference.whereEqualTo("izinSonuc","1").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (e != null){

                        e.printStackTrace();
                    }

                    if (queryDocumentSnapshots != null){

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                            Map<String,Object> gelenVeri = snapshot.getData();

                            String sonuc = gelenVeri.get("dersAdi").toString() + "/ Puan : ";
                            sonuc += gelenVeri.get("puan").toString();

                            sinavSonuc.add(sonuc);
                            arrayAdapter.notifyDataSetChanged();

                        }

                        if (sinavSonuc.size() == 0){
                            bilgiText.setText("girmiş olduğunuz ya da sonucu açıklanan sınav yok.");


                        }else {
                            bilgiText.setText("Girmiş olduğunuz sınavların sonuçları aşağıdadır.");
                        }

                    }

                }
            });

        }catch (Exception err){

            err.printStackTrace();
        }


    }

    public void sonuclariYayinla(View view){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent;
        if (icerdekiKullaniciID == null){
            intent = new Intent(YetkiliSinavSonucGoruntule.this,SinavDetaylariActivity.class);
            startActivity(intent);
            finish();
        }else {
            intent = new Intent(YetkiliSinavSonucGoruntule.this,KullaniciSayfaActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void documentIDGetir(){

        CollectionReference collectionReference = firebaseFirestore.collection("Kisiler");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots != null){

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        documertIDler.add(snapshot.getId());

                    }

                }

            }
        });

    }
}
