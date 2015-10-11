package com.somcrea.smartads.sqlite;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.somcrea.smartads.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by rubengrafgarcia.
 */

public class SmartAdsOpenHelper extends SQLiteOpenHelper {

    //region ATRIBUTS
    Context c;
    private static SmartAdsOpenHelper myHelper;
    //endregion

    //region CONSTRUCTORS
    private SmartAdsOpenHelper(Context context) {
        super(context, "smartAdsDb", null, 1);
        c = context;
    }

    public static synchronized SmartAdsOpenHelper getInstance(Context context)
    {
        if (myHelper == null) {
            myHelper = new SmartAdsOpenHelper(context.getApplicationContext());
        }
        return myHelper;
    }
    //endregion

    //region MÈTODES

    /**
     * Llegeix un fitxer per tal de executar les seves sentències a la BD SQlite.
     * @param c contexte.
     * @param raw fitxer que es tractarà.
     * @param db base de dades en la qual s'executaràn les comandes.
     */

    private void readFile(Context c, int raw, SQLiteDatabase db){
        String line;
        try{
            InputStream rawFile = c.getResources().openRawResource(raw);
            BufferedReader brIn = new BufferedReader(new InputStreamReader(rawFile));
            while ((line=brIn.readLine())!=null){
                db.execSQL(line);
            }
            rawFile.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //endregion

    //region EVENTS
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        readFile(c, R.raw.create_db_sqlite, db);
        Log.i("SmartAdsOpenHelper", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("onUpgrade", "onUpgrade");
    }
    //endregion


}
