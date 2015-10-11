package com.somcrea.smartads;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.BeaconManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.somcrea.smartads.ble.BeaconsMonitoringService;

public class SmartAdsApplication extends Application {

    //Atributs:
    private BeaconManager beaconManager = null;

    //region EVENTS
    @Override
    public void onCreate() {
        super.onCreate();
        setBeaconManager(new BeaconManager(this));
        startService(new Intent(getApplicationContext(), BeaconsMonitoringService.class));
        initImageLoader(getApplicationContext());
    }
    //endregion

    //region MÉTODES

    //Get for beacon manager:
    public BeaconManager getBeaconManager() {
        try {
            if (beaconManager == null) {
                beaconManager = new BeaconManager(this);
            }
        }
        catch (Exception e) {e.printStackTrace();}
        return beaconManager;
    }

    //Set for beacon manager:
    public void setBeaconManager(BeaconManager beaconManager) {
        this.beaconManager = beaconManager;
    }

    //Init image loader.
    public static void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions=new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.loaderimg)
                .showImageOnLoading(R.drawable.loaderimg)
                .showImageOnFail(R.drawable.loaderimg)
                .cacheInMemory(true)
                .cacheOnDisk(true).build();

        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .writeDebugLogs()
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
    //endregion


}