package com.creativtrendz.folio.activities;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.utils.PreferencesUtility;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;


@SuppressWarnings("ALL")
public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isFolioTheme = PreferencesUtility.getInstance(this).getTheme().equals("folio");
        boolean isFolioDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("foliodark");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        final boolean isPinkDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pinkdark");
        boolean isPurpleTheme = PreferencesUtility.getInstance(this).getTheme().equals("purple");
        boolean isPurpleDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("purpledark");
        boolean isDeepPurpleTheme = PreferencesUtility.getInstance(this).getTheme().equals("deeppurple");
        boolean isDeepPurpleDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("deeppurpledark");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        boolean isOrangeTheme = PreferencesUtility.getInstance(this).getTheme().equals("orange");
        boolean isOrangeDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("orangedark");
        boolean isDeepOrangeTheme = PreferencesUtility.getInstance(this).getTheme().equals("deeporange");
        boolean isDeepOrangeDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("deeporangedark");
        boolean isFalconTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcon");
        boolean isFalconDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcondark");
        boolean isLimeTheme = PreferencesUtility.getInstance(this).getTheme().equals("lime");
        boolean isLimeDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("limedark");
        boolean isGreenTheme = PreferencesUtility.getInstance(this).getTheme().equals("green");
        boolean isGreenDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("greendark");
        boolean isLightGreenTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightgreen");
        boolean isLightGreenDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightgreendark");
        boolean isAmberTheme = PreferencesUtility.getInstance(this).getTheme().equals("amber");
        boolean isAmberDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("amberdark");
        boolean isYellowTheme = PreferencesUtility.getInstance(this).getTheme().equals("yellow");
        boolean isYellowDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("yellowdark");
        boolean isRedTheme = PreferencesUtility.getInstance(this).getTheme().equals("red");
        boolean isRedDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("reddark");
        boolean isBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("googleblue");
        boolean isBlueDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("googlebluedark");
        boolean isLightBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightblue");
        boolean isLightBlueDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightbluedark");
        boolean isTealTheme = PreferencesUtility.getInstance(this).getTheme().equals("teal");
        boolean isTealDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("tealdark");
        boolean isCyanTheme = PreferencesUtility.getInstance(this).getTheme().equals("cyan");
        boolean isCyanDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("cyandark");
        boolean isBrownTheme = PreferencesUtility.getInstance(this).getTheme().equals("brown");
        boolean isBrownDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("browndark");
        boolean isGreyTheme = PreferencesUtility.getInstance(this).getTheme().equals("grey");
        boolean isGreyDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("greydark");
        final boolean isBlueGreyTheme = PreferencesUtility.getInstance(this).getTheme().equals("bluegrey");
        final boolean isBlueGreyDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("bluegreydark");
        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isFolioTheme)
                setTheme(R.style.FolioBlue);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);
        } else {


            if (isDarkTheme)
                setTheme(R.style.FolioDark);


            if (isPinkTheme)
                setTheme(R.style.FolioPink);


            if (isBlueGreyTheme)
                setTheme(R.style.BlueGrey);


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }


            CardView facebook = (CardView) findViewById(R.id.onFacebook);
            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                    intent.setData(Uri.parse("http://facebook.com/creativetrendz"));
                    startActivity(intent);
                }
            });


            CardView google = (CardView) findViewById(R.id.onGoogle);
            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.credits_google_plus_link))));

                }
            });

            CardView twitter = (CardView) findViewById(R.id.onTwitter);
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/creativetrendsa")));
                }
            });

            CardView donate = (CardView) findViewById(R.id.onDonate);
            donate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://paypal.me/creativetrends")));
                }
            });
        }


        setScreenElements();
    }




    @SuppressLint("SetTextI18n")
    private void setScreenElements() {
        TextView appNameVersion = (TextView) findViewById(R.id.app_name);
        assert appNameVersion != null;
        appNameVersion.setText(getResources().getString(R.string.app_name) + " " + PreferencesUtility.getAppVersionName(getApplicationContext()));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dummy_menu, menu);
        return true;

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }



}
