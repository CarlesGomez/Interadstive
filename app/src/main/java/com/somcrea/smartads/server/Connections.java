package com.somcrea.smartads.server;

import com.somcrea.smartads.models.FacebookUserProfile;
import com.somcrea.smartads.models.Offers;
import com.somcrea.smartads.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubengrafgarcia.
 */
public class Connections {

    //region Atributs
    private HttpClient client;
    private HttpPost post;
    private String urlString;
    private URL url;
    private HttpURLConnection con;
    private InputStream is;
    private StringBuilder response;

    //endregion Atributs

    //Constructor.
    public Connections(String url)
    {
        try {
            this.urlString = url;
            this.url = new URL(this.urlString);
            response = new StringBuilder();
        }
        catch (Exception e) {e.printStackTrace();}
    }

    //region METODES
    /**
     * Registra o fa un update del usuari a la BD del servidor.
     *
     * @param fbUserProfile Paràmetres a registrar/update.
     */

    public void registerOrUpdateUser(FacebookUserProfile fbUserProfile) {
        client = new DefaultHttpClient();
        post = new HttpPost(urlString);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("insertOrUpdateUser", "insertOrUpdateUser"));
            nameValuePairs.add(new BasicNameValuePair("id", fbUserProfile.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("firstname", fbUserProfile.getFirstName()));
            nameValuePairs.add(new BasicNameValuePair("secondname", fbUserProfile.getSecondName()));
            nameValuePairs.add(new BasicNameValuePair("age", fbUserProfile.getAge()));
            nameValuePairs.add(new BasicNameValuePair("person_link", fbUserProfile.getPersonLink()));
            nameValuePairs.add(new BasicNameValuePair("photo_url", fbUserProfile.getPhotoUrl()));
            nameValuePairs.add(new BasicNameValuePair("interest", fbUserProfile.getInterest()));
            nameValuePairs.add(new BasicNameValuePair("gender", fbUserProfile.getGender()));
            nameValuePairs.add(new BasicNameValuePair("email", fbUserProfile.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("last_connection", fbUserProfile.getLastConnection()));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            client.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * S'encarrega de retornar algunes taules del servidor com a String.
     *
     * @return Retorna el JSON que correspon a aquestes taules.
     */

    public String returnServerTableAsJson(String key) {
        BufferedReader reader;
        String linia = "";
        String resposta = "";
        con = null;
        is = null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tableForJson", key));

        try {

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            con.connect();

            is = con.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            while ((linia = reader.readLine()) != null)
                response.append(linia);
            is.close();
            con.disconnect();
            resposta = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            resposta = "";
        }

        return resposta;
    }

    //Get query for make a post in HttpURLConnection.
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    //Registra els likes del usuari.
    public void registerUserLikes(String userId, ArrayList<String> userLikes) {
        client = new DefaultHttpClient();
        post = new HttpPost(urlString);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("registerUserLikes", "registerUserLikes"));
            nameValuePairs.add(new BasicNameValuePair("userId", userId));
            nameValuePairs.add(new BasicNameValuePair("userLikes", Utils.returnStringForRegisterUserLikes(userLikes)));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            client.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Envia la oferta al servidor:
    public void sendOfferToServer(Offers offer, String userId, String state)
    {
        client = new DefaultHttpClient();
        post = new HttpPost(urlString);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("registerOffer", "registerOffer"));
            nameValuePairs.add(new BasicNameValuePair("userId", userId));

            nameValuePairs.add(new BasicNameValuePair("id", offer.getId()));
            nameValuePairs.add(new BasicNameValuePair("brand_id", offer.getBrand_id()));
            nameValuePairs.add(new BasicNameValuePair("clothes_id", offer.getClothes_id()));
            nameValuePairs.add(new BasicNameValuePair("style_id", offer.getStyle_id()));
            nameValuePairs.add(new BasicNameValuePair("discount", offer.getDiscount().toString()));
            nameValuePairs.add(new BasicNameValuePair("gender", offer.getGender()));
            nameValuePairs.add(new BasicNameValuePair("minage", offer.getMinage().toString()));
            nameValuePairs.add(new BasicNameValuePair("maxage", offer.getMaxage().toString()));
            nameValuePairs.add(new BasicNameValuePair("url", offer.getUrl()));
            nameValuePairs.add(new BasicNameValuePair("bluetooth_id", offer.getBluetooth_id()));
            nameValuePairs.add(new BasicNameValuePair("creation_time", offer.getCreation_time()));
            nameValuePairs.add(new BasicNameValuePair("state", state));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            client.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

}
