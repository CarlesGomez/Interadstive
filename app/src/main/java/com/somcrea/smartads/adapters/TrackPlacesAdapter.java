package com.somcrea.smartads.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.somcrea.smartads.R;
import com.somcrea.smartads.models.Place;

import java.util.ArrayList;

/**
 * Created by Carles.
 */
public class TrackPlacesAdapter extends RecyclerView.Adapter<TrackPlacesAdapter.PlaceViewHolder>{

    //region Atributs
    ArrayList<Place> places;
    public static ImageLoader imgLoader=ImageLoader.getInstance();
    //endregion

    //Constructor:
    public TrackPlacesAdapter(ArrayList<Place> places){
        this.places = places;
    }

    //region EVENTS
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tracked_card_item, viewGroup, false);
        PlaceViewHolder pvh = new PlaceViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder placeViewHolder, int i) {
        imgLoader.displayImage(places.get(i).getUrl(), placeViewHolder.imageUrl);
        placeViewHolder.placeName.setText(places.get(i).getName());
        placeViewHolder.time.setText("From " + places.get(i).getEnterHour() + " to " + places.get(i).getExitHour());

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    //endregion

    //Class View Holder:
    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView imageUrl;
        TextView placeName;
        TextView time;

        PlaceViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            imageUrl = (ImageView)itemView.findViewById(R.id.place_photo);
            placeName = (TextView)itemView.findViewById(R.id.txt_place);
            time = (TextView)itemView.findViewById(R.id.txt_time);
        }
    }
}
