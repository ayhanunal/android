package com.ayhanunal.gelismisharita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    static List<Locations> location = new ArrayList<Locations>();
    static CustomAdapter adapter;
    ListView listView;

    //ImageView favLocationImage;
    //EditText sehirBilgisiText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.konumEkle){

            Intent intent = new Intent(HomeActivity.this,MapsActivity.class);
            intent.putExtra("state","yeni");
            startActivity(intent);

        }else if(item.getItemId() == R.id.tumKonum){

            Intent intent = new Intent(HomeActivity.this,MapsActivity.class);
            intent.putExtra("state","tum");
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView = findViewById(R.id.home_list_view);
        //favLocationImage = findViewById(R.id.favImageView);
        //sehirBilgisiText = findViewById(R.id.sehirBilgisi2);
        //sehirBilgisiText.setFocusable(false);

        dbBaglanVeriCek();


        adapter = new CustomAdapter(this,location);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(HomeActivity.this,MapsActivity.class);
                intent.putExtra("state","eski");
                intent.putExtra("listLocationIx",position);
                startActivity(intent);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {

                PopupMenu popupMenu = new PopupMenu(HomeActivity.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.home_activity_popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId() == R.id.deleteLocation){

                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                            alert.setTitle("Sil");
                            alert.setMessage("Konum Silinecektir. Emin Misiniz?");
                            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    try {
                                        MapsActivity.database = HomeActivity.this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
                                        MapsActivity.database.delete("Place","PlaceLat=? AND PlaceLng=?",new String[]{location.get(position).getPlaceLatitude(),location.get(position).getPlaceLongitude()});

                                        location.remove(position);
                                        adapter.notifyDataSetChanged();

                                        Toast.makeText(HomeActivity.this,"Konum Silindi",Toast.LENGTH_SHORT).show();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                            alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();

                                }
                            });
                            alert.show();

                        }else if (item.getItemId() == R.id.favLocation){
                            if(!location.get(position).isFavPlace()){

                                try {
                                    ContentValues cv = new ContentValues();
                                    cv.put("FavLoc",1);
                                    MapsActivity.database = HomeActivity.this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
                                    MapsActivity.database.update("Place",cv,"PlaceLat=? AND PlaceLng=?",new String[]{location.get(position).getPlaceLatitude(),location.get(position).getPlaceLongitude()});

                                    location.get(position).setFavPlace(true);
                                    //favLocationImage.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();


                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else {
                                try {
                                    ContentValues cv = new ContentValues();
                                    cv.put("FavLoc",0);
                                    MapsActivity.database = HomeActivity.this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
                                    MapsActivity.database.update("Place",cv,"PlaceLat=? AND PlaceLng=?",new String[]{location.get(position).getPlaceLatitude(),location.get(position).getPlaceLongitude()});

                                    location.get(position).setFavPlace(false);
                                    //favLocationImage.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();


                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }else if (item.getItemId() == R.id.isimDegistir){
                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                            alert.setTitle("Başlık");

                            final EditText input = new EditText(HomeActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            alert.setView(input);
                            alert.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String baslik = input.getText().toString();
                                    baslik = kelimeIlkHarfBuyut(baslik);

                                    try {
                                        ContentValues cv = new ContentValues();
                                        cv.put("PlaceTitle",baslik);
                                        MapsActivity.database = HomeActivity.this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
                                        MapsActivity.database.update("Place",cv,"PlaceLat=? AND PlaceLng=?",new String[]{location.get(position).getPlaceLatitude(),location.get(position).getPlaceLongitude()});

                                        location.get(position).setPlaceBaslik(baslik);
                                        adapter.notifyDataSetChanged();

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                            alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alert.show();

                        }

                        return true;
                    }
                });

                popupMenu.show();

                return true;
            }
        });









    }

    public String kelimeIlkHarfBuyut(String kelime){
        String yeniKelime = "";
        String[] kelimeler = kelime.split(" ");
        for (String wd : kelimeler){
            yeniKelime += wd.substring(0,1).toUpperCase();
            yeniKelime += wd.substring(1);
            yeniKelime += " ";
        }
        yeniKelime = yeniKelime.trim();
        return yeniKelime;

    }

    public void dbBaglanVeriCek(){

        try {
            MapsActivity.database = this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
            Cursor cursor = MapsActivity.database.rawQuery("SELECT * FROM Place ORDER BY PlaceID DESC",null);
            while (cursor.moveToNext()){

                String dbPlaceLat = cursor.getString(cursor.getColumnIndex("PlaceLat"));
                String dbPlaceLng = cursor.getString(cursor.getColumnIndex("PlaceLng"));
                String dbPlaceCity = cursor.getString(cursor.getColumnIndex("PlaceCity"));
                String dbPlaceTown = cursor.getString(cursor.getColumnIndex("PlaceTown"));
                String dbPlaceStreet = cursor.getString(cursor.getColumnIndex("PlaceStreet"));
                String dbUploadDate = cursor.getString(cursor.getColumnIndex("UploadDate"));
                int dbFavLoc = cursor.getInt(cursor.getColumnIndex("FavLoc"));
                boolean fav;
                if (dbFavLoc == 0){
                    fav = false;
                }else {
                    fav = true;
                }

                String dbPlaceTitle = cursor.getString(cursor.getColumnIndex("PlaceTitle"));

                location.add(new Locations(dbPlaceCity,dbPlaceTown,dbPlaceStreet,dbUploadDate,dbPlaceLat,dbPlaceLng,fav,dbPlaceTitle));


            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }



    }

}