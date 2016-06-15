// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite

package com.creativtrendz.folio.activities;
import android.app.Application;
import android.content.Context;
import com.creativetrends.folio.app.R;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


@ReportsCrashes(formUri = "",  // will not be used
        mailTo = "bugs@creativetrendsapps.com",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = R.mipmap.ic_launcher,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt
)

public class FolioApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();

        ACRA.init(this);

    }

    public static Context getContextOfApplication() {
        return mContext;
    }



}