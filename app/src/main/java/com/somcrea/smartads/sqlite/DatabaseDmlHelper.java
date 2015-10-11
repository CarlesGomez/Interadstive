package com.somcrea.smartads.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.somcrea.smartads.models.FacebookUserProfile;
import com.somcrea.smartads.models.Offers;
import com.somcrea.smartads.models.Place;
import com.somcrea.smartads.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rubengrafgarcia.
 */
public class DatabaseDmlHelper {

    //Atributs:
    private SQLiteDatabase smaDb;

    //Constructor:
    public DatabaseDmlHelper(SQLiteDatabase smaDb)
    {
        this.smaDb = smaDb;
    }

    //region METODES
    //Inserta o updata un usuari de facebook.
    public void insertOrUpdateFacebookUser(FacebookUserProfile userToSave)
    {
        try
        {
            String sql = FacebookUserProfile.getSqlForFbUser(userToSave, "INSERT");
            smaDb.execSQL(sql);
            Log.i("DatabaseDmlHelper", "insert user");
        }
        catch (SQLiteConstraintException sqlEx)
        {
            String sql = FacebookUserProfile.getSqlForFbUser(userToSave, "UPDATE");
            smaDb.execSQL(sql);
            Log.i("DatabaseDmlHelper", "update user");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Inserta o updata les ofertes.
    public void insertOrUpdateOffers(JSONObject offers)
    {
        try {
            JSONArray offersArray = offers.getJSONArray("offers");
            String id, brand_id, clothes_id, style_id, gender, url, bluetooth_id, creation_time;
            Integer discount, minage, maxage;
            String insert = "", update = "";
            for(int i = 0; i < offersArray.length(); i++)
            {
                JSONObject offer = offersArray.getJSONObject(i);
                id = offer.getString("id");
                brand_id = offer.getString("brand_id");
                clothes_id = offer.getString("clothes_id");
                style_id = offer.getString("style_id");
                discount = offer.getInt("discount");
                gender = offer.getString("gender");
                minage = offer.getInt("minage");
                maxage = offer.getInt("maxage");
                url = offer.getString("url");
                bluetooth_id = offer.getString("bluetooth_id");
                creation_time = offer.getString("creation_time");

                try
                {
                    insert = "INSERT INTO offers VALUES('" + id + "', '"
                            + brand_id + "', '" + clothes_id + "', '"
                            + style_id + "', " + discount + ", '"
                            + gender + "', " + minage + ", "
                            + maxage + ", '" + url + "', '"
                            + bluetooth_id + "', '"
                            + creation_time + "');";
                    smaDb.execSQL(insert);
                    Log.i("DatabaseDmlHelper", "insert offer");
                }
                catch (SQLiteConstraintException sqlEx)
                {
                    update = "UPDATE offers SET "
                            + "id = '" + id + "', "
                            + "brand_id = '" + brand_id + "', "
                            + "clothes_id = '" + clothes_id + "', "
                            + "style_id ='" + style_id + "', "
                            + "discount = " + discount + ", "
                            + "gender = '" + gender + "', "
                            + "minage = " + minage + ", "
                            + "maxage = " + maxage + ", "
                            + "url = '" + url + "', "
                            + "bluetooth_id = '" + bluetooth_id + "', "
                            + "creation_time = '" + creation_time + "'"
                            + " WHERE id = '" + id + "';";
                    smaDb.execSQL(update);
                    Log.i("DatabaseDmlHelper", "update offer");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        catch (Exception e){e.printStackTrace();}
    }

    //Retorna la oferta en el cas de que hagi una fent un random de les trobades si aquesta no ha estat rebuda ja.
    //(Falta passar els metres dels beacons per saber si està entrant o sortint)
    public Offers getOfferByUserIdAndBeacon(String userId, String bluetoothId)
    {
        Offers offerToReturn = null;
        ArrayList<Offers> offersArray = new ArrayList<Offers>();

        String clothesSql = "SELECT ul.related_like FROM user_likes ul, clothes c WHERE ul.related_like = c.id AND ul.user_id = '" + userId + "'";
        String brandsSql = "SELECT ul.related_like FROM user_likes ul, brands b WHERE ul.related_like = b.id AND ul.user_id = '" + userId + "'";
        String stylesSql = "SELECT ul.related_like FROM user_likes ul, styles s WHERE ul.related_like = s.id AND ul.user_id = '" + userId + "'";

        //Filtered offer:
        /*String offersSql = "SELECT * FROM offers WHERE clothes_id IN (" + clothesSql + ")" +
                " OR brand_id IN (" + brandsSql + ")" +
                " OR style_id IN (" + stylesSql + ")" +
                " AND bluetooth_id = '" + bluetoothId + "';";*/

        String offersSql = "SELECT * FROM offers WHERE bluetooth_id = '" + bluetoothId + "';";

        try {
            Cursor c = this.smaDb.rawQuery(offersSql, null);
            if(c.moveToFirst())
            {
                offersArray.add(new Offers(c.getString(0), c.getString(1), c.getString(2), c.getString(3),
                        c.getInt(4), c.getString(5), c.getInt(6), c.getInt(7), c.getString(8), c.getString(9),
                        c.getString(10)));
            }

            if(offersArray.size() > 0) {
                offerToReturn = offersArray.get(0);
            }

        }
        catch (Exception e) {e.printStackTrace();}

        return offerToReturn;
    }

    //Get beacon id by minor and major.
    public String getBeaconIdByMinorAndMajor(Integer minor, Integer major)
    {
        String id = "";
        try {
            String sql = "SELECT id FROM bluetooth_dev WHERE minor = " + minor + " AND major = " + major + ";";
            Cursor c = this.smaDb.rawQuery(sql, null);
            if (c.moveToFirst())
                id = c.getString(0);
        }catch (Exception e){e.printStackTrace();}

        return id;
    }

    //Register track path:
    public void registerTrackPath(String bluetoothId, final String userId, final String state)
    {
        String insert = "INSERT INTO tracked_path VALUES ('" + userId + "', '" + bluetoothId + "', '" + state + "', '" + Utils.getCurrentTime() + "');";
        String insertStyle1 = "INSERT INTO styles VALUES ('sty_5', 'beer', 'http://sompartyapp.com/smart_ads/img/styles/cerveza_dia.jpg');";
        String insertStyle2 = "INSERT INTO styles VALUES ('sty_6', 'geek', 'http://sompartyapp.com/smart_ads/img/styles/geek.jpg');";

        String[] arrayInserts = new String[3];
        arrayInserts[0] = insert;
        arrayInserts[1] = insertStyle1;
        arrayInserts[2] = insertStyle2;

        for(int i = 0; i < arrayInserts.length; i++) {
            try {
                this.smaDb.execSQL(arrayInserts[i]);
            } catch (Exception e) {
                Log.i("Test", "Test");
                e.printStackTrace();
            }
        }

    }

    public Place getMorePlaces()
    {
        Place p = new Place("", "", "", "");
        try
        {
            String sql = "SELECT p.name, p.url, tp.state, tp.hour FROM tracked_path tp, bluetooth_dev bd, zones z, places p " +
                    "WHERE tp.user_id = '" + FacebookUserProfile.getLastUserId(this.smaDb)  +"' AND tp.bluetooth_id = '2' AND tp.bluetooth_id = bd.id " +
                    "AND bd.zone_id = z.id AND z.place_id = p.id;";

            Cursor c = this.smaDb.rawQuery(sql, null);

            int i = 0;
            String name="", url="", openTrack = "", finalTrack = "";
            if(c.getCount() > 1) {
                while (c.moveToNext()) {
                    name = c.getString(0);
                    url = c.getString(1);
                    if (c.getString(2).equals("entered")) {
                        openTrack = c.getString(3);
                    } else {
                        finalTrack = c.getString(3);
                    }
                    Log.i("test", c.getString(0));
                }
                openTrack = Utils.getHourByDate(openTrack);
                finalTrack = Utils.getHourByDate(finalTrack);

                p = new Place(name, url, openTrack, finalTrack);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p;
    }
    //endregion


}
