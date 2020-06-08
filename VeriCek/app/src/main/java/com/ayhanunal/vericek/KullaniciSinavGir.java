package com.ayhanunal.vericek;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class KullaniciSinavGir extends AppCompatActivity {

    ListView listView;
    Button sinavaGitButton;
    TextView bilgiText;

    private FirebaseFirestore firebaseFirestore;

    String icerdekiKullaniciID;

    ArrayList<String> sinavID;
    ArrayList<String> sinavAD;
    ArrayList<String> sinavDK;

    ArrayAdapter arrayAdapter;

    int seciliIX = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_sinav_gir);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        icerdekiKullaniciID = intent.getStringExtra("icerdekiKullaniciID");

        sinavID = new ArrayList<>();
        sinavAD = new ArrayList<>();
        sinavDK = new ArrayList<>();

        listView = findViewById(R.id.kullaniciSinavGirListView);
        sinavaGitButton = findViewById(R.id.KullaniciSinavGirBaslatButton);
        bilgiText = findViewById(R.id.kullaniciSinavGirBilgiText);

        sinavaGitButton.setVisibility(View.INVISIBLE);

        sinavlariGetir();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,sinavAD);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                sinavaGitButton.setVisibility(View.VISIBLE);
                seciliIX = i;
                bilgiText.setText("Seçili Sınav : " + sinavAD.get(seciliIX));


            }
        });


    }

    public void sinavlariGetir(){

        CollectionReference collectionReference = firebaseFirestore.collection("Sinav");
        collectionReference.whereEqualTo("aktif","1").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Toast.makeText(KullaniciSinavGir.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
                }

                if(queryDocumentSnapshots != null){

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String,Object> gelenVeri = snapshot.getData();

                        String dersAdi = (String) gelenVeri.get("dersAdi");
                        String sure = (String) gelenVeri.get("sure");

                        sinavID.add(snapshot.getId());
                        sinavAD.add(dersAdi);
                        sinavDK.add(sure);

                        arrayAdapter.notifyDataSetChanged();

                    }

                }

            }
        });

    }

    public void sinavaGit(View view){

        Intent intent = new Intent(KullaniciSinavGir.this,KullaniciSinavBaslat.class);
        intent.putExtra("sinavBaslatSinavID",sinavID.get(seciliIX));
        intent.putExtra("sinavBaslaSinavAD",sinavAD.get(seciliIX));
        intent.putExtra("sinavBaslaSinavDK",sinavDK.get(seciliIX));
        intent.putExtra("icerdekiKullaniciID",icerdekiKullaniciID);
        startActivity(intent);

    }
}
