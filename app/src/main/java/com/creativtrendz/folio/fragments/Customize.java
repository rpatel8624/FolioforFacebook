package com.creativtrendz.folio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.MainActivity;

public class Customize extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.customize_preferences);
        Preference images = findPreference("no_images");
        Preference people = findPreference("hide_people");
        Preference fab = findPreference("show_fab");
        Preference tap = findPreference("tap");
        images.setOnPreferenceClickListener(this);
        people.setOnPreferenceClickListener(this);
        fab.setOnPreferenceClickListener(this);
        tap.setOnPreferenceClickListener(this);


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

            case "tap":
                refresh();
                break;


        }

        return false;


    }


    private void refresh() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("apply_changes_to_app", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }
}


