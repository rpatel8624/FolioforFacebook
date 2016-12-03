package com.creativtrendz.folio.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.creativetrends.folio.app.key.R;
import com.creativtrendz.folio.utils.AppTheme;
import com.creativtrendz.folio.utils.ImageGrabber;
import com.creativtrendz.folio.utils.PhotoClickListener;
import com.creativtrendz.folio.utils.PreferencesUtility;

import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

//Created by Jorell on 5/28/2016.
public class PhotoViewer extends AppCompatActivity {
    private static final int REQUEST_STORAGE = 1;
    private static String appDirectoryName;
    ImageView fullImage;
    PhotoViewAttacher Image;
    String title;
    String url;
    TextView name;
    ImageView commentButton;
    ImageView likeButton;
    PhotoClickListener listener;
    PhotoViewAttacher mAttacher;
    SharedPreferences preferences;

    @SuppressWarnings("ConstantConditions")


    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            }
            setContentView(R.layout.photoviewer);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);

            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            url = getIntent().getStringExtra("url");
            title = getIntent().getStringExtra("title");
            name = (TextView) findViewById(R.id.pic_title);
            commentButton = (ImageView) findViewById(R.id.comment);
            likeButton = (ImageView) findViewById(R.id.like);
            if (url == null) {
                onBackPressed();
                onDestroy();
            }
            name.setText(title);
            fullImage = (ImageView) findViewById(R.id.pictureholder);
            mAttacher = new PhotoViewAttacher(fullImage);
            mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            public void onPhotoTap(View view, float f, float f2) {
                listener.onPhotoClick();
            }

            public void onOutsidePhotoTap() {
            }

        });
            appDirectoryName = getString(R.string.app_name).replace(" ", " ");            
            loadImage();


            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.clear(fullImage);
                    finish();

                }
            });


            likeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Glide.clear(fullImage);
                    finish();

                }
            });


        }



    private void loadImage() {
        Glide.with(this).load(url).listener(new RequestListener<String, GlideDrawable>() {
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                fullImage.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
                findViewById(R.id.photoprogress).setVisibility(View.GONE);
                return false;
            }
        }).into(fullImage);
        Image = new PhotoViewAttacher(fullImage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_menu, menu);
        return true;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] storagePermission;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.photo_save:
                requestStoragePermission();
                return true;

            case R.id.photo_share:
                if (hasStoragePermission()) {
                            Toast.makeText(getBaseContext(), R.string.context_share_image_progress, Toast.LENGTH_SHORT).show();
                    new ImageGrabber(new ImageGrabber.OnImageLoaderListener() {
                        public void onError(ImageGrabber.ImageError error) {
                            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }

                        public void onProgressChange(int percent) {
                        }

                        public void onComplete(Bitmap result) {
                            try {
                                FileOutputStream stream = openFileOutput("bitmap.png", 0);
                                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), result, "Folio", null));
                                stream.close();
                                result.recycle();
                                Intent share = new Intent("android.intent.action.SEND");
                                share.setType("image/*");
                                share.putExtra("android.intent.extra.STREAM", uri);
                                startActivity(Intent.createChooser(share, getString(R.string.context_share_image)));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(PhotoViewer.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).download(url, false);
                    return true;
                }
                storagePermission = new String[REQUEST_STORAGE];
                storagePermission[0] = "android.permission.WRITE_EXTERNAL_STORAGE";
                ActivityCompat.requestPermissions(this, storagePermission, REQUEST_STORAGE);
                return true;


            case R.id.photo_copy:
                ClipboardManager clipboard = (ClipboardManager) PhotoViewer.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Image Share", url);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getBaseContext(), R.string.content_copy_link_done, Toast.LENGTH_SHORT).show();
                return true;


            case R.id.photo_open:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    finish();
                } catch (ActivityNotFoundException e) {

                    e.printStackTrace();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Glide.clear(fullImage);
        fullImage.setImageDrawable(null);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);


    }



    public void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        Glide.clear(fullImage);
        System.gc();


    }


    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else {
            if (url != null)
                saveImageToDisk(url, null, null);
        }
    }


    private boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (url != null)
                        saveImageToDisk(url, null, null);
                } else {
                    Toast.makeText(getBaseContext(), getString( R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void saveImageToDisk(final String url, final String contentDisposition, final String mimeType) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);            
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, filename);
            request.setVisibleInDownloadsUi(true);
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            }
            if (intent != null) {
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }
            if (intent != null) {
                intent.setType("*/*");
            }
            Toast.makeText(PhotoViewer.this, getString(R.string.fragment_main_downloading), Toast.LENGTH_SHORT).show();
        } catch (Exception exc) {
            Toast.makeText(PhotoViewer.this, exc.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}




