package com.creativtrendz.folio.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.utils.PreferencesUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jorell on 5/23/2016.
 */
public class OldMessages extends AppCompatActivity {
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    public Context context;
    private WebView webView;
    public Toolbar toolbar;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        boolean isFolioTheme = PreferencesUtility.getInstance(this).getTheme().equals("folio");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        final boolean isBlueGreyTheme = PreferencesUtility.getInstance(this).getTheme().equals("bluegrey");
        final boolean fbtheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("facebooktheme");
        final boolean blacktheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("darktheme");
        final boolean dracula = PreferencesUtility.getInstance(this).getFreeTheme().equals("draculatheme");
        final boolean folio = PreferencesUtility.getInstance(this).getFreeTheme().equals("materialtheme");
        final boolean folioclassic = PreferencesUtility.getInstance(this).getFreeTheme().equals("folioclassic");

        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isFolioTheme)
                setTheme(R.style.FolioBlue);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.quick_message);

        } else {


            if (isDarkTheme)
                setTheme(R.style.FolioDark);


            if (isPinkTheme)
                setTheme(R.style.FolioPink);


            if (isBlueGreyTheme)
                setTheme(R.style.BlueGrey);


            super.onCreate(savedInstanceState);
            setContentView(R.layout.quick_message);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        webView = (WebView) findViewById(R.id.text_box);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (BB10; Touch) AppleWebKit/537.1+ (KHTML, like Gecko) Version/10.0.0.1337 Mobile Safari/537.1+");
        webView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        String webViewUrl = "https://m.facebook.com/messages";

        webView.loadUrl(webViewUrl);
        webView.setWebViewClient(new FolioWebView() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
               
            }
            }

            

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                if (fbtheme)
                            injectDefaultCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("facebooktheme", "facebooktheme"));

                        if (folio)
                            injectMaterialCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("materialtheme", "materialtheme"));

                        if (folioclassic)
                            injectClassicCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("folioclassic", "folioclassic"));

                        if (blacktheme)
                            injectDarkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("darktheme", "darktheme"));

                        if (dracula)
                            injectDraculaCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("draculatheme", "draculatheme"));

                        if (isPinkTheme)
                            injectPinkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pinktheme", "pinktheme"));

                        if (isBlueGreyTheme)
                            injectBGCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("bluegrey", "bluegrey"));


                        injectFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                    if (url.contains("sharer") || url.contains("/composer/") || url.contains("throwback_share_source")) {
                        showFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                    } else {
                        injectFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                    }
                } catch (NullPointerException e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }

            }
        });


        webView.setWebChromeClient(new WebChromeClient() {


            @Override
            public void
            onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                OldMessages.this.setTitle(title);
            }


            // for Lollipop, all in one
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    // create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {

                    }


                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

            private File createImageFile() throws IOException {

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");

                if (!imageStorageDir.exists()) {

                    imageStorageDir.mkdirs();
                }

                imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;

                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");

                    if (!imageStorageDir.exists()) {

                        imageStorageDir.mkdirs();
                    }

                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception e) {

                }

            }

            // not needed but let's make it overloaded just in case
            // openFileChooser for Android < 3.0
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // openFileChooser for other Android versions

            /** may not work on KitKat due to lack of implementation of openFileChooser() or onShowFileChooser()
             *  https://code.google.com/p/android/issues/detail?id=62220
             *  however newer versions of KitKat fixed it on some devices */
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {

                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {

                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.minimize:
                webView.loadUrl("https://m.facebook.com/buddylist.php");
                return false;


            case R.id.refresh_mess:
                webView.reload();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();
    }


    private void injectDefaultCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("fbdefault.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectMaterialCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("foliotheme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectClassicCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("folioclassic.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectDarkCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("black.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception b) {
            b.printStackTrace();
        }
    }

    private void injectDraculaCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("dracula.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectPinkCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("pink_theme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectFbBar(String mode) {
        try {
            InputStream inputStream = getAssets().open("fb_bar.css");
            byte[] buffer = new byte[inputStream.available()];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void showFbBar(String mode) {
        try {
            InputStream inputStream = getAssets().open("showfbar.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }



    private void injectHide(String mode) {
        try {
            InputStream inputStream = getAssets().open("hidepeople.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectShow(String mode) {
        try {
            InputStream inputStream = getAssets().open("showpeople.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }



    private void injectBGCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("blue_grey.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }
}



