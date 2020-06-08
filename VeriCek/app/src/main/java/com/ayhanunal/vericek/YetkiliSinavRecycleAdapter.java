package com.ayhanunal.vericek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class YetkiliSinavRecycleAdapter extends RecyclerView.Adapter<YetkiliSinavRecycleAdapter.PostHolder> {

    private ArrayList<String> Sinavlar;
    private ArrayList<String> Aktiflik;
    private ArrayList<String> Kullanicilar;

    public YetkiliSinavRecycleAdapter(ArrayList<String> sinavlar, ArrayList<String> aktiflik, ArrayList<String> kullanicilar) {
        Sinavlar = sinavlar;
        Aktiflik = aktiflik;
        Kullanicilar = kullanicilar;
    }

    @NonNull
    @Override
    public YetkiliSinavRecycleAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.sinav_recyle_satir,parent,false);
        return new PostHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        String durum;

        if (Aktiflik.get(position).matches("1")){
            durum = "Aktif";
        }else {
            durum = "Pasif";
        }

        holder.sinavAdi.setText("Sınav Adı : " + Sinavlar.get(position));
        holder.sinavDurum.setText("Durum : " + durum);
        holder.sinavSayi.setText("Kullanıcı Sayısı : " + Kullanicilar.get(position));

    }

    @Override
    public int getItemCount() {
        return Sinavlar.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        TextView sinavAdi;
        TextView sinavDurum;
        TextView sinavSayi;

        public PostHolder(@NonNull View itemView){
            super(itemView);

            sinavAdi = itemView.findViewById(R.id.yetSinavAdiText);
            sinavDurum = itemView.findViewById(R.id.yetSinavDurumText);
            sinavSayi = itemView.findViewById(R.id.yetSinavKullaniciText);

        }

    }


}
