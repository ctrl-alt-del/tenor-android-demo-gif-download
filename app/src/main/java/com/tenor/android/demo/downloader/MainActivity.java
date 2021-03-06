package com.tenor.android.demo.downloader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ImageView mImageView;
    private static final int REQUEST_PERMISSIONS = 0;
    private static final String GIF_URL = "https://media.tenor.co/images/782ba18e2ff1bb49a36ade1ab90f2869/tenor.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.gif_view);

        // Make sure you have granted the storage permission
        requestPermissions();
    }

    private void requestPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        } else {
            downloadGif();
        }
    }

    /**
     * Download GIF using {@link GifDownloader}
     */
    private void downloadGif() {
        new GifDownloader<MainActivity>(this, BuildConfig.APPLICATION_ID, GIF_URL) {

            public void success(@Nullable Uri uri) {
                if (uri == null) {
                    return;
                }
                /*
                 * load the Uri content into ImageView to prove the Uri is working properly
                 *
                 * use override() to specific the dimension of the gif to be displayed
                 */
                Glide.with(MainActivity.this).load(uri).asGif().override(300, 300).into(mImageView);
            }

            @Override
            public void failure() {

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                boolean hasPermissions = true;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        hasPermissions = false;
                        break;
                    }
                }
                if (hasPermissions) {
                    downloadGif();
                } else {
                    requestPermissions();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
