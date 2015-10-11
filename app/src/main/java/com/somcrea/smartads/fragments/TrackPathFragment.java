package com.somcrea.smartads.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.somcrea.smartads.R;
import com.somcrea.smartads.adapters.TrackPlacesAdapter;
import com.somcrea.smartads.models.Place;
import com.somcrea.smartads.sqlite.DatabaseDmlHelper;
import com.somcrea.smartads.sqlite.SmartAdsOpenHelper;

import java.util.ArrayList;

/**
 * Created by rubengrafgarcia.
 */
public class TrackPathFragment extends Fragment {

    //Atributs:
    private ArrayList<Place> places = new ArrayList<Place>();
    private SmartAdsOpenHelper smaOpenHelper;
    private SQLiteDatabase smaDB;
    private DatabaseDmlHelper ddmh;

    //region EVENTS
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        smaOpenHelper = SmartAdsOpenHelper.getInstance(getActivity());
        smaDB = smaOpenHelper.getWritableDatabase();
        ddmh = new DatabaseDmlHelper(this.smaDB);

        Place p1 = new Place("Plaza Norte 2","http://sompartyapp.com/smart_ads/img/plazanorte.jpg","10:00","14:00" );
        Place p2 = new Place("Metallica concert","http://sompartyapp.com/smart_ads/img/metallica.jpg","12:00","15:00" );

        places.add(p1);
        places.add(p2);

        Place place = ddmh.getMorePlaces();
        if(!place.getName().equals(""))
            places.add(place);

        View v =inflater.inflate(R.layout.fragment_for_track_path,container,false);
        RecyclerView rv = (RecyclerView)v.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        TrackPlacesAdapter adapter = new TrackPlacesAdapter(places);
        rv.setAdapter(adapter);

        return v;
    }
    //endregion
}
