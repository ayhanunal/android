package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class KullaniciSinavBaslat extends AppCompatActivity {

    String sinavDurumKontrol = "0";

    TextView dersAdi;
    TextView sinavSure;
    TextView soru;
    RadioGroup secenekler;
    RadioButton cevap;
    Button sinavBaslatBtn;
    Button sinavBitirBtn;
    Button sonrakiSoruBtn;

    String sinavID;
    String icerdekiKullaniciID;
    String dersAdiStr;
    int sinavSuresi;

    private FirebaseFirestore firebaseFirestore;

    ArrayList<HashMap> seceneklerList;
    ArrayList<String> kulCevapList;
    ArrayList<String> dogruCevapList;
    ArrayList<HashMap> soruCevapList;

    HashMap<String,String> gelenSoruCevapMap;
    HashMap<String,String> gelenSecenekMap;

    int soruSayac;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_sinav_baslat);

        firebaseFirestore = FirebaseFirestore.getInstance();

        seceneklerList = new ArrayList<>();
        kulCevapList = new ArrayList<>();
        soruCevapList = new ArrayList<>();
        dogruCevapList = new ArrayList<>();

        dersAdi = findViewById(R.id.sinavBaslatDersAdText);
        sinavSure = findViewById(R.id.sinavBaslatSureText);
        soru = findViewById(R.id.sinavBaslatSoruText);
        secenekler = findViewById(R.id.sinavBaslatCevapGroup);
        sinavBaslatBtn = findViewById(R.id.sinavBaslatStartButton);
        sinavBitirBtn = findViewById(R.id.sinavBaslatStopButton);
        sonrakiSoruBtn = findViewById(R.id.sinavBaslatSonrakiButton);

        Intent intent = getIntent();
        dersAdiStr = intent.getStringExtra("sinavBaslaSinavAD");
        dersAdi.setText(dersAdiStr);
        sinavSure.setText(intent.getStringExtra("sinavBaslaSinavDK")+" dk");
        sinavSuresi = 60000 * Integer.parseInt(intent.getStringExtra("sinavBaslaSinavDK"));
        sinavID = intent.getStringExtra("sinavBaslatSinavID");
        icerdekiKullaniciID = intent.getStringExtra("icerdekiKullaniciID");

        soru.setVisibility(View.INVISIBLE);
        secenekler.setVisibility(View.INVISIBLE);
        sinavBitirBtn.setVisibility(View.INVISIBLE);
        sonrakiSoruBtn.setVisibility(View.INVISIBLE);

        soruSayac = 0;


        sorularıAl();

        sinavGirisKontrol();



    }

    public void sinavBaslat(final View view){

        sinavDurumKontrol = "1";

        new CountDownTimer(sinavSuresi,1000){  //10000 ms(10 sn) den başlasın 1000 ms(1 sn) de bir düşsün.
            @Override
            public void onTick(long l) {

                // her 1 sn de bir ne yapacak.
                sinavSure.setText("Kalan :" + l/1000 + " sn."); //l, ms cinsinden old için 1000 e bölerek sn ye cevirdik.



            }

            @Override
            public void onFinish() {

                //bitince ne yapacak

                Toast.makeText(KullaniciSinavBaslat.this,"Süre Bitti",Toast.LENGTH_LONG).show();
                soru.setVisibility(View.INVISIBLE);
                secenekler.setVisibility(View.INVISIBLE);
                sonrakiSoruBtn.setVisibility(View.INVISIBLE);

                sinavBitir(view);

            }



        }.start(); //baslat


        HashMap<String,String> sorulariListedenAlMap = soruCevapList.get(soruSayac);
        Set set = sorulariListedenAlMap.entrySet();
        Iterator z = set.iterator();
        while (z.hasNext()){
            Map.Entry me = (Map.Entry) z.next();
            soru.setText("Soru- " + (soruSayac+1) + ") " + me.getKey().toString());
            dogruCevapList.add(me.getValue().toString());

        }


        HashMap<String,String> secenekleriListedenAlMap = seceneklerList.get(soruSayac);
        Set set2 = secenekleriListedenAlMap.entrySet();
        Iterator zz = set2.iterator();
        while (zz.hasNext()){
            Map.Entry me2 = (Map.Entry) zz.next();


            switch (me2.getKey().toString()){

                case "A" : {
                    ((RadioButton) secenekler.getChildAt(0)).setText("A) "+me2.getValue().toString());
                    break;
                }
                case "B" : {
                    ((RadioButton) secenekler.getChildAt(1)).setText("B) "+me2.getValue().toString());
                    break;
                }
                case "C" : {
                    ((RadioButton) secenekler.getChildAt(2)).setText("C) "+me2.getValue().toString());
                    break;
                }
                case "D" : {
                    ((RadioButton) secenekler.getChildAt(3)).setText("D) "+me2.getValue().toString());
                    break;
                }

            }


        }




        soru.setVisibility(View.VISIBLE);
        secenekler.setVisibility(View.VISIBLE);
        sinavBitirBtn.setVisibility(View.VISIBLE);
        sonrakiSoruBtn.setVisibility(View.VISIBLE);
        sinavBaslatBtn.setVisibility(View.INVISIBLE);



    }

    public void sinavBitir(View view){

        double dSayisi = 0;
        for(int i=0;i<kulCevapList.size();i++){
            if(kulCevapList.get(i).matches(dogruCevapList.get(i))){
                dSayisi++;
            }
        }
        double puan = (100.0/dogruCevapList.size())*dSayisi;


        HashMap<String,Object> kulSinavKaydet = new HashMap<>();
        kulSinavKaydet.put("dersAdi",dersAdiStr);
        kulSinavKaydet.put("katilim","1");
        kulSinavKaydet.put("puan",String.valueOf(puan));
        kulSinavKaydet.put("izinSonuc","0");

        firebaseFirestore.collection("Kullanici").document(icerdekiKullaniciID).collection("sinavDers").document(sinavID).set(kulSinavKaydet).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(KullaniciSinavBaslat.this,"Sınav Kaydedildi",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(KullaniciSinavBaslat.this,KullaniciSayfaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(KullaniciSinavBaslat.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

            }
        });

    }

    public void sonrakiSoru(View view){

        if (secenekler.getCheckedRadioButtonId() == -1){
            Toast.makeText(KullaniciSinavBaslat.this,"Seçim Yapınız.",Toast.LENGTH_SHORT).show();
        }else{

            int seciliCevap = secenekler.getCheckedRadioButtonId();
            cevap = findViewById(seciliCevap);

            String kCevap = cevap.getText().toString();
            kulCevapList.add(String.valueOf(kCevap.charAt(0)));

            secenekler.clearCheck();

            soruSayac++;
            if(soruSayac < soruCevapList.size()){

                HashMap<String,String> sorulariListedenAlMap = soruCevapList.get(soruSayac);
                Set set = sorulariListedenAlMap.entrySet();
                Iterator z = set.iterator();
                while (z.hasNext()){
                    Map.Entry me = (Map.Entry) z.next();
                    soru.setText("Soru- " + (soruSayac+1) + ") " + me.getKey().toString());
                    dogruCevapList.add(me.getValue().toString());

                }


                HashMap<String,String> secenekleriListedenAlMap = seceneklerList.get(soruSayac);
                Set set2 = secenekleriListedenAlMap.entrySet();
                Iterator zz = set2.iterator();
                while (zz.hasNext()){
                    Map.Entry me2 = (Map.Entry) zz.next();


                    switch (me2.getKey().toString()){

                        case "A" : {
                            ((RadioButton) secenekler.getChildAt(0)).setText("A) "+me2.getValue().toString());
                            break;
                        }
                        case "B" : {
                            ((RadioButton) secenekler.getChildAt(1)).setText("B) "+me2.getValue().toString());
                            break;
                        }
                        case "C" : {
                            ((RadioButton) secenekler.getChildAt(2)).setText("C) "+me2.getValue().toString());
                            break;
                        }
                        case "D" : {
                            ((RadioButton) secenekler.getChildAt(3)).setText("D) "+me2.getValue().toString());
                            break;
                        }

                    }


                }




            }else{

                soru.setText("SORULAR TAMAMLANDI");
                secenekler.setVisibility(View.INVISIBLE);
                sonrakiSoruBtn.setVisibility(View.INVISIBLE);

            }

        }



    }

    public void sorularıAl(){


        CollectionReference collectionReference = firebaseFirestore.collection("Sinav").document(sinavID).collection("sorular");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null){
                    Toast.makeText(KullaniciSinavBaslat.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null){

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                        gelenSoruCevapMap = new HashMap<String, String>();
                        gelenSecenekMap = new HashMap<String, String>();

                        Map<String,Object> gelenVeri = snapshot.getData();

                        gelenSoruCevapMap.put(gelenVeri.get("soru").toString(),gelenVeri.get("cevap").toString());

                        gelenSecenekMap.put("A",gelenVeri.get("A").toString());
                        gelenSecenekMap.put("B",gelenVeri.get("B").toString());
                        gelenSecenekMap.put("C",gelenVeri.get("C").toString());
                        gelenSecenekMap.put("D",gelenVeri.get("D").toString());

                        soruCevapList.add(gelenSoruCevapMap);
                        seceneklerList.add(gelenSecenekMap);



                    }

                }

            }
        });

    }

    public void sinavGirisKontrol(){


        try {

            CollectionReference collectionReference = firebaseFirestore.collection("Kullanici").document(icerdekiKullaniciID).collection("sinavDers");
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (queryDocumentSnapshots != null){

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                            Map<String,Object> gelenVeri = snapshot.getData();

                            if (snapshot.getId().matches(sinavID) && gelenVeri.get("katilim").toString().matches("1")){
                                soru.setVisibility(View.VISIBLE);
                                soru.setText("Bu sınava daha önce katıldınız tekrar giremezsiniz.");

                                sinavBitirBtn.setVisibility(View.INVISIBLE);
                                sonrakiSoruBtn.setVisibility(View.INVISIBLE);
                                sinavBaslatBtn.setVisibility(View.INVISIBLE);

                            }

                        }

                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && sinavDurumKontrol.matches("1")) {

            try {

                AlertDialog.Builder alert = new AlertDialog.Builder(KullaniciSinavBaslat.this);
                alert.setTitle("Çıkış Yapılıyor");
                alert.setMessage("Çıkarsanız puanınız 0 olarak kaydedilecektir. Emin misiniz?");
                alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        HashMap<String,Object> kaydedilecek = new HashMap<>();
                        kaydedilecek.put("dersAdi",dersAdiStr);
                        kaydedilecek.put("izinSonuc","0");
                        kaydedilecek.put("katilim","1");
                        kaydedilecek.put("puan","0.0");
                        firebaseFirestore.collection("Kullanici").document(icerdekiKullaniciID).collection("sinavDers").document(sinavID).set(kaydedilecek).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Intent intent = new Intent(KullaniciSinavBaslat.this,KullaniciSinavGir.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(KullaniciSinavBaslat.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
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

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return super.onKeyDown(keyCode, event);
    }
}
