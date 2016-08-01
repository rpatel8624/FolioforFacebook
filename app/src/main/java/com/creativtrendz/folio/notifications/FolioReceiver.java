package com.creativtrendz.folio.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.creativtrendz.folio.activities.FolioApplication;


public class FolioReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        context = FolioApplication.getContextOfApplication();

        Intent startIntent = new Intent(context, FolioNotifications.class);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);


        if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false))
            context.startService(startIntent);
    }

}
