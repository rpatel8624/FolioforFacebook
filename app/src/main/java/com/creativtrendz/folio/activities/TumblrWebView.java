package com.creativtrendz.folio.activities;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creativtrendz.folio.services.Connectivity;


public class TumblrWebView extends WebViewClient {


	private boolean refreshed;

	private static Context context = FolioApplication.getContextOfApplication();
	final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);


	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if ((url.contains("market://")
				|| url.contains("mailto:")
				|| url.contains("play.google")
				|| url.contains("tel:")
				|| url.contains("youtube")
				|| url.contains("vid:"))) {
			view.getContext().startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;

		}else if (Uri.parse(url).getHost().endsWith("tumblr.com")) {
			return false;

		}if (preferences.getBoolean("allow_inside", false)) {
			Intent intent = new Intent((MainActivity.getMainActivity()), FolioBrowser.class);
			intent.setData(Uri.parse(url));
			view.getContext().startActivity(intent);
			return true;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			view.getContext().startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
			e.printStackTrace();
		}
		return true;
	}


	@SuppressWarnings("deprecation")
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

		if (Connectivity.isConnected(context) && !refreshed) {
			view.loadUrl(failingUrl);

			refreshed = true;
		}
	}

	@TargetApi(android.os.Build.VERSION_CODES.M)
	@Override
	public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {

		onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
	}

}