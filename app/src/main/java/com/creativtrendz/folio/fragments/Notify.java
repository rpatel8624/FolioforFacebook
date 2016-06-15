// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite

package com.creativtrendz.folio.fragments;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.util.Log;
import android.widget.RemoteViews;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.FolioApplication;
import com.creativtrendz.folio.activities.QuickFacebook;
import com.creativtrendz.folio.activities.QuickGoogle;
import com.creativtrendz.folio.activities.QuickInstagram;
import com.creativtrendz.folio.notifications.FolioNotifications;
import com.creativtrendz.folio.preferences.SwitchPreferenceCompat;

import net.grandcentrix.tray.TrayAppPreferences;

@SuppressLint({ "NewApi", "ServiceCast" })
public class Notify extends PreferenceFragment {
private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
private static Context context;
private SharedPreferences preferences;
public static final String FACEBOOK = "https://m.facebook.com/";
private TrayAppPreferences trayPreferences;

    
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.notifications_preferences);


        context = FolioApplication.getContextOfApplication();


        trayPreferences = new TrayAppPreferences(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        ListPreference lp = (ListPreference) findPreference("interval_pref");
        String temp1 = getString(R.string.interval_pref_description).replace("%s", "");
        String temp2 = lp.getSummary().toString();
        if (temp1.equals(temp2))
            lp.setValueIndex(2);


        myPrefListner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                final Intent intent = new Intent(context, FolioNotifications.class);

                switch (key) {
                    case "interval_pref":
                        // update Tray Preference before restarting the service
                        trayPreferences.put("interval_pref", Integer.parseInt(preferences.getString("interval_pref", "1800000")));
                        // restart the service after time interval change
                        if (prefs.getBoolean("notifications_activated", false)) {
                            context.stopService(intent);
                            context.startService(intent);
                        }
                        break;
                    case "ringtone":
                        trayPreferences.put("ringtone", preferences.getString("ringtone", "content://settings/system/notification_sound"));
                        break;
                    case "ringtone_msg":
                        trayPreferences.put("ringtone_msg", preferences.getString("ringtone_msg", "content://settings/system/notification_sound"));
                        break;
                    case "vibrate":
                        trayPreferences.put("vibrate", preferences.getBoolean("vibrate", false));
                        break;
                    case "led_light":
                        trayPreferences.put("led_light", preferences.getBoolean("led_light", false));
                        break;
                    case "notifications_everywhere":
                        trayPreferences.put("notifications_everywhere", preferences.getBoolean("notifications_everywhere", true));
                        break;

                    case "notifications_activated":
                        trayPreferences.put("notifications_activated", preferences.getBoolean("notifications_activated", false));
                        if (prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("messages_activated", false)) {
                            context.stopService(intent);
                            context.startService(intent);
                        } else //noinspection StatementWithEmptyBody
                            if (!prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("messages_activated", false)) {
                                // ignore this case
                            } else if (prefs.getBoolean("notifications_activated", false) && !preferences.getBoolean("messages_activated", false)) {
                                context.startService(intent);
                            } else
                                context.stopService(intent);
                        break;
                    case "messages_activated":
                        trayPreferences.put("messages_activated", preferences.getBoolean("messages_activated", false));
                        if (prefs.getBoolean("messages_activated", false) && preferences.getBoolean("notifications_activated", false)) {
                            context.stopService(intent);
                            context.startService(intent);
                        } else //noinspection StatementWithEmptyBody
                            if (!prefs.getBoolean("messages_activated", false) && preferences.getBoolean("notifications_activated", false)) {
                                // ignore this case
                            } else if (prefs.getBoolean("messages_activated", false) && !preferences.getBoolean("notifications_activated", false)) {
                                context.startService(intent);
                            } else
                                context.stopService(intent);
                        break;
                }

                // what's going on, dude?
                Log.v("SharedPreferenceChange", key + " changed in NotificationsSettingsFragment");
            }
        };


        SwitchPreferenceCompat quickbar = (SwitchPreferenceCompat) findPreference("quickbar_pref");
        quickbar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                NotificationManager notificationmanager = (NotificationManager) FolioApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                if ((Boolean) newValue) {


                    RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.quickbar);

                    remoteView.setTextViewText(R.id.quick, getString(R.string.app_name));
                    remoteView.setTextViewText(R.id.quick_bar, getString(R.string.quick_bar));
                    Builder builder = new Notification.Builder(context);
                    builder.setSmallIcon(R.drawable.ic_stat_f)
                            .setTicker(getString(R.string.quick_bar_on))
                            .setOngoing(true)
                            .setContent(remoteView)
                            .setPriority(Notification.PRIORITY_MIN);


                    Intent quickNewsfeed = new Intent(getActivity().getApplicationContext(), QuickInstagram.class);
                    quickNewsfeed.setData(Uri.parse(FACEBOOK + "notifications"));
                    quickNewsfeed.setAction(Intent.ACTION_VIEW);
                    PendingIntent newsIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, quickNewsfeed,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteView.setOnClickPendingIntent(R.id.quick_notifications, newsIntent);


                    Intent quickMessages = new Intent(getActivity().getApplicationContext(), QuickGoogle.class);
                    quickMessages.setData(Uri.parse(FACEBOOK + "messages/"));
                    quickMessages.setAction(Intent.ACTION_VIEW);
                    PendingIntent messagesIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, quickMessages,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteView.setOnClickPendingIntent(R.id.quick_messages, messagesIntent);


                    Intent quickFriends = new Intent(getActivity().getApplicationContext(), QuickFacebook.class);
                    quickFriends.setData(Uri.parse(FACEBOOK + "friends/center/friends/"));
                    quickFriends.setAction(Intent.ACTION_VIEW);
                    PendingIntent friendsIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, quickFriends,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteView.setOnClickPendingIntent(R.id.quick_friends, friendsIntent);


                    notificationmanager.notify(22, builder.build());


                } else {
                    notificationmanager.cancel(22);

                }
                return true;
            }
        });


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

     
        String ringtoneString = preferences.getString("ringtone", "content://settings/system/notification_sound");
        Uri ringtoneUri = Uri.parse(ringtoneString);
        String name;

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            name = ringtone.getTitle(context);
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpn = (RingtonePreference) findPreference("ringtone");
        rpn.setSummary(getString(R.string.notification_sound_description) + name);

       
        ringtoneString = preferences.getString("ringtone_msg", "content://settings/system/notification_sound");
        ringtoneUri = Uri.parse(ringtoneString);

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            name = ringtone.getTitle(context);
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpm = (RingtonePreference) findPreference("ringtone_msg");
        rpm.setSummary(getString(R.string.message_sound_description) + name);
    }


}

