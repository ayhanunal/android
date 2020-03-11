package com.ayhanunal.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

public class SurvivorBird extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;
	Texture bird;
	Texture bee1;
	Texture bee2;
	Texture bee3;

	float birdX = 0;
	float birdY = 0;

	int gameState = 0; //oyunun baslayip baslamadigini kontrol ettigimiz degisken.

    float velocity = 0;
    float gravity = 0.8f;
    float enemyVelocity = 6; //her olusturulan ari seti icin velocity tanimladik.

	Random random;

	int score = 0;
	int scoredEnemy = 0;

	BitmapFont font;
	BitmapFont font2;


	//sekillerin etrafina bir circle ciziyoruz carpismalari kontrol edebilmek icin.
	Circle birdCircle;
	Circle[] enemyCircles1;
    Circle[] enemyCircles2;
    Circle[] enemyCircles3;

    //ShapeRenderer shapeRenderer;





    int numberOfEnemies = 4; //4 farklı ari seti olucak.
	float[] enemyX = new float[numberOfEnemies];
	float[] enemyOffSet1 = new float[numberOfEnemies];
	float[] enemyOffSet2 = new float[numberOfEnemies];
	float[] enemyOffSet3 = new float[numberOfEnemies];
	float distance = 0; //ari setleri arasindaki mesafe

	
	@Override
	public void create () { //onCreate gibi düsünülebilir. Oyun basladiginda ne olacaksa buraya yaziyoruz.

		batch = new SpriteBatch();
		background = new Texture("background.png");
		bird = new Texture("bird.png");
		bee1 = new Texture("bee.png");
		bee2 = new Texture("bee.png");
		bee3 = new Texture("bee.png");

		distance = Gdx.graphics.getWidth()/2; //ekranin yarisi kadar fark olsun ari seti arasinda

		random = new Random();

		birdX = Gdx.graphics.getWidth()/2 - bird.getHeight()/2;
		birdY = Gdx.graphics.getHeight()/3;


		//shapeRenderer = new ShapeRenderer();

		birdCircle = new Circle();
		enemyCircles1 = new Circle[numberOfEnemies];
		enemyCircles2 = new Circle[numberOfEnemies];
		enemyCircles3 = new Circle[numberOfEnemies];


		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(4);

		font2 = new BitmapFont();
		font2.setColor(Color.WHITE);
		font2.getData().setScale(6);

		for(int i = 0; i<numberOfEnemies; i++){

			enemyOffSet1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
			enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
			enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
			//-0.5f yapmamızın sebebi random uretilen sonuc 0.5 den büyükse pozitif, kucukse negatif bir deger ciksin ki arilarin konumları ekranın ortasinin altinda ve ustunde olabilsim.

			enemyX[i] = Gdx.graphics.getWidth() - bee1.getWidth() / 2 + i * distance ;

			enemyCircles1[i] = new Circle();
            enemyCircles2[i] = new Circle();
            enemyCircles3[i] = new Circle();

		}


	}

	@Override
	public void render () { //oyun devam ettigi sürece cagrilan kodlar.


        batch.begin(); //çizilecek seyleri batch.begin ile batch.end arasina yaziyoruz.

        batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight()); //cizme islemi yapiyoruz. arkaplan tüm sayfayı kaplamasi icin 0,0 noktasindan baslatip genislik ve yüksekligi ekran kadar yapiyoruzç


	    if(gameState == 1){ //oyun baslamissa


	        if(enemyX[scoredEnemy] < Gdx.graphics.getWidth()/2 - bird.getHeight()/2){
	            //arilar kustan daha geride bir yerdeyse score 1 artacak
                score++;

                if(scoredEnemy < numberOfEnemies - 1){
                    scoredEnemy++;
                }else{
                    scoredEnemy = 0;
                }
            }


			if(Gdx.input.justTouched()){ //ekrana tiklaninca kus zipliyor.
				velocity = -15;
			}


			for(int i = 0; i<numberOfEnemies; i++){

			    if(enemyX[i] < Gdx.graphics.getWidth()/15){ // arinin x'i 0'ın altına veya kusun width nin altina indiyse yani arilar ekranin en soluna geldiyse

                    enemyX[i] = enemyX[i] + numberOfEnemies * distance; //basa aldik. yani ari setleri soldan kaybolup sagdan tekrar basliyor.

					enemyOffSet1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
					enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
					enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
					//-0.5f yapmamızın sebebi random uretilen sonuc 0.5 den büyükse pozitif, kucukse negatif bir deger ciksin ki arilarin konumları ekranın ortasinin altinda ve ustunde olabilsim.


                }else{
                    enemyX[i] = enemyX[i] - enemyVelocity;
                }



				batch.draw(bee1,enemyX[i],Gdx.graphics.getHeight()/2 + enemyOffSet1[i],Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
				batch.draw(bee2,enemyX[i],Gdx.graphics.getHeight()/2 + enemyOffSet2[i],Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
				batch.draw(bee3,enemyX[i],Gdx.graphics.getHeight()/2 + enemyOffSet3[i],Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);


				enemyCircles1[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth()/30, Gdx.graphics.getHeight()/2 + enemyOffSet1[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				enemyCircles2[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth()/30, Gdx.graphics.getHeight()/2 + enemyOffSet2[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				enemyCircles3[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth()/30, Gdx.graphics.getHeight()/2 + enemyOffSet3[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);

			}











	        if(birdY > 0) {
                velocity = velocity + gravity; //her render calistiginda velocity degerine 0.1 degeri olan gravity degeri eklenecek.
                birdY = birdY - velocity; //sürekli olarak kusun yuksekliginden velocity cikarilacak ve kus asagi düsecek.
            }else {
	            gameState = 2;
            }


        }else if (gameState == 0){ //oyun baslamamıssa

            if(Gdx.input.justTouched()){ //ekrana tiklanmissa
                gameState = 1;
            }

        }else if(gameState == 2){
	        //oyun bittyise;

            font2.draw(batch,"Game Over! Tap To Play Again!",100,Gdx.graphics.getHeight()/2);

            if(Gdx.input.justTouched()){ //ekrana tiklanmissa
                gameState = 1;

                birdY = Gdx.graphics.getHeight()/3;

                for(int i = 0; i<numberOfEnemies; i++){

                    enemyOffSet1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
                    enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
                    enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getWidth() - 200); //0-1 arasinda bir yüzde vericek.(random.nextFloat)
                    //-0.5f yapmamızın sebebi random uretilen sonuc 0.5 den büyükse pozitif, kucukse negatif bir deger ciksin ki arilarin konumları ekranın ortasinin altinda ve ustunde olabilsim.

                    enemyX[i] = Gdx.graphics.getWidth() - bee1.getWidth() / 2 + i * distance ;

                    enemyCircles1[i] = new Circle();
                    enemyCircles2[i] = new Circle();
                    enemyCircles3[i] = new Circle();

                }

                velocity = 0;
                scoredEnemy = 0;
                score = 0;

            }

        }






		batch.draw(bird,birdX,birdY,Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10); //kusun baslangic noktasini ekranın genisligi
        //ve yüksekligine gore oranlayarak baslangıc noktasini belirliyoruz. kusun genislik ve yüksekligini ise yine ekranın genisligi ve yuksekligine gore oranliyoruzz.

        font.draw(batch,String.valueOf(score),100,200);

		batch.end();

		birdCircle.set(birdX + Gdx.graphics.getWidth()/30,birdY + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);



		for(int i = 0;i<numberOfEnemies;i++){
			//shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth()/30, Gdx.graphics.getHeight()/2 + enemyOffSet1[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
			//shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth()/30, Gdx.graphics.getHeight()/2 + enemyOffSet2[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
			//shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth()/30, Gdx.graphics.getHeight()/2 + enemyOffSet3[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);


			if(Intersector.overlaps(birdCircle,enemyCircles1[i]) || Intersector.overlaps(birdCircle,enemyCircles2[i])|| Intersector.overlaps(birdCircle,enemyCircles3[i])){
				//carpismalar kontrol ediliyor.
				gameState = 2; //oyun bitti.
			}

		}


	}
	
	@Override
	public void dispose () {

	}
}
