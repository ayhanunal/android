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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SinavOlusturOnizleActivity extends AppCompatActivity {

    TextView dersAdi;
    TextView sinavSure;
    TextView sinavDurum;

    ListView listView;

    ArrayList<HashMap> sorularListe;
    ArrayList<HashMap> sorularSecenekListe;

    FirebaseFirestore firebaseFirestore;

    HashMap<String,Object> sinavBilgiMap;

    String documentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinav_olustur_onizle);

        dersAdi = findViewById(R.id.onizleSinavAdiText);
        sinavSure = findViewById(R.id.onizleSinavSureText);
        sinavDurum = findViewById(R.id.onizleSinavDurumText);
        listView = findViewById(R.id.sinavOnizleListView);
        sinavBilgiMap = new HashMap<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        dersAdi.setText("Ders Adı : " + intent.getStringExtra("onizleDersAd"));
        sinavSure.setText("Sınav Süresi : " + intent.getStringExtra("onizleSinavSure"));
        if(intent.getStringExtra("onizleSinavDurum").matches("0")){
            sinavDurum.setText("Sınav Durumu : Pasif");
        }else {
            sinavDurum.setText("Sınav Durumu : Aktif");
        }

        sorularListe = (ArrayList<HashMap>) intent.getSerializableExtra("onizleSorular");
        sorularSecenekListe = (ArrayList<HashMap>) intent.getSerializableExtra("onizleSecenekler");

        sinavBilgiMap.put("dersAdi",intent.getStringExtra("onizleDersAd"));
        sinavBilgiMap.put("aktif",intent.getStringExtra("onizleSinavDurum"));
        sinavBilgiMap.put("kayitliKullanici","0");
        sinavBilgiMap.put("sure",intent.getStringExtra("onizleSinavSure"));



        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,sorularListe);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(SinavOlusturOnizleActivity.this,sorularSecenekListe.get(i).toString(),Toast.LENGTH_LONG).show();

            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder soruSilAlert = new AlertDialog.Builder(SinavOlusturOnizleActivity.this);
                soruSilAlert.setTitle("Kaldır");
                soruSilAlert.setMessage("Soru Silinecek Emin Misin?");
                soruSilAlert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int ix) {

                        sorularListe.remove(i);
                        sorularSecenekListe.remove(i);
                        arrayAdapter.notifyDataSetChanged();

                    }
                }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int iy) {


                    }
                });
                soruSilAlert.show();
                return false;
            }
        });



    }
    public void sinavKaydet(View view){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kaydet");
        alert.setMessage("Sınav Kaydedilecek Emin misin?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                firebaseFirestore.collection("Sinav").add(sinavBilgiMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        documentID = documentReference.getId().toString();

                        for (int ix=0;ix<sorularListe.size();ix++){

                            HashMap<String,String> sorucevap = sorularListe.get(ix);
                            HashMap<String,String> sorusecenek = sorularSecenekListe.get(ix);


                            Set set = sorucevap.entrySet();
                            Iterator j = set.iterator();
                            while (j.hasNext()){
                                Map.Entry me = (Map.Entry) j.next();

                                HashMap<String,Object> sorumap = new HashMap<>();
                                sorumap.put("soru",me.getKey());
                                sorumap.put("cevap",me.getValue());

                                Set set1 = sorusecenek.entrySet();
                                Iterator k = set1.iterator();
                                while (k.hasNext()){
                                    Map.Entry me1 = (Map.Entry) k.next();

                                    sorumap.put(me1.getKey().toString(),me1.getValue().toString());


                                }


                                firebaseFirestore.collection("Sinav").document(documentID).collection("sorular").document(String.valueOf(ix)).set(sorumap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(SinavOlusturOnizleActivity.this,"Sınav Kaydedildi",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SinavOlusturOnizleActivity.this,YetkiliSinavActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(SinavOlusturOnizleActivity.this,"Sınav Eklenemedi",Toast.LENGTH_LONG).show();

                                    }
                                });



                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SinavOlusturOnizleActivity.this,"Başarısız",Toast.LENGTH_LONG).show();

                    }
                });







            }
        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(SinavOlusturOnizleActivity.this,"Eklenmedi",Toast.LENGTH_LONG).show();

            }
        });

        alert.show();



    }


    public void katilimciDuzenle(View view){

        //sinava kimlerin katılacağını belirle

    }
}
