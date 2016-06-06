package com.creativtrendz.folio.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.AboutActivity;
import com.creativtrendz.folio.activities.FolioApplication;
import com.creativtrendz.folio.activities.FolioLockSetup;
import com.creativtrendz.folio.activities.MainActivity;
import com.creativtrendz.folio.utils.FileOperation;
import com.facebook.login.LoginManager;

import net.grandcentrix.tray.TrayAppPreferences;


public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;
    private static Context context;
    private TrayAppPreferences trayPreferences;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = Settings.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = FolioApplication.getContextOfApplication();


        trayPreferences = new TrayAppPreferences(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);


        myPrefListner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                switch (key) {
                    case "folio_locker":
                        trayPreferences.put("folio_locker", preferences.getBoolean("folio_locker", false));
                        if (prefs.getBoolean("folio_locker", false)) {
                            Intent folioLocker = new Intent(getActivity(), FolioLockSetup.class);
                            startActivity(folioLocker);
                        } else


                            break;

                    case "allow_location":
                        trayPreferences.put("allow_location", preferences.getBoolean("allow_location", false));
                        if (prefs.getBoolean("allow_location", false)) {
                            requestLocationPermission();
                        } else


                            break;

                }


                Log.v("SharedPreferenceChange", key + " changed in NotificationsSettingsFragment");
            }
        };

        addPreferencesFromResource(R.xml.preferences);
        Preference notifications = findPreference("notifications_settings");
        Preference customize = findPreference("custom_settings");
        Preference about = findPreference("about_settings");
        Preference location = findPreference ("allow_location");
        Preference credits = findPreference("credits_settings");
        Preference tabs = findPreference("allow_inside");
        Preference navigation = findPreference("customnav");
        Preference fontset = findPreference("tap");
        Preference terms = findPreference("terms");
        Preference getkey = findPreference("help_development");
        Preference clearCachePref = findPreference("clear");
        notifications.setOnPreferenceClickListener(this);
        location.setOnPreferenceClickListener(this);
        customize.setOnPreferenceClickListener(this);
        about.setOnPreferenceClickListener(this);
        credits.setOnPreferenceClickListener(this);
        tabs.setOnPreferenceClickListener(this);
        navigation.setOnPreferenceClickListener(this);
        fontset.setOnPreferenceClickListener(this);
        terms.setOnPreferenceClickListener(this);
        clearCachePref.setOnPreferenceClickListener(this);
        getkey.setOnPreferenceClickListener(this);


    }


    @SuppressWarnings("ResourceType")
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {
            case "notifications_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Notify()).commit();
                break;

            case "custom_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Customize()).commit();
                break;

            case "about_settings":
                Intent settings = new Intent(getActivity(), AboutActivity.class);
                startActivity(settings);
                break;


            case "credits_settings":
                //noinspection ResourceType
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Credits()).commit();
                break;

            case "allow_inside":

                break;

            case "help_development":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));

                break;

            case "tap":
                relaunch();
                break;

            case "customnav":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Navigation()).commit();
                break;


            case "clear":

                AlertDialog.Builder clear =
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                clear.setTitle(getResources().getString(R.string.clear_cache_title));
                clear.setMessage(Html.fromHtml(getResources().getString(R.string.clear_cache_message)));
                clear.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        FileOperation.deleteCache(getActivity().getApplicationContext());
                        LoginManager.getInstance().logOut();
                        getActivity().finish();
                    }
                });
                clear.setNeutralButton(R.string.cancel, null);
                clear.show();
                break;

            case "terms":

                AlertDialog.Builder terms =
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                terms.setTitle(getResources().getString(R.string.terms_settings));
                terms.setMessage(getResources().getString(R.string.eula_string));
                terms.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                terms.show();
                break;


        }

        return false;


    }



    @Override
    public void onStart() {

        super.onStart();
        preferences.registerOnSharedPreferenceChangeListener(myPrefListner);
    }

    @Override
    public void onStop() {
        super.onStop();
        preferences.unregisterOnSharedPreferenceChangeListener(myPrefListner);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }


    private void relaunch() {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("apply_changes_to_app", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    private void requestLocationPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[] { locationPermission };
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION);
        } else
            Log.i(TAG, "We already have location permission.");
    }


}



	
