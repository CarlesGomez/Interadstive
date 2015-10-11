package com.somcrea.smartads.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by rubengrafgarcia.
 */

public class Utils
{
    //region METODES
    //Retorna un valor en string de un objecte JSON determinat.
    public static String getValueFromJsonKey(JSONObject jsonObject, String key)
    {
        String toReturn = "";
        try
        {
            toReturn = jsonObject.has(key) ? jsonObject.getString(key) : "";
        }
        catch (Exception e) { e.printStackTrace(); }

        if(key.equals("birthday"))
            toReturn = String.valueOf(Utils.returnAgeBetweenTwoDates(Utils.getCurrentTime(), toReturn));
        else if(key.equals("photoUrl"))
            toReturn = "https://graph.facebook.com/" + Utils.getValueFromJsonKey(jsonObject, "id") + "/picture?height=500&width=500";
        else if(key.equals("interested_in"))
            toReturn = Utils.getValueFromJsonKey(jsonObject, "gender").equals("male") ? "female" : "male";

        return toReturn;
    }

    /**
     * S'encarrega de retornar l'edat entre dos dates.
     * @param dateToday data d'avui.
     * @param dateFacebook data de fb.
     * @return diferència d'edat.
     */

    public static long returnAgeBetweenTwoDates(String dateToday, String dateFacebook) {
        long years = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat inputFacebook = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat outFacebook = new SimpleDateFormat("yyyy/MM/dd");

        try {
            dateToday=dateToday.substring(0,dateToday.indexOf(" ")).replace("-", "/");
            Date dateFB = inputFacebook.parse(dateFacebook);
            dateFacebook = outFacebook.format(dateFB);
            Date dateIni = simpleDateFormat.parse(dateToday);
            Date dateFin = simpleDateFormat.parse(dateFacebook);
            long different = dateIni.getTime() - dateFin.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long elapsedDays = different / daysInMilli;
            years=elapsedDays / 365;
        }

        catch (Exception pe){
            pe.printStackTrace();
        }
        return years;
    }

    //Agafa la data actual.
    public static String getCurrentTime()
    {
        String formattedDate = "";
        try {
            Date serverTime = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formattedDate = df.format(serverTime);
        }
        catch (Exception e) {e.printStackTrace();}
        return formattedDate;
    }

    //Mira si una key existeix en el shared preferences.
    public static Boolean getIfExistsSharedPreferences(Context c, String key)
    {
        SharedPreferences settings = c.getSharedPreferences("PreferencesInstallationFile", 0);
        return settings.getBoolean(key, true);
    }

    //Save in shared preferences.
    public static void setKeyInSharedPreferences(Context c, String key)
    {
        SharedPreferences settings = c.getSharedPreferences("PreferencesInstallationFile", 0);
        settings.edit().putBoolean(key, false).commit();
    }

    //Monta el string per enviar-lo al server dels likes del usuari.
    public static String returnStringForRegisterUserLikes(ArrayList<String> userLikes)
    {
        String toReturn = "";
        try {
            for (int i = 0; i < userLikes.size(); i++)
                toReturn += userLikes.get(i) + ";";

            toReturn = toReturn.substring(0, toReturn.length() - 1);
        }
        catch (Exception e){e.printStackTrace();}
        return toReturn;
    }

    //Get random number by min and max.
    public static Integer getIntRdmNumber(int min, int max)
    {
        Integer rdmToReturn = 0;
        try {
            Random r = new Random();
            rdmToReturn = r.nextInt(max);
        }catch (Exception e){e.printStackTrace();}
        return rdmToReturn;
    }

    public static String getHourByDate(String date)
    {
        String init = "", finalS = "";
        try {
            String operationDate = date.substring(date.indexOf(" "), date.length());
            init = operationDate.substring(0, operationDate.indexOf(":"));
            finalS = operationDate.substring(operationDate.indexOf(":"), operationDate.indexOf(":") + 3);
        }
        catch(Exception e){e.printStackTrace();}

        return init + finalS;

    }
    //endregion

}
