package com.ayhanunal.gelismisharita;

public class Locations {

    private String sehirAdi;
    private String ilceAdi;
    private String sokakAdi;
    private String eklenmeTarihi;

    private String placeLatitude;
    private String placeLongitude;
    private boolean favPlace;
    private String placeBaslik;

    public Locations(String sehirAdi, String ilceAdi, String sokakAdi, String eklenmeTarihi, String placeLatitude, String placeLongitude) {
        this.sehirAdi = sehirAdi;
        this.ilceAdi = ilceAdi;
        this.sokakAdi = sokakAdi;
        this.eklenmeTarihi = eklenmeTarihi;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.favPlace = false;
        this.placeBaslik = "";
    }


    public Locations(String sehirAdi, String ilceAdi, String sokakAdi, String eklenmeTarihi, String placeLatitude, String placeLongitude, boolean favPlace, String placeBaslik) {
        this.sehirAdi = sehirAdi;
        this.ilceAdi = ilceAdi;
        this.sokakAdi = sokakAdi;
        this.eklenmeTarihi = eklenmeTarihi;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.favPlace = favPlace;
        this.placeBaslik = placeBaslik;
    }



    public String getSehirAdi() {
        return sehirAdi;
    }

    public void setSehirAdi(String sehirAdi) {
        this.sehirAdi = sehirAdi;
    }

    public String getIlceAdi() {
        return ilceAdi;
    }

    public void setIlceAdi(String ilceAdi) {
        this.ilceAdi = ilceAdi;
    }

    public String getSokakAdi() {
        return sokakAdi;
    }

    public void setSokakAdi(String sokakAdi) {
        this.sokakAdi = sokakAdi;
    }

    public String getEklenmeTarihi() {
        return eklenmeTarihi;
    }

    public void setEklenmeTarihi(String eklenmeTarihi) {
        this.eklenmeTarihi = eklenmeTarihi;
    }

    public String getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(String placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public String getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(String placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public boolean isFavPlace() {
        return favPlace;
    }

    public void setFavPlace(boolean favPlace) {
        this.favPlace = favPlace;
    }

    public String getPlaceBaslik() {
        return placeBaslik;
    }

    public void setPlaceBaslik(String placeBaslik) {
        this.placeBaslik = placeBaslik;
    }
}
