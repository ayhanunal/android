package com.ayhanunal.vericek;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YetkiliSinavActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<String> sinavAdi;
    ArrayList<String> sinavDurumu;
    ArrayList<String> sinavKullaniciSayisi;
    ArrayList<String> sinavSuresi;
    ArrayList<HashMap> sinavSorular;
    ArrayList<String> sinavIDler;


    YetkiliSinavRecycleAdapter yetkiliSinavRecycleAdapter;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yetkili_sinav);

        sinavAdi = new ArrayList<>();
        sinavDurumu = new ArrayList<>();
        sinavKullaniciSayisi = new ArrayList<>();
        sinavSuresi = new ArrayList<>();
        sinavSorular = new ArrayList<>();
        sinavIDler = new ArrayList<>();



        firebaseFirestore = FirebaseFirestore.getInstance();

        verileriOku();



        recyclerView = findViewById(R.id.yetSinavRecyleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        yetkiliSinavRecycleAdapter = new YetkiliSinavRecycleAdapter(sinavAdi,sinavDurumu,sinavKullaniciSayisi);
        recyclerView.setAdapter(yetkiliSinavRecycleAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(YetkiliSinavActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent sinavBilgisiIntent = new Intent(YetkiliSinavActivity.this,SinavDetaylariActivity.class);
                sinavBilgisiIntent.putExtra("detaySinavAd",sinavAdi.get(position));
                sinavBilgisiIntent.putExtra("detaySinavDurum",sinavDurumu.get(position));
                sinavBilgisiIntent.putExtra("detaySinavKullanici",sinavKullaniciSayisi.get(position));
                sinavBilgisiIntent.putExtra("detaySinavSure",sinavSuresi.get(position));
                sinavBilgisiIntent.putExtra("detaySinavSorular",sinavSorular.get(position));
                sinavBilgisiIntent.putExtra("detaySinavIDler",sinavIDler.get(position));
                startActivity(sinavBilgisiIntent);

            }

            @Override
            public void onLongClick(View view, int position) {

                Toast.makeText(YetkiliSinavActivity.this,"uzun basma aslan",Toast.LENGTH_LONG).show();

            }
        }));






    }

    public void yeniSinav(View view){

        Intent yeniSinavIntent = new Intent(YetkiliSinavActivity.this,SinavOlusturActivity.class);
        startActivity(yeniSinavIntent);

    }

    public void verileriOku(){

        CollectionReference collectionReference = firebaseFirestore.collection("Sinav");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null){
                    Toast.makeText(YetkiliSinavActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null){

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String,Object> gelenVeri = snapshot.getData();

                        String dersad = (String) gelenVeri.get("dersAdi");
                        String sinavdurum = (String) gelenVeri.get("aktif");
                        String sinavsure = (String) gelenVeri.get("sure");
                        String sinavkullanici = (String) gelenVeri.get("kayitliKullanici");

                        sinavAdi.add(dersad);
                        sinavDurumu.add(sinavdurum);
                        sinavSuresi.add(sinavsure);
                        sinavKullaniciSayisi.add(sinavkullanici);
                        sinavIDler.add(snapshot.getId());

                        //System.out.println(dersad);

                        final HashMap<String,HashMap> sorularNumaraSozluk = new HashMap<>();

                        CollectionReference collectionReference1 = firebaseFirestore.collection("Sinav").document(snapshot.getId()).collection("sorular");
                        collectionReference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots1, @Nullable FirebaseFirestoreException e1) {

                                if (e1 != null){
                                    Toast.makeText(YetkiliSinavActivity.this,e1.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                                }

                                if (queryDocumentSnapshots1 != null){

                                    for (DocumentSnapshot snapshot1 : queryDocumentSnapshots1.getDocuments()){

                                        HashMap<String,String> sorularCevaplarSozluk = new HashMap<>();
                                        Map<String,Object> gelenVeriSoru = snapshot1.getData();

                                        String sinavsoru = (String) gelenVeriSoru.get("soru");
                                        String sinavcevap = (String) gelenVeriSoru.get("cevap");
                                        String sorunumarasi = snapshot1.getId();
                                        String secA = (String) gelenVeriSoru.get("A");
                                        String secB = (String) gelenVeriSoru.get("B");
                                        String secC = (String) gelenVeriSoru.get("C");
                                        String secD = (String) gelenVeriSoru.get("D");

                                        switch (sinavcevap){
                                            case "A" : {sinavcevap = secA + " (A)";break;}
                                            case "B" : {sinavcevap = secB + " (B)";break;}
                                            case "C" : {sinavcevap = secC + " (C)";break;}
                                            case "D" : {sinavcevap = secD + "(D)";break;}
                                        }


                                        sorularCevaplarSozluk.put(sinavsoru,sinavcevap);
                                        sorularNumaraSozluk.put(sorunumarasi,sorularCevaplarSozluk);




                                    }

                                    sinavSorular.add(sorularNumaraSozluk);
                                }

                            }
                        });


                        yetkiliSinavRecycleAdapter.notifyDataSetChanged();

                    }

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(YetkiliSinavActivity.this,YetkiliSayfaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
