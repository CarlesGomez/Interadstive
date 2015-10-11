package com.somcrea.smartads.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.somcrea.smartads.fragments.TrackPathFragment;

/**
 * Created by Ruben.
 */
public class SmaPagerAdapter extends FragmentStatePagerAdapter {

    //region ATRIBUTS
    CharSequence titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int numbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    //endregion

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public SmaPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.titles = mTitles;
        this.numbOfTabs = mNumbOfTabsumb;

    }

    //region EVENTS
    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        TrackPathFragment tpf = new TrackPathFragment();
        return tpf;

    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return numbOfTabs;
    }
    //endregion
}