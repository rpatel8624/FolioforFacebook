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


public class FolioWebView extends WebViewClient {
    private boolean refreshed;
    private static Context context = FolioApplication.getContextOfApplication();


    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if ((url.contains("market://") || url.contains("mailto:")
                || url.contains("play.google") || url.contains("tel:") || url.contains("youtube") || url
                .contains("vid:"))) {
            view.getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;

        } else if (Uri.parse(url).getHost().endsWith("facebook.com")
                || Uri.parse(url).getHost().endsWith("m.facebook.com")
                || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                || Uri.parse(url).getHost().endsWith("m.facebook.com/messages")
                || Uri.parse(url).getHost().endsWith("h.facebook.com")
                || Uri.parse(url).getHost().endsWith("l.facebook.com")
                || Uri.parse(url).getHost().endsWith("0.facebook.com")
                || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                || Uri.parse(url).getHost().endsWith("fbcdn.net")
                || Uri.parse(url).getHost().endsWith("akamaihd.net")
                || Uri.parse(url).getHost().endsWith("fb.me")
                || Uri.parse(url).getHost().endsWith("googleusercontent.com")
                || Uri.parse(url).getHost().endsWith("messenger.com")) {
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
        if (Connectivity.isConnected(context) && !failingUrl.contains("edge-chat") && !failingUrl.contains("akamaihd")
                && !failingUrl.contains("atdmt") && !refreshed) {
            view.loadUrl(failingUrl);
            refreshed = true;
        }
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
        onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
    }


    @Override
    public void onPageFinished(WebView view, String url) {


        if (preferences.getBoolean("no_images", false))
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('.img, ._5s61, ._5sgg{ display: none; }');");


    }

}
