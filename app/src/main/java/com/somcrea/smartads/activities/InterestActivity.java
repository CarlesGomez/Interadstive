package com.somcrea.smartads.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.manuelpeinado.multichoiceadapter.CheckableImageView;
import com.somcrea.smartads.R;
import com.somcrea.smartads.adapters.InterestsAdapter;
import com.somcrea.smartads.models.Interest;
import com.somcrea.smartads.server.Connections;
import com.somcrea.smartads.sqlite.SmartAdsOpenHelper;
import com.somcrea.smartads.utils.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ruben.
 */

public class InterestActivity extends ActionBarActivity {

    //region ATRIBUTS
    InterestsAdapter adapter;
    ListView listView;
    private SmartAdsOpenHelper smaOpenHelper;
    private SQLiteDatabase smaDB;

    List<Map<String, List<Interest>>> items = new ArrayList<Map<String, List<Interest>>>();
    private ArrayList<String> checkedInterests;
    private String userId;
    private Boolean myFirstTimeInstallation;
    //endregion ATRIBUTS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_interest);

        try {
            //Extras:
            Intent i = this.getIntent();
            Bundle extras = i.getExtras();
            userId = extras.getString("userId");
            myFirstTimeInstallation = extras.getBoolean("myFirstTimeInstallation");

            //Database:
            smaOpenHelper = SmartAdsOpenHelper.getInstance(getApplicationContext());
            smaDB = smaOpenHelper.getWritableDatabase();

            //List view and adapters.
            listView = (ListView) findViewById(R.id.listView);
            initItems();
            checkedInterests = Interest.getInterestsByUserId(userId, smaDB);
            adapter = new InterestsAdapter(this, R.layout.interest_item, items, 2, mItemClickListener, checkedInterests);
            listView.setAdapter(adapter);
        }catch (Exception e){e.printStackTrace();}
    }

    //Click from the adapter.
    View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int position = (Integer) v.getTag(R.id.row);
                int col = (Integer) v.getTag(R.id.col);

                Map<String, List<Interest>> map = adapter.getItem(position);
                List<Interest> list = map.get(adapter.getItemTypeAtPosition(position));

                Interest model = (Interest) list.get(col);

                CheckableImageView chkIv = (CheckableImageView) v.findViewWithTag("image");
                chkIv.setChecked(chkIv.isChecked() ? false : true);

                if (chkIv.isChecked())
                    checkedInterests.add(model.getId());
                else
                    checkedInterests.remove(model.getId());

                adapter.setCheckedInterests(checkedInterests);
            }catch (Exception e){e.printStackTrace();}
        }
    };

    //region METODES
    //Inicialitza els items del adapter.
    private void initItems(){

        List<String> itemTypesListMine = new ArrayList<String>();
        itemTypesListMine.add(AppConstants.CLOTHES);
        itemTypesListMine.add(AppConstants.BRANDS);
        itemTypesListMine.add(AppConstants.STYLES);

        List<Interest> arrayClothes = Interest.getInterests(AppConstants.CLOTHES, smaDB);
        Map<String, List<Interest>> mapClothes = new HashMap<String, List<Interest>>();
        mapClothes.put(AppConstants.CLOTHES, arrayClothes);
        items.add(mapClothes);

        List<Interest> arrayBrands = Interest.getInterests(AppConstants.BRANDS, smaDB);
        Map<String, List<Interest>> mapBrands = new HashMap<String, List<Interest>>();
        mapBrands.put(AppConstants.BRANDS, arrayBrands);
        items.add(mapBrands);

        List<Interest> arrayStyles = Interest.getInterests(AppConstants.STYLES, smaDB);
        Map<String, List<Interest>> mapStyles = new HashMap<String, List<Interest>>();
        mapStyles.put(AppConstants.STYLES, arrayStyles);
        items.add(mapStyles);
    }

    //Envia els interessos d'un usuari al servidor.
    private void sendInterestsToServer()
    {
        AsyncTask<Void, Void, Void> taskForSaveInterestsInServer = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Connections con = new Connections(AppConstants.URL_MOBILE_CONTROLLER);
                con.registerUserLikes(userId, checkedInterests);
                return null;
            }
        }.execute();
    }
    //endregion

    //region EVENTS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_interest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done_interests) {
            sendInterestsToServer();
            Interest.saveInterests(userId, checkedInterests, smaDB);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        sendInterestsToServer();
        Interest.saveInterests(userId, checkedInterests, smaDB);
        super.onBackPressed();
    }
    //endregion

}
