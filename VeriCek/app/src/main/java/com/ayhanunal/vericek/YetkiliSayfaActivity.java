package com.ayhanunal.vericek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class YetkiliSayfaActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.yetkili_sayfa_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.yetkiliCikis){
            firebaseAuth.signOut();
            Intent giriseYonlendir = new Intent(YetkiliSayfaActivity.this,MainActivity.class);
            startActivity(giriseYonlendir);
            finish();
        }
        else if (item.getItemId() == R.id.bekleyenKullanicilarGit){
            Intent bekleyenKullanicilarIntent = new Intent(YetkiliSayfaActivity.this,BekleyenKullanicilar.class);
            bekleyenKullanicilarIntent.putExtra("gelmeAmaci","onayKul");
            startActivity(bekleyenKullanicilarIntent);
        }
        else if (item.getItemId() == R.id.sinavSayfasinaGit){
            Intent sinavSayfasiIntent = new Intent(YetkiliSayfaActivity.this,YetkiliSinavActivity.class);
            startActivity(sinavSayfasiIntent);
        }else if(item.getItemId() == R.id.toplamKullanicilarGit){
            Intent toplamKullanicilarIntent = new Intent(YetkiliSayfaActivity.this,BekleyenKullanicilar.class);
            toplamKullanicilarIntent.putExtra("gelmeAmaci","topKul");
            startActivity(toplamKullanicilarIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yetkili_sayfa);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}
