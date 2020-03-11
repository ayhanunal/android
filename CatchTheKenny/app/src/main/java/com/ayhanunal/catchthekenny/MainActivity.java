package com.ayhanunal.catchthekenny;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView timeText;
    TextView scoreText;
    int score;

    ImageView imageView;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ImageView imageView9;

    ImageView[] imageArray; //resimlerin kaybolmasi icin diziye atip islem yapmamiz gerekiyor.

    Handler handler;
    Runnable runnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize-baslatma
        timeText = (TextView) findViewById(R.id.timeText);
        scoreText = (TextView) findViewById(R.id.scoreText);
        score=0;

        imageView=findViewById(R.id.imageView);
        imageView2=findViewById(R.id.imageView2);
        imageView3=findViewById(R.id.imageView3);
        imageView4=findViewById(R.id.imageView4);
        imageView5=findViewById(R.id.imageView5);
        imageView6=findViewById(R.id.imageView6);
        imageView7=findViewById(R.id.imageView7);
        imageView8=findViewById(R.id.imageView8);
        imageView9=findViewById(R.id.imageView9);

        imageArray = new ImageView[] {imageView,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9};
        //buu bir döngüye alip gorunmez yapicaz sonra random olarak 1 tanesini gorunur yapicz.

        hideImages();

        //uygulama acildigi andan itibaren geriye saymasini istedigimiz icin CountDownTimer 'i oncreate altinda yaziyoruz.
        new CountDownTimer(10000, 1000) {   //10sn den geriye 1sn 1sn saymasi icin;
            @Override
            public void onTick(long l) {
                timeText.setText("Time :"+l/1000);
            }

            @Override
            public void onFinish() {

                timeText.setText("Time Off");
                handler.removeCallbacks(runnable); //runnable yi durduruyoruz ve kenny hareket etmiyor.
                for(ImageView image : imageArray){
                    image.setVisibility(View.INVISIBLE); //tum resimleri tekrar gizliyoruz.
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                alert.setTitle("Restart?");
                alert.setMessage("are you sure to restart game?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //yes ' tıklarsa restart aticaz.

                        Intent intent = getIntent(); //baska sayfaya gecemek icin kullaniyoruz ama ayni sayfaya tekrar geldigimiz icin intent ile ayni sayfaya tekrar getirdik.
                        finish(); //guncel aktiviteyi bitirecek
                        startActivity(intent); //ayni aktiviteyi tekrar baslatiyoruz.
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //no'ya basarsa oyun bitti mesaji verdik.
                        Toast.makeText(MainActivity.this,"Game Over!",Toast.LENGTH_LONG).show();
                    }
                });
                alert.show();
            }
        }.start();




    }

    public void increaseScor(View view){
        score++; //her tiklandiginda scor 1 aratacak.
        scoreText.setText("Score :"+score); //ardindan score yazisi guncellenecek.
    }

    public void hideImages(){

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                for(ImageView image : imageArray){
                    image.setVisibility(View.INVISIBLE); //resimler gorunmez oldu.
                }

                Random random = new Random();
                int i = random.nextInt(9); //0-8 arasinda rastgele eleman getirir.
                imageArray[i].setVisibility(View.VISIBLE); //rastegele secilen resim gorunur oldu.

                handler.postDelayed(this,300); //zaten runnable icinde oldugumuz icin this yazdik. runnable de yazabilirdik-1sn de runnable calisir.


            }
        };
        handler.post(runnable); //runnable baslatmak icin bunu yazmamız gerekiyor.




    }
}
