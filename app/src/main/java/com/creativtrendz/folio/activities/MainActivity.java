package com.creativtrendz.folio.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.notifications.FolioNotifications;
import com.creativtrendz.folio.notifications.FolioReceiver;
import com.creativtrendz.folio.services.Connectivity;
import com.creativtrendz.folio.ui.FolioHelpers;
import com.creativtrendz.folio.ui.FolioInterfaces;
import com.creativtrendz.folio.ui.FolioWebViewScroll;
import com.creativtrendz.folio.utils.FolioListener;
import com.creativtrendz.folio.utils.PreferencesUtility;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.greysonparrelli.permiso.Permiso;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.squareup.picasso.Picasso;

import net.grandcentrix.tray.TrayAppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    private static Activity mainActivity;
    private final static String LOG_TAG = "Folio";
    public static final String FACEBOOK = "https://m.facebook.com/";
    private final BadgeStyle BADGE_GRAY_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, Color.parseColor("#595c68"), Color.parseColor("#595c68"), Color.WHITE);
    static final List<String> FB_PERMISSIONS = Arrays.asList("public_profile", "user_friends");
    int request_Code = 1;
    public View mCoordinatorLayoutView;
    private CallbackManager callbackManager;
    private Snackbar snackbar = null;
    private String folioUser = null;
    DrawerLayout drawerLayoutFavs;
    private DrawerLayout drawerLayout;
    private static SharedPreferences preferences;
    private TrayAppPreferences trayPreferences;
    private static final int REQUEST_STORAGE = 1;
    private MenuItem mNotificationButton;
    private MenuItem mMessagesButton;
    public Toolbar toolbar;
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2981279;
    NavigationView navigationView;
    NavigationView navigationViewFavs;
    public SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionMenu FAB;
    private FolioWebViewScroll webView;
    private final View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.jumpFab:
                    webView.loadUrl("javascript:scroll(0,0)");
                    break;

                case R.id.shareFab:
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.share_action_subject);
                    i.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                    startActivity(Intent.createChooser(i, getString(R.string.share_action)));
                    break;

                case R.id.photoFab:
                        webView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22https%3A%2F%2Fm.facebook.com%2F%3Fpageload%3Dcomposer_photo%22%7D%7D)()");

                    break;
                case R.id.updateFab:
                    ShareLinkContent.Builder post = new ShareLinkContent.Builder();
                    post = post.setContentUrl(Uri.parse(""));
                    post.setContentTitle("");
                    ShareDialog.show(MainActivity.this, post.build());
                    break;
                default:
                    break;
            }
            FAB.close(true);
        }
    };
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private int previousUiVisibility;
    public static final String PREFS_NAME = "MyPrefsFile";

    private String mPendingImageUrlToSave = null;
    protected final static String URL_PAGE_SHARE_LINKS = "/sharer.php?u=%s&t=%s";


    private Context mContext = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    View mCustomView;
    FrameLayout customViewContainer;
    Window window;
    
    private CustomViewCallback mCustomViewCallback;

    
    private static final String MESSENGER = "Mozilla/5.0 (Linux; Linux x86_64; LG-H815 Build/MRA58K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/49.0.2623.105 Safari/537.36\"";

    private Handler mUiHandler = new Handler();
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView userNameView;
    List<String> bookmarkUrls;
    List<String> bookmarkTitles;


    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }


    @Override
    @SuppressLint({"setJavaScriptEnabled", "CutPasteId", "ClickableViewAccessibility", "SdCardPath"})
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;
        boolean isFolioTheme = PreferencesUtility.getInstance(this).getTheme().equals("folio");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        final boolean isBlueGreyTheme = PreferencesUtility.getInstance(this).getTheme().equals("bluegrey");
        boolean defaultfont = PreferencesUtility.getInstance(this).getFont().equals("default_font");
        boolean mediumfont = PreferencesUtility.getInstance(this).getFont().equals("medium_font");
        boolean largefont = PreferencesUtility.getInstance(this).getFont().equals("large_font");
        boolean xlfont = PreferencesUtility.getInstance(this).getFont().equals("xl_font");
        boolean xxlfont = PreferencesUtility.getInstance(this).getFont().equals("xxl_font");
        boolean smallfont = PreferencesUtility.getInstance(this).getFont().equals("small_font");
        boolean topnews = PreferencesUtility.getInstance(this).getFeed().equals("top_news");
        boolean defaultfeed = PreferencesUtility.getInstance(this).getFeed().equals("default_news");
        boolean mostrecent = PreferencesUtility.getInstance(this).getFeed().equals("most_recent");
        final boolean fbtheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("facebooktheme");
        final boolean blacktheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("darktheme");
        final boolean dracula = PreferencesUtility.getInstance(this).getFreeTheme().equals("draculatheme");
        final boolean folio = PreferencesUtility.getInstance(this).getFreeTheme().equals("materialtheme");
        final boolean folioclassic = PreferencesUtility.getInstance(this).getFreeTheme().equals("folioclassic");


        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isFolioTheme)
                setTheme(R.style.FolioTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        } else {

            if (isDarkTheme)
                setTheme(R.style.DarkTheme);


            if (isPinkTheme)
                setTheme(R.style.PinkTheme);


            if (isBlueGreyTheme)
                setTheme(R.style.BlueGreyTheme);


            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(this.getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            PreferenceManager.setDefaultValues(this, R.xml.navigation_preferences, false);
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            PreferenceManager.setDefaultValues(this, R.xml.customize_preferences, true);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            trayPreferences = new TrayAppPreferences(getApplicationContext());
            setContentView(R.layout.activity_main);
            Permiso.getInstance().setActivity(this);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);


            if (preferences.getBoolean("enable_gplus", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.googleplus).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.googleplus).setVisible(false);

            }

            if (preferences.getBoolean("enable_instagram", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.instagram).setVisible(true);
            } else {
                navigationView.getMenu().findItem(R.id.instagram).setVisible(false);

            }


            if (preferences.getBoolean("recent_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.newsfeed).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.newsfeed).setVisible(false);

            }


            if (preferences.getBoolean("trending_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.trending).setVisible(true);
            } else {
                navigationView.getMenu().findItem(R.id.trending).setVisible(false);

            }

            if (preferences.getBoolean("friends_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.friends).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.friends).setVisible(false);

            }


            if (preferences.getBoolean("groups_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.group).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.group).setVisible(false);

            }

            if (preferences.getBoolean("pages_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.pages).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.pages).setVisible(false);

            }

            if (preferences.getBoolean("photos_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.photos).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.photos).setVisible(false);

            }

            if (preferences.getBoolean("events_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.events).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.events).setVisible(false);

            }


            if (preferences.getBoolean("thisday_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.onthisday).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.onthisday).setVisible(false);

            }

            if (preferences.getBoolean("saved_off", false)) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.saved).setVisible(true);
            } else {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
                navigationView.getMenu().findItem(R.id.saved).setVisible(false);

            }


            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                navigationView.getMenu().findItem(R.id.loginFolio).setVisible(false);
                navigationView.getMenu().findItem(R.id.goPro).setVisible(true);
            } else {
                navigationView.getMenu().findItem(R.id.loginFolio).setVisible(true);
                navigationView.getMenu().findItem(R.id.goPro).setVisible(false);

            }

            String lockState = (String) getIntent().getSerializableExtra("state");
            if (lockState != null && lockState.equals("unlocked")) {
            } else {
                if (preferences.getBoolean("folio_locker", false)) {
                    startActivity(new Intent(MainActivity.this, FolioUnlock.class));
                }
            }


            if (preferences.getBoolean("quickbar_pref", false)) {
                RemoteViews remoteView = new RemoteViews(MainActivity.this.getPackageName(), R.layout.quickbar);
                NotificationManager notificationmanager = (NotificationManager) FolioApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                remoteView.setTextViewText(R.id.quick, getString(R.string.app_name));
                remoteView.setTextViewText(R.id.quick_bar, getString(R.string.quick_bar));

                Builder builder = new Builder(FolioApplication.getContextOfApplication());
                builder.setSmallIcon(R.drawable.ic_stat_f)
                        .setOngoing(true)
                        .setContent(remoteView)
                        .setPriority(Notification.PRIORITY_MIN);


                Intent quickNewsfeed = new Intent(getApplicationContext(), QuickInstagram.class);
                quickNewsfeed.putExtra("start_url", "https://m.facebook.com/notifications");
                quickNewsfeed.setAction(Intent.ACTION_VIEW);
                PendingIntent newsIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickNewsfeed,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_notifications, newsIntent);

                Intent quickMessages = new Intent(this, QuickGoogle.class);
                quickMessages.putExtra("start_url", "https://m.facebook.com/messages");
                quickMessages.setAction(Intent.ACTION_VIEW);
                PendingIntent messagesIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickMessages,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_messages, messagesIntent);


                Intent quickFriends = new Intent(this, QuickFacebook.class);
                quickFriends.putExtra("start_url", "https://m.facebook.com/friends/center/friends/");
                quickFriends.setAction(Intent.ACTION_VIEW);
                PendingIntent friendsIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickFriends,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_friends, friendsIntent);


                notificationmanager.notify(22, builder.build());


            }

            if (preferences.getBoolean("first_run", true)) {
                onFblogin();
                preferences.edit().putBoolean("first_run", false).apply();
            }


            if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false)) {
                FolioReceiver.scheduleAlarms(getApplicationContext(), false);
            }

            if (preferences.getBoolean("show_fab", false)) {
                FAB = (FloatingActionMenu) findViewById(R.id.fab);
                FAB.setVisibility(View.VISIBLE);
            } else {
                FAB = (FloatingActionMenu) findViewById(R.id.fab);
                FAB.setVisibility(View.GONE);
            }


            toolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    webView.loadUrl("javascript:scroll(0,0)");

                }
            });


            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            drawerLayoutFavs = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationViewFavs = (NavigationView) findViewById(R.id.folio_favorites);
            customViewContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
            FAB = (FloatingActionMenu) findViewById(R.id.fab);
            View headerView = navigationView.inflateHeaderView(R.layout.header);
            FrameLayout drawerHeader = (FrameLayout) headerView.findViewById(R.id.header);
            drawerHeader.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    webView.loadUrl("https://m.facebook.com/me/?refid=7");
                                                    drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
                                                    drawerLayout.closeDrawers();
                                                }
                                            }
            );


            String webViewUrl = "https://m.facebook.com";

            if (defaultfeed)
                webViewUrl = "https://m.facebook.com";

            if (mostrecent)
                webViewUrl = "https://m.facebook.com/home.php?sk=h_chr&refid=8";

            if (topnews)
                webViewUrl = "https://m.facebook.com/home.php?sk=h_nor&refid=8";


            mCoordinatorLayoutView = findViewById(R.id.coordinatorLayout);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeRefreshLayout.setColorSchemeResources(R.color.md_blue_500, R.color.md_deep_purple_700, R.color.bcP);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    webView.reload();
                    getUsersShit();
                }
            });


            findViewById(R.id.jumpFab).setOnClickListener(mFABClickListener);
            findViewById(R.id.shareFab).setOnClickListener(mFABClickListener);
            findViewById(R.id.photoFab).setOnClickListener(mFABClickListener);
            findViewById(R.id.updateFab).setOnClickListener(mFABClickListener);


            webView = (FolioWebViewScroll) findViewById(R.id.webView1);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            if (preferences.getBoolean("allow_location", false)) {
                webView.getSettings().setGeolocationEnabled(true);
                webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
            } else {
                webView.getSettings().setGeolocationEnabled(false);
            }
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            if (Build.VERSION.SDK_INT < 18) {
                webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024);
            }
            webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.setListener(this, new FolioListener(this, webView));
            webView.addJavascriptInterface(new FolioInterfaces(this), "android");

            if (preferences.getBoolean("no_images", false))
                webView.getSettings().setLoadsImagesAutomatically(false);


            if (defaultfont)
                webView.getSettings().setTextZoom(100);

            if (smallfont)
                webView.getSettings().setTextZoom(90);


            if (mediumfont)
                webView.getSettings().setTextZoom(105);


            if (largefont)
                webView.getSettings().setTextZoom(110);


            if (xlfont)
                webView.getSettings().setTextZoom(120);

            if (xxlfont)
                webView.getSettings().setTextZoom(150);


            boolean isConnectedMobile = Connectivity.isConnectedMobile(getApplicationContext());
            boolean isFacebookZero = preferences.getBoolean("facebook_zero", false);


            String sharedSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
            String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);


            if (sharedUrl != null) {
                if (!sharedUrl.equals("")) {

                    if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                        int startUrlIndex = sharedUrl.indexOf("http:");
                        if (startUrlIndex > 0) {

                            sharedUrl = sharedUrl.substring(startUrlIndex);
                        }
                    }

                    webViewUrl = String.format("https://m.facebook.com/sharer.php?u=%s&t=%s", sharedUrl, sharedSubject);

                    webViewUrl = Uri.parse(webViewUrl).toString();
                }
            }


            if ((getIntent() != null && getIntent().getDataString() != null) && (!isFacebookZero || !isConnectedMobile)) {
                webViewUrl = getIntent().getDataString();


            } else if (isFacebookZero && isConnectedMobile) {

                webViewUrl = "https://0.facebook.com";

            }

            if ((getIntent() != null && getIntent().getDataString() != null) && (!isFacebookZero || !isConnectedMobile)) {
                webViewUrl = getIntent().getDataString();


            } else if (isFacebookZero && isConnectedMobile) {


            }


            try {

                if (getIntent().getExtras().getString("start_url") != null) {
                    String temp = getIntent().getExtras().getString("start_url");
                    if (!isFacebookZero || !isConnectedMobile)
                        webViewUrl = temp;

                    if (temp.equals("https://m.facebook.com/notifications"))
                        FolioNotifications.clearNotifications();
                    if (temp.equals("https://m.facebook.com/messages/"))
                        FolioNotifications.clearNotifications();

                }
            } catch (Exception ignored) {
            }


            if (!Connectivity.isConnected(this) && !preferences.getBoolean("offline_mode", false))
                webView.loadUrl("file:///android_asset/error.html");
            webView.loadUrl(webViewUrl);
            webView.setWebViewClient(new FolioWebView() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if ((url.contains("market://") || url.contains("mailto:")
                            || url.contains("play.google") || url.contains("youtube")
                            || url.contains("tel:")
                            || url.contains("vid:")) == true) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    }
                    if (url.contains("scontent") && url.contains("jpg")) {
                        if (url.contains("l.php?u=")) {
                            return false;
                        }
                        Intent photoViewer = new Intent(MainActivity.this, PhotoViewer.class);
                        photoViewer.putExtra("url", url);
                        photoViewer.putExtra("title", view.getTitle());
                        startActivity(photoViewer);
                        return true;

                    } else if (Uri.parse(url).getHost().endsWith("facebook.com")
                            || Uri.parse(url).getHost().endsWith("m.facebook.com")
                            || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                            || Uri.parse(url).getHost().endsWith("mobile.facebook.com/messages")
                            || Uri.parse(url).getHost().endsWith("m.facebook.com/messages")
                            || Uri.parse(url).getHost().endsWith("h.facebook.com")
                            || Uri.parse(url).getHost().endsWith("l.facebook.com")
                            || Uri.parse(url).getHost().endsWith("0.facebook.com")
                            || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                            || Uri.parse(url).getHost().endsWith("fbcdn.net")
                            || Uri.parse(url).getHost().endsWith("akamaihd.net")
                            || Uri.parse(url).getHost().endsWith("fb.me")
                            || Uri.parse(url).getHost().endsWith("googleusercontent.com")) {
                        return false;


                    } if (preferences.getBoolean("allow_inside", false)) {
                        Intent intent = new Intent(MainActivity.this, FolioBrowser.class);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
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


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    swipeRefreshLayout.setRefreshing(true);
                    
                }

                


                @SuppressLint("ResourceAsColor")
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    try {
                        swipeRefreshLayout.setRefreshing(false);
                        initalizeBookmarks(navigationViewFavs);
                        getUsersShit();
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


                        if (preferences.getBoolean("hide_people", false)) {
                            injectHide(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("hidepeople", "hidepeople"));
                        } else {
                            injectShow(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                        }
                            injectFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));

                        if (url.contains("messages") && preferences.getBoolean("use_messenger", false)) {
                            startActivity(new Intent(MainActivity.this, Messenger.class));
                            overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right);
                            FolioNotifications.clearMessages();
                            FolioHelpers.updateNums(webView);
                        } else {
                            if (url.contains("messages")) {
                                startActivity(new Intent(MainActivity.this, OldMessages.class));
                                overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right);
                                FolioNotifications.clearMessages();
                                FolioHelpers.updateNums(webView);
                            }
                        }
                        if (url.contains("sharer") || url.contains("/composer/") || url.contains("throwback_share_source")) {
                            showFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                            swipeRefreshLayout.setEnabled(false);
                        } else {
                        swipeRefreshLayout.setEnabled(true);
                        }
                    } catch (NullPointerException e) {
                        Log.e("onLoadResourceError", "" + e.getMessage());
                        e.printStackTrace();
                    }

                }
            });


            webView.setWebChromeClient(new WebChromeClient() {
            @SuppressWarnings("deprecation")
            @Override
             public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
               onShowCustomView(view, callback);
               }

             @Override
              public void onShowCustomView(View view,CustomViewCallback callback) {
                 try {
                     if (mCustomView != null) {
                         callback.onCustomViewHidden();
                         return;
                     }
                     mCustomView = view;
                     customViewContainer.setVisibility(View.VISIBLE);
                     toolbar.setVisibility(View.GONE);
                     customViewContainer.addView(view);
                     mCustomViewCallback = callback;
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }



                @Override
                public void onHideCustomView() {
                    super.onHideCustomView();
                    try{
                        if (mCustomView == null)
                        return;
                    mCustomView.setVisibility(View.GONE);
                    customViewContainer.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                    customViewContainer.removeView(mCustomView);
                    mCustomViewCallback.onCustomViewHidden();
                    mCustomView = null;
                }catch(Exception e){
                        e.printStackTrace();
                    }

                }


                @Override
                public void onGeolocationPermissionsShowPrompt(String origin,
                                                               Callback callback) {

                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                    callback.invoke(origin, true, false);
                }


                @Override
                public void
                onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    try { if (title != null && title.contains("Facebook") || title.contains("1")) {
                        MainActivity.this.setTitle(R.string.app_name_toolbar);
                    } else {
                        MainActivity.this.setTitle(title);
                    }
                        if (title != null && title.contains("https://www.facebook.com/dialog/return")) {
                            webView.loadUrl(FACEBOOK);
                            Snackbar.make(mCoordinatorLayoutView, "Success", Snackbar.LENGTH_LONG).show();
                        }

                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                }

            });

            ActionItemBadge.update(this, navigationView.getMenu().findItem(R.id.newsfeed), (Drawable) null, BADGE_GRAY_FULL, Integer.MIN_VALUE);

            ActionItemBadge.update(this, navigationView.getMenu().findItem(R.id.friends), (Drawable) null, BADGE_GRAY_FULL, Integer.MIN_VALUE);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {


                    drawerLayout.closeDrawers();


                    switch (menuItem.getItemId()) {

                        case R.id.loginFolio:
                            onFblogin();
                            return true;

                        case R.id.goPro:
                            AlertDialog.Builder proalert =
                                    new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                            proalert.setTitle(getResources().getString(R.string.preference_donate_category));
                            proalert.setMessage(Html.fromHtml(getResources().getString(R.string.go_pro_message)));
                            proalert.setPositiveButton("Go Pro", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));
                                }
                            });
                            proalert.setNeutralButton("No Thanks", null);
                            proalert.show();
                            return true;

                        case R.id.googleplus:
                            Intent plus = new Intent(MainActivity.this, GoogleActivity.class);
                            startActivity(plus);
                            menuItem.setChecked(true);
                            return true;


                        case R.id.instagram:
                            Intent instagram = new Intent(MainActivity.this, InstagramActivity.class);
                            startActivity(instagram);
                            menuItem.setChecked(true);
                            return true;


                        case R.id.newsfeed:
                            webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                            FolioHelpers.updateNums(webView);
                            menuItem.setChecked(true);
                            return true;


                        case R.id.fbmenu:
                            webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");

                            menuItem.setChecked(true);
                            return true;

                        case R.id.trending:
                            webView.loadUrl("https://m.facebook.com/search/trending-news/?ref=bookmark&app_id=343553122467255");
                            menuItem.setChecked(true);
                            return true;

                        case R.id.friends:
                            webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                            FolioHelpers.updateNums(webView);
                            menuItem.setChecked(true);
                            return true;
                        case R.id.group:
                            webView.loadUrl("https://m.facebook.com/groups/?category=membership");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.pages:
                            webView.loadUrl("https://m.facebook.com/pages/launchpoint/?from=pages_nav_discover&ref=bookmarks");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.photos:
                            webView.loadUrl("https://m.facebook.com/profile.php?v=photos&soft=composer");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.events:
                            webView.loadUrl("https://m.facebook.com/events");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.onthisday:
                            webView.loadUrl("https://m.facebook.com/onthisday");
                            menuItem.setChecked(true);
                            return true;
                        case R.id.saved:
                            webView.loadUrl("https://m.facebook.com/saved");
                            menuItem.setChecked(true);
                            return true;


                        case R.id.trans:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://creativetrendsapps.oneskyapp.com/collaboration/project?id=66498")));
                            return true;


                        case R.id.settings:
                            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(settings);
                            return true;

                        default:

                            return true;
                    }
                }
            });


            drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {


                @Override
                public void onDrawerClosed(View drawerViewMain) {


                    super.onDrawerClosed(drawerViewMain);


                }

                @Override
                public void onDrawerOpened(View drawerViewMain) {


                    super.onDrawerOpened(drawerViewMain);

                    super.onDrawerSlide(drawerViewMain, 0);

                }
            };


            actionBarDrawerToggle.syncState();

        }


        initalizeBookmarks(navigationViewFavs);
        drawerLayoutFavs.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                initalizeBookmarks(navigationViewFavs);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                initalizeBookmarks(navigationViewFavs);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        navigationViewFavs.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getTitle() == getString(R.string.addPage)) {
                    if (!webView.getTitle().equals("Facebook")) {
                        addBookmark(webView.getTitle().replace("Facebook", ""), webView.getUrl());
                    }
                } else if (menuItem.getTitle() == getString(R.string.removePage)) {
                    removeBookmark(webView.getTitle().replace(" Facebook", ""));
                } else {
                    webView.loadUrl(bookmarkUrls.get(bookmarkTitles.indexOf(menuItem.getTitle())));
                    drawerLayoutFavs.closeDrawers();
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        mNotificationButton = menu.findItem(R.id.action_notifications);

        ActionItemBadge.update(this, mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);

        mMessagesButton = menu.findItem(R.id.action_messages);

        ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_messenger, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);


        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            if (navigationView.getMenu().getItem(i).isChecked()) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.search:
                webView.loadUrl("javascript:try{document.querySelector('#search_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "/search';}");
                return true;

            case R.id.folio_fav:
                drawerLayoutFavs.openDrawer(findViewById(R.id.folio_favorites));
                return true;


            case R.id.online:
                webView.loadUrl("https://m.facebook.com/buddylist.php");
                return true;


            case R.id.action_notifications:
                webView.loadUrl("https://mobile.facebook.com/notifications/");
                FolioNotifications.clearNotifications();
                FolioHelpers.updateNums(webView);
                return true;


            case R.id.action_messages:
                if (preferences.getBoolean("use_messenger", false)) {
                    Intent messenger = new Intent(MainActivity.this, Messenger.class);
                    startActivity(messenger);
                    FolioNotifications.clearMessages();
                    FolioHelpers.updateNums(webView);
                } else {
                    Intent messages = new Intent(MainActivity.this, OldMessages.class);
                    startActivity(messages);
                    FolioNotifications.clearMessages();
                    FolioHelpers.updateNums(webView);
                }
                return true;

            case R.id.fb_settings:
                webView.loadUrl("https://m.facebook.com/settings");
                return true;

            case R.id.logout:
                LoginManager.getInstance().logOut();
                finish();
                return true;


            case R.id.close:
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void onFblogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getUsersShit();
                        navigationView.getMenu().findItem(R.id.loginFolio).setVisible(false);
                        webView.loadUrl("https://mobile.facebook.com");
                    }

                    @Override
                    public void onCancel() {
                        Snackbar.make(mCoordinatorLayoutView, R.string.error_super_wrong, Snackbar.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Snackbar.make(mCoordinatorLayoutView, R.string.error_login, Snackbar.LENGTH_LONG).show();
                        LoginManager.getInstance().logOut();

                    }
                });
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webView.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(data.getData().toString()));
                startActivity(i);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String webViewUrl = getIntent().getDataString();


        boolean isConnectedMobile = Connectivity.isConnectedMobile(getApplicationContext());
        boolean isFacebookZero = preferences.getBoolean("facebook_zero", false);


        String sharedSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
        String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);


        if (sharedUrl != null) {
            if (!sharedUrl.equals("")) {

                if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                    int startUrlIndex = sharedUrl.indexOf("http:");
                    if (startUrlIndex > 0) {

                        sharedUrl = sharedUrl.substring(startUrlIndex);
                    }
                }

                webViewUrl = String.format("https://m.facebook.com/sharer.php?&app_id=749196541804006&u=%s&t=%s", sharedUrl, sharedSubject);
                webViewUrl = Uri.parse(webViewUrl).toString();

            }
        }

        try {
            if (getIntent().getExtras().getString("start_url") != null)
                webViewUrl = getIntent().getExtras().getString("start_url");

            if ("https://m.facebook.com/notifications".equals(webViewUrl))
                FolioNotifications.clearNotifications();
            if ("https://m.facebook.com/messages".equals(webViewUrl))
                FolioNotifications.clearMessages();
        } catch (Exception ignored) {
        }


        if (isFacebookZero && isConnectedMobile) {
        } else
            webView.loadUrl(webViewUrl);


        if (!Connectivity.isConnected(getApplicationContext()) && !preferences.getBoolean("offline_mode", false))


            if (getIntent().getBooleanExtra("apply_changes_to_app", false)) {
                finish();
                Intent restart = new Intent(MainActivity.this, MainActivity.class);
                startActivity(restart);
            }
    }


    public void setNotificationNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_public, null), num);
        } else {

            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), Integer.MIN_VALUE);
        }

    }

    public void setMessagesNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_messenger_new, null), num);
        } else {

            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_messenger, null), Integer.MIN_VALUE);
        }

    }


    public void setNewsNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(navigationView.getMenu().findItem(R.id.newsfeed), num);
        } else {

            ActionItemBadge.update(navigationView.getMenu().findItem(R.id.newsfeed), Integer.MIN_VALUE);
        }

    }

    public void setFriendsNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(navigationView.getMenu().findItem(R.id.friends), num);
        } else {

            ActionItemBadge.update(navigationView.getMenu().findItem(R.id.friends), Integer.MIN_VALUE);
        }

    }


    public void initalizeBookmarks(NavigationView navigationViewFavs) {
        bookmarkUrls = new ArrayList<>();
        bookmarkTitles = new ArrayList<>();

        final Menu menu = navigationViewFavs.getMenu();
        menu.clear();
        String result = preferences.getString("bookmarks", "[]");
        try {
            JSONArray bookmarksArray = new JSONArray(result);
            for (int i = 0; i < bookmarksArray.length(); i++) {
                JSONObject bookmark = bookmarksArray.getJSONObject(i);
                menu.add(bookmark.getString("title")).setIcon(R.drawable.ic_favorite);
                bookmarkTitles.add(bookmark.getString("title"));
                bookmarkUrls.add(bookmark.getString("url"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!bookmarkUrls.contains(webView.getUrl())) {
            menu.add(getString(R.string.addPage)).setIcon(R.drawable.ic_add);
        } else {
            menu.add(getString(R.string.removePage)).setIcon(R.drawable.ic_close_folio);
        }
    }

    public void addBookmark(String title, String url) {
        String result = preferences.getString("bookmarks", "[]");
        try {
            JSONArray bookmarksArray = new JSONArray(result);
            bookmarksArray.put(new JSONObject("{'title':'" + title + "','url':'" + url + "'}"));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("bookmarks", bookmarksArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initalizeBookmarks(navigationViewFavs);
    }

    public void removeBookmark(String title) {
        String result = preferences.getString("bookmarks", "[]");
        try {
            JSONArray bookmarksArray = new JSONArray(result);
            if (Build.VERSION.SDK_INT >= 19) {
                bookmarksArray.remove(bookmarkTitles.indexOf(title));
            } else {
                final List<JSONObject> objs = asList(bookmarksArray);
                objs.remove(bookmarkTitles.indexOf(title));
                final JSONArray out = new JSONArray();
                for (final JSONObject obj : objs) {
                    out.put(obj);
                }
                bookmarksArray = out;
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("bookmarks", bookmarksArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initalizeBookmarks(navigationViewFavs);
    }

    @Override
    public void onStart() {
        super.onStart();
        getUsersShit();
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
        registerForContextMenu(webView);
        trayPreferences.put("activity_visible", true);
        String lockState = (String) getIntent().getSerializableExtra("state");
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterForContextMenu(webView);
        trayPreferences.put("activity_visible", false);
    }


    @Override
    public void onDestroy() {
        Log.i("MainActivity", "Destroying...");
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();

    }

    @Override
    public boolean onLongClick(View v) {
        openContextMenu(v);
        return true;
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

    private void injectComposer(String mode) {
        try {
            InputStream inputStream = getAssets().open("composer.css");
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

    private void injectSelect(String mode) {
        try {
            InputStream inputStream = getAssets().open("selectshit.css");
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


    private void getUsersShit() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String userID = object.getString("id");
                    folioUser = object.getString("link");
                    final TextView name = (TextView) findViewById(R.id.profile_name);
                    name.setText(object.getString("name"));
                    final TextView email = (TextView) findViewById(R.id.user_email);
                    email.setText(getResources().getString(R.string.app_name) + " " + PreferencesUtility.getAppVersionName(getApplicationContext()));
                    final View header = findViewById(R.id.header);
                    Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + userID + "/picture?type=large").into((ImageView) findViewById(R.id.profile_pic));
                    Glide.with(getApplicationContext()).load(object.getJSONObject("cover").getString("source")).into((ImageView) findViewById(R.id.back_color));
                } catch (NullPointerException e) {
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,cover,link");
        request.setParameters(parameters);
        request.executeAsync();
    }


    public static Activity getMainActivity() {
        return mainActivity;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        try{
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }catch(Exception e){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // from stackoverflow http://stackoverflow.com/q/21667002
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        previousUiVisibility = decorView.getSystemUiVisibility();
        onConfigurationChanged(getResources().getConfiguration());

    }
}
