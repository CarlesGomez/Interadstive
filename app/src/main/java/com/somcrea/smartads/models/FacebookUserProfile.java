package com.somcrea.smartads.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.somcrea.smartads.utils.Utils;

import org.json.JSONObject;

/**
 * Created by Carechimbo.
 */
public class FacebookUserProfile {

    //region ATRIBUTS
    private String userId;
    private String firstName;
    private String secondName;
    private String age;
    private String personLink;
    private String photoUrl;
    private String interest;
    private String gender;
    private String email;
    private String lastConnection;
    //endregion

    //region CONSTRUCTOR
    public FacebookUserProfile(JSONObject user)
    {
        String id, firstname, secondname, age, personLink, photoUrl, interest, gender, email, lastConnection;

        id = Utils.getValueFromJsonKey(user, "id");
        firstname = Utils.getValueFromJsonKey(user, "first_name");
        secondname = Utils.getValueFromJsonKey(user, "last_name");
        age = Utils.getValueFromJsonKey(user, "birthday");
        personLink = Utils.getValueFromJsonKey(user, "link");
        photoUrl = Utils.getValueFromJsonKey(user, "photoUrl");
        interest = Utils.getValueFromJsonKey(user, "interested_in");
        gender = Utils.getValueFromJsonKey(user, "gender");
        email = Utils.getValueFromJsonKey(user, "email");
        lastConnection = Utils.getCurrentTime();

        this.setUserId(id);
        this.setFirstName(firstname);
        this.setSecondName(secondname);
        this.setAge(age);
        this.setPersonLink(personLink);
        this.setPhotoUrl(photoUrl);
        this.setInterest(interest);
        this.setGender(gender);
        this.setEmail(email);
        this.setLastConnection(lastConnection);
    }
    //endregion

    //region GETTERES/SETTERS
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPersonLink() {
        return personLink;
    }

    public void setPersonLink(String personLink) {
        this.personLink = personLink;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(String lastConnection) {
        this.lastConnection = lastConnection;
    }
    //endregion

    //region METODES
    //Crea el sql per insertar un usuari.
    public static String getSqlForFbUser(FacebookUserProfile user, String action)
    {
        String toReturn = "";
        if(action.equals("INSERT"))
        {
            toReturn = "INSERT INTO users VALUES('" + user.getUserId() + "', '"
                    + user.getFirstName() + "', '" + user.getSecondName() + "', '"
                    + user.getAge() + "', '" + user.getPersonLink() + "', '"
                    + user.getPhotoUrl() + "', '" + user.getInterest() + "', '"
                    + user.getGender() + "', '" + user.getEmail() + "', '"
                    + user.getLastConnection() + "');";
        }
        else
        {
            toReturn = "UPDATE users SET "
                    + "id = '" + user.getUserId() + "', "
                    + "firstname = '" + user.getFirstName() + "', "
                    + "secondname = '" + user.getSecondName() + "', "
                    + "age ='" + user.getAge() + "', "
                    + "person_link = '" + user.getPersonLink() + "', "
                    + "photo_url = '" + user.getPhotoUrl() + "', "
                    + "interest = '" + user.getInterest() + "', "
                    + "gender = '" + user.getGender() + "', "
                    + "email = '" + user.getEmail() + "', "
                    + "last_connection = '" + user.getLastConnection() + "'"
                    + " WHERE id = '" + user.getUserId() + "';";
        }
        return toReturn;
    }

    //Coje el ultimo user id de la base de datos.
    public static String getLastUserId(SQLiteDatabase smaDb)
    {
        String toReturn = "";
        String sql = "SELECT id " +
                "FROM users " +
                "WHERE last_connection = (SELECT MAX(last_connection) FROM users);";

        try {
            Cursor c = smaDb.rawQuery(sql, null);
            if (c.moveToFirst())
                toReturn = c.getString(0);
        }catch (Exception e){e.printStackTrace();}

        return toReturn;

    }
    //endregion METODES
}
