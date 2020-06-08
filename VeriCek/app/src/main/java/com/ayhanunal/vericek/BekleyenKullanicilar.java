package com.ayhanunal.vericek;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BekleyenKullanicilar extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;

    ArrayList<String> listAdlar;
    ArrayList<String> listSoyadlar;
    ArrayList<String> listTcler;
    ArrayList<String> listMailler;
    ArrayList<String> listSifreler;

    ListView bekleyenKullaniciListe;

    ArrayAdapter arrayAdapter;

    String gelmeAmaci;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bekleyen_kullanicilar);

        firebaseFirestore = FirebaseFirestore.getInstance();

        listAdlar = new ArrayList<>();
        listSoyadlar = new ArrayList<>();
        listMailler = new ArrayList<>();
        listSifreler = new ArrayList<>();
        listTcler = new ArrayList<>();

        Intent amacIntent = getIntent();
        gelmeAmaci = amacIntent.getStringExtra("gelmeAmaci");

        kullanicilariGetir();


        bekleyenKullaniciListe = findViewById(R.id.bekleyenListView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listMailler);
        bekleyenKullaniciListe.setAdapter(arrayAdapter);

        bekleyenKullaniciListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(BekleyenKullanicilar.this,BekleyenKullanicilarOnay.class);
                intent.putExtra("detayGelmeAmaci",gelmeAmaci);
                intent.putExtra("listViewAd",listAdlar.get(i));
                intent.putExtra("listViewSoyad",listSoyadlar.get(i));
                intent.putExtra("listViewTc",listTcler.get(i));
                intent.putExtra("listViewMail",listMailler.get(i));
                intent.putExtra("listViewSifre",listSifreler.get(i));

                startActivity(intent);
            }
        });





    }

    public void kullanicilariGetir(){


        CollectionReference collectionReference = firebaseFirestore.collection("Kullanici");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Toast.makeText(BekleyenKullanicilar.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null){


                    try {

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){



                            Map<String,Object> gelenVeriBekleyenKullanici = snapshot.getData();

                            String gelenAd = (String) gelenVeriBekleyenKullanici.get("isim");
                            String gelenSoyad = (String) gelenVeriBekleyenKullanici.get("soyisim");
                            String gelenTc = (String) gelenVeriBekleyenKullanici.get("tc");
                            String gelenMail = (String) gelenVeriBekleyenKullanici.get("email");
                            String gelenSifre = (String) gelenVeriBekleyenKullanici.get("sifre");
                            String gelenOnay = (String) gelenVeriBekleyenKullanici.get("onay");


                            if(gelmeAmaci.matches("topKul")){

                                if (gelenOnay.matches("1") && listTcler.indexOf(gelenTc) == -1){

                                    listAdlar.add(gelenAd);
                                    listSoyadlar.add(gelenSoyad);
                                    listMailler.add(gelenMail);
                                    listTcler.add(gelenTc);
                                    listSifreler.add(gelenSifre);


                                }

                            }else if(gelmeAmaci.matches("onayKul")){

                                if (gelenOnay.matches("0") && listTcler.indexOf(gelenTc) == -1){

                                    listAdlar.add(gelenAd);
                                    listSoyadlar.add(gelenSoyad);
                                    listMailler.add(gelenMail);
                                    listTcler.add(gelenTc);
                                    listSifreler.add(gelenSifre);


                                }

                            }
                            arrayAdapter.notifyDataSetChanged();
                        }

                    }catch (Exception err){
                        err.printStackTrace();
                    }




                }

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(BekleyenKullanicilar.this,YetkiliSayfaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
