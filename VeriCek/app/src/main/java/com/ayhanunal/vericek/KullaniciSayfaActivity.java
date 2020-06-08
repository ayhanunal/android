package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.zip.Inflater;

public class KullaniciSayfaActivity extends AppCompatActivity {

    Intent menuIntent;

    String icerdekiKullaniciID;
    String icerdekiKullaniciSifre;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.kullanici_sayfa_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.kullaniciBilgiDuzenle){
            menuIntent = new Intent(KullaniciSayfaActivity.this,KullaniciBilgileriGoruntule.class);
            menuIntent.putExtra("icerdekiKullaniciID",icerdekiKullaniciID);
            menuIntent.putExtra("icerdekiKullaniciSifre",icerdekiKullaniciSifre);
            startActivity(menuIntent);

        }else if (item.getItemId() == R.id.kullaniciSinavaGir){

            menuIntent = new Intent(KullaniciSayfaActivity.this,KullaniciSinavGir.class);
            menuIntent.putExtra("icerdekiKullaniciID",icerdekiKullaniciID);
            startActivity(menuIntent);

        }else if (item.getItemId() == R.id.kullaniciSinavSonuc){

            menuIntent = new Intent(KullaniciSayfaActivity.this,YetkiliSinavSonucGoruntule.class);
            menuIntent.putExtra("geldigimSayfa","kulSinavSonucGor");
            menuIntent.putExtra("icerdekiKullanici",icerdekiKullaniciID);
            startActivity(menuIntent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_sayfa);

        Intent intent = getIntent();
        icerdekiKullaniciID = intent.getStringExtra("icerdekiKullaniciID");
        icerdekiKullaniciSifre = intent.getStringExtra("icerdekiKullaniciSifre");

    }
}
