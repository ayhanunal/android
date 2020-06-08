package com.ayhanunal.vericek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SinavOlusturActivity extends AppCompatActivity {

    EditText dersAdi;
    EditText sinavSure;
    RadioGroup sinavDurumGroup;
    RadioButton sinavDurumRadio;
    EditText soru;
    EditText soruA;
    EditText soruB;
    EditText soruC;
    EditText soruD;
    RadioGroup soruCevapGroup;
    RadioButton soruCevapRadio;
    Button soruyuKaydetButton;
    Button yeniSoruButton;
    Button sinavOnizleButton;

    TextView cevapLabel;

    ArrayList<HashMap> kaydedilecekSorular;
    ArrayList<HashMap> kaydedilecekSecenekler;

    HashMap<String,String> cevapSecenekMap;
    HashMap<String,String> soruCevapMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinav_olustur);

        cevapLabel = findViewById(R.id.textView5);
        dersAdi = findViewById(R.id.sinavEkleSinavAdEdit);
        sinavSure = findViewById(R.id.sinavEkleSinavSureEdit);
        sinavDurumGroup = findViewById(R.id.sinavEkleSinavDurumRadio);
        soru = findViewById(R.id.sinavEkleYeniSoruText);
        soruA = findViewById(R.id.soruA);
        soruB = findViewById(R.id.soruB);
        soruC = findViewById(R.id.soruC);
        soruD = findViewById(R.id.soruD);
        soruCevapGroup = findViewById(R.id.sinavEkleCevapRadio);
        soruyuKaydetButton = findViewById(R.id.sinavEkleSoruKaydetButton);
        yeniSoruButton = findViewById(R.id.sinavEkleYeniSoruButton);
        sinavOnizleButton = findViewById(R.id.sinavEkleOnizleButton);


        kaydedilecekSecenekler = new ArrayList<>();
        kaydedilecekSorular = new ArrayList<>();

        soru.setVisibility(View.INVISIBLE);
        soruA.setVisibility(View.INVISIBLE);
        soruB.setVisibility(View.INVISIBLE);
        soruC.setVisibility(View.INVISIBLE);
        soruD.setVisibility(View.INVISIBLE);
        soruCevapGroup.setVisibility(View.INVISIBLE);
        cevapLabel.setVisibility(View.INVISIBLE);
        soruyuKaydetButton.setVisibility(View.INVISIBLE);
        sinavOnizleButton.setVisibility(View.INVISIBLE);

    }



    public void yeniSoru(View view){

        soru.setVisibility(View.VISIBLE);
        soruA.setVisibility(View.VISIBLE);
        soruB.setVisibility(View.VISIBLE);
        soruC.setVisibility(View.VISIBLE);
        soruD.setVisibility(View.VISIBLE);
        soruCevapGroup.setVisibility(View.VISIBLE);
        cevapLabel.setVisibility(View.VISIBLE);
        yeniSoruButton.setVisibility(View.INVISIBLE);
        soruyuKaydetButton.setVisibility(View.VISIBLE);

        soru.setText("");
        soruA.setText("");
        soruB.setText("");
        soruC.setText("");
        soruD.setText("");
        soruCevapGroup.clearCheck();




    }


    public void soruKaydet(View view){

        if (soru.getText().toString().matches("") || soruA.getText().toString().matches("") || soruB.getText().toString().matches("") || soruC.getText().toString().matches("") || soruD.getText().toString().matches("") || soruCevapGroup.getCheckedRadioButtonId() == -1){

            Toast.makeText(SinavOlusturActivity.this,"Soruyla İlgili Alanları Eksiksiz Doldurun!!",Toast.LENGTH_LONG).show();

        }else{

            soruCevapMap = new HashMap<>();
            cevapSecenekMap = new HashMap<>();

            int seciliCevap = soruCevapGroup.getCheckedRadioButtonId();
            soruCevapRadio = findViewById(seciliCevap);

            String dCevap = soruCevapRadio.getText().toString();

            /*
            switch (dCevap){
                case "A" : {dCevap = soruA.getText().toString().trim();break;}
                case "B" : {dCevap = soruB.getText().toString().trim();break;}
                case "C" : {dCevap = soruC.getText().toString().trim();break;}
                case "D" : {dCevap = soruD.getText().toString().trim();break;}
            }
            */

            soruCevapMap.put(soru.getText().toString().trim(),dCevap);
            kaydedilecekSorular.add(soruCevapMap);

            cevapSecenekMap.put("A",soruA.getText().toString().trim());
            cevapSecenekMap.put("B",soruB.getText().toString().trim());
            cevapSecenekMap.put("C",soruC.getText().toString().trim());
            cevapSecenekMap.put("D",soruD.getText().toString().trim());
            kaydedilecekSecenekler.add(cevapSecenekMap);




            soru.setVisibility(View.INVISIBLE);
            soruA.setVisibility(View.INVISIBLE);
            soruB.setVisibility(View.INVISIBLE);
            soruC.setVisibility(View.INVISIBLE);
            soruD.setVisibility(View.INVISIBLE);
            soruCevapGroup.setVisibility(View.INVISIBLE);
            cevapLabel.setVisibility(View.INVISIBLE);
            yeniSoruButton.setVisibility(View.VISIBLE);
            soruyuKaydetButton.setVisibility(View.INVISIBLE);
            sinavOnizleButton.setVisibility(View.VISIBLE);

        }






    }

    public void sinavOnizle(View view){

        if (dersAdi.getText().toString().matches("")||sinavSure.getText().toString().matches("")||sinavDurumGroup.getCheckedRadioButtonId()==-1){

            Toast.makeText(SinavOlusturActivity.this,"Sınavla İlgili Alanları Eksiksiz Doldurun!!",Toast.LENGTH_LONG).show();

        }else{

            int seciliDurum = sinavDurumGroup.getCheckedRadioButtonId();
            sinavDurumRadio = findViewById(seciliDurum);

            String sinav_durum = sinavDurumRadio.getText().toString().trim();
            switch (sinav_durum){
                case "Hemen Yayınla":{sinav_durum = "1";break;}
                case  "Sonra Yayınla":{sinav_durum = "0";break;}
            }

            Intent intent = new Intent(SinavOlusturActivity.this,SinavOlusturOnizleActivity.class);
            intent.putExtra("onizleDersAd",dersAdi.getText().toString().trim());
            intent.putExtra("onizleSinavSure",sinavSure.getText().toString().trim());
            intent.putExtra("onizleSinavDurum",sinav_durum);
            intent.putExtra("onizleSorular",kaydedilecekSorular);
            intent.putExtra("onizleSecenekler",kaydedilecekSecenekler);
            startActivity(intent);

        }

    }



}
