package com.somcrea.smartads.ble;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.somcrea.smartads.SmartAdsApplication;
import com.somcrea.smartads.MainActivity;
import com.somcrea.smartads.R;
import com.somcrea.smartads.models.FacebookUserProfile;
import com.somcrea.smartads.models.Offers;
import com.somcrea.smartads.server.Connections;
import com.somcrea.smartads.sqlite.DatabaseDmlHelper;
import com.somcrea.smartads.sqlite.SmartAdsOpenHelper;
import com.somcrea.smartads.utils.AppConstants;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruben.
 */

public class BeaconsMonitoringService extends Service {

    //region ATRIBUTS
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    //For offer.
    private static final Region REGION_OFFER_ESTIMOTE = new Region("rid", ESTIMOTE_PROXIMITY_UUID, 62207, 12936);
    //For track path and make inserts.
    private static final Region REGION_TRACK_PATH_ESTIMOTE = new Region("rid", ESTIMOTE_PROXIMITY_UUID, 35439, 34600);
    private static final String TAG = "SmartAds";
    private BeaconManager beaconManager;
    private SmartAdsOpenHelper smaOpenHelper;
    private SQLiteDatabase smaDB;
    private DatabaseDmlHelper ddmh;
    private Connections con;
    private String userId = "";
    //endregion

    //region EVENTS
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onCreate() {
        // Configure BeaconManager.
        Log.d(TAG, "Beacons monitoring service created");
        smaOpenHelper = SmartAdsOpenHelper.getInstance(getApplicationContext());
        smaDB = smaOpenHelper.getWritableDatabase();
        ddmh = new DatabaseDmlHelper(smaDB);
        con = new Connections(AppConstants.URL_MOBILE_CONTROLLER);
        userId = FacebookUserProfile.getLastUserId(smaDB);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Beacons monitoring service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            SmartAdsApplication app = (SmartAdsApplication) getApplication();
            beaconManager = app.getBeaconManager();

            //Connect:
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        Log.d(TAG, "serviceReady");
                        /**
                         * Por 2000 ms búscame beacons, cuándo encuentres espera 10000 a volver a buscar.
                         */

                        beaconManager.setBackgroundScanPeriod(2000, 4000);
                        beaconManager.setForegroundScanPeriod(2000, 4000);

                        beaconManager.startMonitoring(REGION_OFFER_ESTIMOTE);
                        beaconManager.startMonitoring(REGION_TRACK_PATH_ESTIMOTE);

                    } catch (RemoteException e) {
                        Log.e(TAG, "Cannot start ranging", e);
                    }
                }
            });

            beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
                @Override public void onEnteredRegion(Region region, List<Beacon> beacons) {
                    //Log.i("Entered Region Mine: ", region.toString());

                    if(region.getMinor() == 12936 && region.getMajor() == 62207) //Oferta.
                    {
                        Toast.makeText(getApplicationContext(), "Entered Region Mine: " + region.toString(), Toast.LENGTH_SHORT).show();
                        offerGestion(region, "entered");
                    }
                    else if(region.getMinor() == 34600 && region.getMajor() == 35439) //Camí.
                    {
                        Toast.makeText(getApplicationContext(), "Entered Region Mine: " + region.toString(), Toast.LENGTH_SHORT).show();
                        trackPathGestion(ddmh.getBeaconIdByMinorAndMajor(region.getMinor(), region.getMajor()), userId, "entered");
                    }

                    //Decidir si fer la gestió del camí o de la oferta depenent del beacon rebut.

                }
                @Override public void onExitedRegion(Region region) {
                    //Log.i("Exited Region Mine: ", region.toString());
                    if(region.getMinor() == 12936 && region.getMajor() == 62207) //Oferta.
                    {
                        Toast.makeText(getApplicationContext(), "Exited Region Mine: " + region.toString(), Toast.LENGTH_SHORT).show();
                        offerGestion(region, "exited");
                    }
                    else if(region.getMinor() == 34600 && region.getMajor() == 35439) //Camí.
                    {
                        Toast.makeText(getApplicationContext(), "Exited Region Mine: " + region.toString(), Toast.LENGTH_SHORT).show();
                        trackPathGestion(ddmh.getBeaconIdByMinorAndMajor(region.getMinor(), region.getMajor()), userId, "exited");
                    }
                }
            });
        }
        catch(Exception e) {e.printStackTrace();}

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    //Offer gestion:
    public void offerGestion(Region region, String action)
    {
        Offers offer = ddmh.getOfferByUserIdAndBeacon(userId,
                ddmh.getBeaconIdByMinorAndMajor(region.getMinor(), region.getMajor()));
        sendOfferToServer(offer, userId, action);
    }

    //Envia la oferta al servidor.
    private void sendOfferToServer(final Offers offer, final String userId, final String state)
    {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                con.sendOfferToServer(offer, userId, state);
                return null;
            }
        }.execute();
    }

    //Track gestion:
    private void trackPathGestion(final String bluetoothId, final String userId, final String state)
    {
        ddmh.registerTrackPath(bluetoothId, userId, state);
    }
    //endregion
}
