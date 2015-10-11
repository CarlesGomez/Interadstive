package com.somcrea.smartads;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.somcrea.smartads.activities.InterestActivity;
import com.somcrea.smartads.adapters.SmaPagerAdapter;
import com.somcrea.smartads.layoutsfortabs.SlidingTabLayout;
import com.somcrea.smartads.models.FacebookUserProfile;
import com.somcrea.smartads.server.Connections;
import com.somcrea.smartads.sqlite.DatabaseDmlHelper;
import com.somcrea.smartads.sqlite.SmartAdsOpenHelper;
import com.somcrea.smartads.utils.AppConstants;
import com.somcrea.smartads.utils.Utils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    //region ATRIBUTS
    private CallbackManager callbackManager;
    private static String FACEBOOK_TAG = "Facebook";
    private SmartAdsOpenHelper smaOpenHelper;
    private SQLiteDatabase smaDB;
    private static FacebookUserProfile fbUser;
    private DatabaseDmlHelper ddmh;
    private Connections con;
    private boolean myFirstTimeInstallation;
    public static ImageLoader imgLoader=ImageLoader.getInstance();

    //Attributes for adapter.
    Toolbar toolbar;
    ViewPager pager;
    SmaPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence titles[] = {"Today", "Yesterday", "Others"};
    int numboftabs = 3;
    //endregion ATRIBUTS

    //region EVENTS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_launcher);
        enableBluetooth();
        firstInstallationGestion();
        configureDataBase();
        fbLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openInterestActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

    //region METODES
    //Login amb facebook.
    private void fbLogin()
    {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                Log.i(FACEBOOK_TAG, "onCompleted");
                                {
                                    fbUser = new FacebookUserProfile(object);
                                    ddmh = new DatabaseDmlHelper(smaDB);
                                    ddmh.insertOrUpdateFacebookUser(fbUser);
                                    threadForInsertOrUpdateUser();
                                    threadForGetOffersTable();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, interested_in, link");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel()
            {
                Log.i(FACEBOOK_TAG, "Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e)
            {
                Log.i(FACEBOOK_TAG, "Login attempt failed.");
            }
        });

        List<String> permissionArray = Arrays.asList("email", "user_birthday", "user_relationship_details", "user_about_me", "user_photos");
        LoginManager.getInstance().logInWithReadPermissions(this, permissionArray);
    }

    //Configuració de la base de dades.
    private void configureDataBase()
    {
        smaOpenHelper = SmartAdsOpenHelper.getInstance(getApplicationContext());
        smaDB = smaOpenHelper.getWritableDatabase();
    }

    //Registra un usuari al servidor.
    private void threadForInsertOrUpdateUser()
    {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                con = new Connections(AppConstants.URL_MOBILE_CONTROLLER);
                con.registerOrUpdateUser(fbUser);
                return null;
            }
        }.execute();
    }

    //Registra un usuari al servidor.
    private void threadForGetOffersTable()
    {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... voids) {
                con = new Connections(AppConstants.URL_MOBILE_CONTROLLER);
                String offers = con.returnServerTableAsJson("offers");
                return offers;
            }

            @Override
            protected void onPostExecute(String offers) {
                super.onPostExecute(offers);
                try {
                    JSONObject jsonOffers = new JSONObject(offers);
                    ddmh.insertOrUpdateOffers(jsonOffers);
                    setSmaPagerAdapter();
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }.execute();
    }

    //Set Sma pager adapter.
    private void setSmaPagerAdapter()
    {
        if(!myFirstTimeInstallation)
            setMainContentAndAssignAdapter();
        else {
            setContentView(R.layout.welcome);
            ImageView ivProfile = (ImageView) findViewById(R.id.imgProfile);
            imgLoader.displayImage(fbUser.getPhotoUrl(), ivProfile);
            TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
            tvUserName.setText("Hello, " + fbUser.getFirstName() + "!");
            LinearLayout lyWelcome = (LinearLayout) findViewById(R.id.lyWelcome);
            lyWelcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMainContentAndAssignAdapter();
                }
            });
        }
    }

    //Gestió de la primera instalació.
    private void firstInstallationGestion()
    {
        myFirstTimeInstallation = Utils.getIfExistsSharedPreferences(getApplicationContext(), "myFirstInstallation");
        if(myFirstTimeInstallation)
            Utils.setKeyInSharedPreferences(getApplicationContext(), "myFirstInstallation");
    }

    //Get Key Hash:
    private void getKeyHash()
    {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.somcrea.smartads", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = new String(Base64.encode(md.digest(), 0));
                Log.e("KeyHash", key);
            }
        } catch (PackageManager.NameNotFoundException packageEx) {
            packageEx.printStackTrace();
        } catch (NoSuchAlgorithmException nsaException) {
            nsaException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Enable bluetooth. (Comentar si se quiere testear en un emulador).
    private void enableBluetooth() {

        //ESTO LO ACTIVA AUTOMÁTICO!!!!!

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        //DEJARÉ ESTE PARA QUE NO SEA TAN INTRUSIVO, PERO EL DE ANTES ES LA HOSTIA!!!!!
        /*BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            // The REQUEST_ENABLE_BT constant passed to startActivityForResult() is a locally defined integer (which must be greater than 0), that the system passes back to you in your onActivityResult()
            // implementation as the requestCode parameter.

            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(intentBtEnabled, REQUEST_ENABLE_BT);
        }*/
    }

    private void setMainContentAndAssignAdapter()
    {
        try {
            setContentView(R.layout.activity_main);

            // Creating The Toolbar and setting it as the Toolbar for the activity
            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);

            // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            adapter = new SmaPagerAdapter(getSupportFragmentManager(), titles, numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);
        }catch (Exception e){e.printStackTrace();}
    }

    //Open interest activity.
    private void openInterestActivity()
    {
        Intent i = new Intent(MainActivity.this, InterestActivity.class);
        i.putExtra("userId", fbUser.getUserId());
        i.putExtra("myFirstTimeInstallation", myFirstTimeInstallation);
        startActivity(i);
    }
    //endregion
}
