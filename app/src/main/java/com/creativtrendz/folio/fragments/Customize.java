package com.creativtrendz.folio.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.creativetrends.folio.app.R;

public class Customize extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.customize_preferences);
        Preference images = findPreference("no_images");
        Preference people = findPreference("hide_people");
        Preference fab = findPreference("show_fab");
        images.setOnPreferenceClickListener(this);
        people.setOnPreferenceClickListener(this);
        fab.setOnPreferenceClickListener(this);


    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {


            case "theme_preference_fb":


            case "no_images":

                break;

            case "allow_inside":

                break;

            case "hide_people":

                break;

            case "theme_preference":

                break;

            case "show_fab":

                break;



        }

        return false;


    }


}


