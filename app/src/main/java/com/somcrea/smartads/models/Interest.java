package com.somcrea.smartads.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.somcrea.smartads.utils.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ruben.
 */
public class Interest {

    //region ATRIBUTS
    private String id;
    private String name;
    private String imageUrl;
    private String type;
    //endregion

    //Constructor
    public Interest(String id, String name, String imageUrl, String type) {
        this.setId(id);
        this.setName(name);
        this.setImageUrl(imageUrl);
        this.setType(type);
    }

    //region GETTERS/SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    //endregion

    //region METODES
    //S'encarrega de agafar tots els interessos de la bd.
    public static ArrayList<Interest> getInterests(String type, SQLiteDatabase smaDb)
    {
        ArrayList<Interest> arrayToReturn = new ArrayList<Interest>();
        try {
            String id = "", name = "", url = "";
            String sql = "SELECT * FROM " + type + ";";
            Cursor c = smaDb.rawQuery(sql, null);
            while (c.moveToNext()) {
                id = c.getString(0);
                name = c.getString(1);
                url = c.getString(2);
                arrayToReturn.add(new Interest(id, name, url, type));
            }
        }catch (Exception e){e.printStackTrace();}
        return arrayToReturn;
    }

    //Agafa els interessos del ususari.
    public static ArrayList<String> getInterestsByUserId(String userId, SQLiteDatabase smaDb)
    {
        ArrayList<String> arrayToReturn = new ArrayList<String>();

        try {
            String id = "", name = "", url = "";

            String sql = "SELECT * FROM user_likes WHERE user_id ='" + userId + "';";

            Cursor c = smaDb.rawQuery(sql, null);
            while (c.moveToNext()) {
                id = c.getString(1);
                arrayToReturn.add(id);
            }
        }catch (Exception e){e.printStackTrace();}

        return arrayToReturn;
    }

    //Guarda els interessos de un usuari.
    public static void saveInterests(String userId, ArrayList<String> interests, SQLiteDatabase smaDb)
    {
        try {
            String delete = "DELETE FROM user_likes WHERE user_id = '" + userId + "';";
            smaDb.execSQL(delete);

            String interest;
            String insert = "";
            for (int i = 0; i < interests.size(); i++) {
                interest = interests.get(i);
                insert = "INSERT INTO user_likes VALUES ('" +
                        userId + "', '" + interest + "');";
                smaDb.execSQL(insert);
            }
        }catch (Exception e){e.printStackTrace();}
    }
    //endregion
}
