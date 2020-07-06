package com.ayhanunal.gelismisharita;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.internal.DialogRedirect;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TooManyListenersException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    static SQLiteDatabase database;

    Intent intent;
    String info;
    int locPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();
        info = intent.getStringExtra("state");
        if (info.matches("eski")){
            locPosition = intent.getIntExtra("listLocationIx",0);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        if(info.matches("yeni")){

            mMap.clear();


            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("com.ayhanunal.gelismisharita",MODE_PRIVATE);
                    boolean firstTimeCheck = sharedPreferences.getBoolean("notFirstTime",false);

                    if(!firstTimeCheck){
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("notFirstTime",true).apply();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };


            if(Build.VERSION.SDK_INT >= 23){

                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

                }else {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    mMap.clear();

                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(lastLocation != null){

                        LatLng userLastLoc = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLoc,15));

                    }

                }

            }else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                }

            }

        }else if(info.matches("eski")){
            mMap.clear();

            Double selectedLat = Double.valueOf(HomeActivity.location.get(locPosition).getPlaceLatitude());
            Double selectedLng = Double.valueOf(HomeActivity.location.get(locPosition).getPlaceLongitude());
            LatLng selectedLoc = new LatLng(selectedLat,selectedLng);
            mMap.addMarker(new MarkerOptions().position(selectedLoc).title(HomeActivity.location.get(locPosition).getIlceAdi()+"/"+HomeActivity.location.get(locPosition).getSokakAdi()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLoc,15));

        }else if(info.matches("tum")){

            mMap.clear();

            if (HomeActivity.location.size() > 0 && HomeActivity.location != null){

                Double listLat = Double.valueOf(HomeActivity.location.get(0).getPlaceLatitude());
                Double listLng = Double.valueOf(HomeActivity.location.get(0).getPlaceLongitude());
                LatLng userLastLocation = new LatLng(listLat,listLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));

                for (Locations locations : HomeActivity.location){
                    LatLng allLocation = new LatLng(Double.valueOf(locations.getPlaceLatitude()),Double.valueOf(locations.getPlaceLongitude()));
                    mMap.addMarker(new MarkerOptions().position(allLocation).title(locations.getIlceAdi()+"/"+locations.getSokakAdi()));
                }

            }else {
                Toast.makeText(MapsActivity.this,"Kayıtlı Konum Yok !!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MapsActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }


        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults != null){
            if (requestCode == 1){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    if(info.matches("yeni")){
                        mMap.clear();
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                        }
                    }else if(info.matches("eski")){
                        //eskiden gelen intentler
                        mMap.clear();

                        Double selectedLat = Double.valueOf(HomeActivity.location.get(locPosition).getPlaceLatitude());
                        Double selectedLng = Double.valueOf(HomeActivity.location.get(locPosition).getPlaceLongitude());
                        LatLng selectedLoc = new LatLng(selectedLat,selectedLng);
                        mMap.addMarker(new MarkerOptions().position(selectedLoc).title(HomeActivity.location.get(locPosition).getIlceAdi()+"/"+HomeActivity.location.get(locPosition).getSokakAdi()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLoc,15));

                    }else if (info.matches("tum")){
                        //tum maplarden gelen intentler.
                        mMap.clear();

                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        LatLng userLastLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));

                        for (Locations locations : HomeActivity.location){
                            LatLng allLocation = new LatLng(Double.valueOf(locations.getPlaceLatitude()),Double.valueOf(locations.getPlaceLongitude()));
                            mMap.addMarker(new MarkerOptions().position(allLocation).title(locations.getIlceAdi()+"/"+locations.getSokakAdi()));
                        }
                    }


                }
            }
        }

    }


    @Override
    public void onMapLongClick(final LatLng latLng) {


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String sehir = "";
        String ilce = "";
        String sokak = "";
        String tarih = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        tarih = dateFormat.format(date);



        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            //System.out.println("deneme ilce"+addressList.get(0).getSubAdminArea()); //ilce
            if(addressList != null && addressList.size() > 0){
                if(addressList.get(0).getAdminArea() != null){
                    sehir += addressList.get(0).getAdminArea();
                }else {
                    sehir = "Tanımsız Şehir";
                }
                if (addressList.get(0).getSubAdminArea() != null){
                    ilce += addressList.get(0).getSubAdminArea();
                }else {
                    ilce = "Tanımsız İlçe";
                }
                if (addressList.get(0).getThoroughfare() != null){
                    sokak += addressList.get(0).getThoroughfare();
                    sokak += " ";
                    if (addressList.get(0).getSubThoroughfare() != null){
                        sokak += addressList.get(0).getSubThoroughfare();
                    }
                }else {
                    sokak = "Tanımsız Sokak";
                }
            }else {
                sehir = "Tanımsız Şehir";
                sokak = "Tanımsız Sokak";
            }


        }catch (Exception e){
            e.printStackTrace();
        }




        mMap.addMarker(new MarkerOptions().title(ilce+"/"+sokak).position(latLng));




        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kaydet");
        alert.setMessage("Konum Kaydedilecektir. Emin Misiniz ?");
        final String finalSehir = sehir;
        final String finalIlce = ilce;
        final String finalSokak = sokak;
        final String finalTarih = tarih;
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    database = MapsActivity.this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS Place(PlaceID INTEGER PRIMARY KEY,PlaceLat VARCHAR,PlaceLng VARCHAR,PlaceCity VARCHAR,PlaceTown VARCHAR,PlaceStreet VARCHAR,UploadDate VARCHAR,FavLoc INTEGER,PlaceTitle VARCHAR)");
                    String toCompile = "INSERT INTO Place (PlaceLat,PlaceLng,PlaceCity,PlaceTown,PlaceStreet,UploadDate,FavLoc,PlaceTitle) VALUES (?, ?, ?, ?, ?, ?, 0, ?)";

                    SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);
                    sqLiteStatement.bindString(1,String.valueOf(latLng.latitude));
                    sqLiteStatement.bindString(2,String.valueOf(latLng.longitude));
                    sqLiteStatement.bindString(3, finalSehir);
                    sqLiteStatement.bindString(4, finalIlce);
                    sqLiteStatement.bindString(5, finalSokak);
                    sqLiteStatement.bindString(6, finalTarih);
                    sqLiteStatement.bindString(7,"");

                    sqLiteStatement.execute();


                    Toast.makeText(MapsActivity.this,"Yer Kaydedildi",Toast.LENGTH_LONG).show();
                    HomeActivity.location.add(0,new Locations(finalSehir,finalIlce,finalSokak,finalTarih,String.valueOf(latLng.latitude),String.valueOf(latLng.longitude)));
                    HomeActivity.adapter.notifyDataSetChanged();

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


    }
}