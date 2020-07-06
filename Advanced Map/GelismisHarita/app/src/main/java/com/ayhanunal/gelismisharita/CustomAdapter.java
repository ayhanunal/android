package com.ayhanunal.gelismisharita;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private LayoutInflater locationsInflater;
    private List<Locations> locationsList;



    public CustomAdapter(Activity activity, List<Locations> locationsList){
        locationsInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.locationsList = locationsList;
    }

    @Override
    public int getCount() {
        return locationsList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View lineView;
        lineView = locationsInflater.inflate(R.layout.home_custom_layout, null);
        TextView sehirBilgisiText = (TextView) lineView.findViewById(R.id.sehirBilgisi2);
        TextView sokakBilgisiText = (TextView) lineView.findViewById(R.id.sokakBilgisi2);
        TextView eklenmeTarihiText = (TextView) lineView.findViewById(R.id.eklenmeTarihi2);
        ImageView favLocationImage = (ImageView) lineView.findViewById(R.id.favImageView);

        Locations locations = locationsList.get(position);



        if (locations.getPlaceBaslik().matches("")){
            sehirBilgisiText.setText(locations.getSehirAdi() + "/" + locations.getIlceAdi());
            sokakBilgisiText.setText(locations.getSokakAdi());
        }else {
            sehirBilgisiText.setText(locations.getPlaceBaslik());
            String sehirIlce = locations.getSehirAdi() + "/" + locations.getIlceAdi() + "/";
            sokakBilgisiText.setText(sehirIlce+locations.getSokakAdi());
        }

        eklenmeTarihiText.setText(locations.getEklenmeTarihi());

        if (!locations.isFavPlace()){
            favLocationImage.setVisibility(View.INVISIBLE);
        }else favLocationImage.setVisibility(View.VISIBLE);



        return lineView;
    }
}
