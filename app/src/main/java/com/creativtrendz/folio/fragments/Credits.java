// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite


package com.creativtrendz.folio.fragments;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.FolioApplication;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Credits extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		addPreferencesFromResource(R.xml.about_preferences);

		Context context = FolioApplication.getContextOfApplication();
		PreferenceManager.getDefaultSharedPreferences(context);

		{

		}


//listener for bejan preference
		findPreference("bejan").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Log.v("CreditsFragment", "bejan clicked");
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.credits_bejan_link))));
				return true;
			}
		});


//listener for nam preference
		findPreference("nam").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Log.v("CreditsFragment", "nam clicked");
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.credits_nam_link))));
				return true;
			}
		});


		findPreference("fadev").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.credits_fadev_link))));
				return true;
			}
		});


		//open about dialog
		Preference preferenceabout = findPreference("about");
		preferenceabout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder =
						new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
				builder.setTitle(getResources().getString(R.string.about_header));
				builder.setMessage(Html.fromHtml(getResources().getString(R.string.about_text)));
				builder.setPositiveButton(getResources().getString(R.string.ok), null);
				builder.setNegativeButton(null, null);
				builder.show();

				return true;
			}
		});

		Preference preferencetrnaslators = findPreference("translators");
		preferencetrnaslators.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder =
						new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
				builder.setTitle(getResources().getString(R.string.preference_category12));
				WebView wv = new WebView(getActivity());
				wv.loadUrl("http:\\jnsdesigns.webs.com/trans.htm");
				wv.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						view.loadUrl(url);
						return true;
					}
				});

				builder.setView(wv);
				builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				builder.show();

				return true;
			}
		});

	}
}
