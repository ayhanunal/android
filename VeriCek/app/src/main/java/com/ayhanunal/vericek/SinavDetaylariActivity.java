package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SinavDetaylariActivity extends AppCompatActivity {

    TextView detaySinavAdi;
    TextView detaySinavDurum;
    TextView detaySinavKullanici;
    TextView detaySinavSure;

    ListView detaySinavListView;

    String sinavDurumYazi;

    ArrayList<String> sorularList;
    ArrayList<String> cevaplarList;

    Button aktifPasifButton;
    String seciliSinavID;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinav_detaylari);

        firebaseFirestore = FirebaseFirestore.getInstance();

        aktifPasifButton = findViewById(R.id.sinavDetaySinavAktButton);
        detaySinavAdi = findViewById(R.id.sinavDetaySinavAdiText);
        detaySinavDurum = findViewById(R.id.sinavDetaySinavDurumText);
        detaySinavKullanici = findViewById(R.id.sinavDetaySinavKullaniciText);
        detaySinavSure = findViewById(R.id.sinavDetaySinavSureText);
        detaySinavListView = findViewById(R.id.sinavDetayListView);

        sorularList = new ArrayList<>();
        cevaplarList = new ArrayList<>();

        Intent intent = getIntent();
        if(intent.getStringExtra("detaySinavDurum").matches("1")){
            sinavDurumYazi = "Aktif";
            aktifPasifButton.setText("Pasifleştir");
        }else {
            sinavDurumYazi = "Pasif";
            aktifPasifButton.setText("Aktifleştir");
        }


        seciliSinavID = intent.getStringExtra("detaySinavIDler");

        HashMap<String,String> gelenSoru = (HashMap<String, String>) intent.getSerializableExtra("detaySinavSorular");
        detaySinavAdi.setText("Sınav Adı : " + intent.getStringExtra("detaySinavAd"));
        detaySinavDurum.setText("Sınav Durumu : " + sinavDurumYazi);
        detaySinavKullanici.setText("Kullanıcı Sayısı : " + intent.getStringExtra("detaySinavKullanici"));
        detaySinavSure.setText("Sınav Süresi : " + intent.getStringExtra("detaySinavSure"));

        Set set = gelenSoru.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()){

            Map.Entry me = (Map.Entry) i.next();

            HashMap<String,String> gelenSoruDeger = (HashMap<String,String>) me.getValue();
            Set set1 = gelenSoruDeger.entrySet();
            Iterator j = set1.iterator();
            while (j.hasNext()){

                Map.Entry me1 = (Map.Entry) j.next();
                sorularList.add(me1.getKey().toString());
                cevaplarList.add(me1.getValue().toString());

            }


        }


        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,sorularList);
        detaySinavListView.setAdapter(arrayAdapter);

        detaySinavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SinavDetaylariActivity.this,"Cevap :"+cevaplarList.get(i),Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void sinavDurumDegis(View view){


        if (sinavDurumYazi.matches("Aktif")){
            sinavDurumYazi = "0";
        }else {
            sinavDurumYazi = "1";
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Sınav Durumu Değişecektir.");
        alert.setMessage("Emin misiniz?");

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        }).setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                DocumentReference documentReference = firebaseFirestore.collection("Sinav").document(seciliSinavID);

                documentReference.update("aktif",sinavDurumYazi).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SinavDetaylariActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(SinavDetaylariActivity.this,"Durum Değişti",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SinavDetaylariActivity.this,YetkiliSinavActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                });






            }
        });
        alert.show();


    }

    public void sinavSil(View view){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Sınav Sil");
        alert.setMessage("Sınav Silinecektir. Emin misiniz?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DocumentReference documentReference = firebaseFirestore.collection("Sinav").document(seciliSinavID);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(SinavDetaylariActivity.this,"Sınav Silindi",Toast.LENGTH_SHORT);
                        Intent intent = new Intent(SinavDetaylariActivity.this,YetkiliSinavActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SinavDetaylariActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT);
                    }
                });

            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();


    }

    public void sinavSonuclari(View view){


        Intent intent = new Intent(SinavDetaylariActivity.this,YetkiliSinavSonucGoruntule.class);
        intent.putExtra("geldigimSayfa","topSinavSonucGor");
        intent.putExtra("sinavSonucSinavID",seciliSinavID);
        startActivity(intent);



    }
}
