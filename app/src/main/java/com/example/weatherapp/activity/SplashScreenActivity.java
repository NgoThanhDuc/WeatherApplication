package com.example.weatherapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.weatherapp.R;
import com.example.weatherapp.until.DialogUntil;
import com.example.weatherapp.until.NetworkChangeReceiver;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView imageView, imageView2, imageView3, imageView4;
    private TextView textView;
    private Animation animation;
    private DialogUntil dialogUntil;
    private BroadcastReceiver mNetworkReceiver;
    private final int DELAY_MILLIS = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        init();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNetworkReceiver = new NetworkChangeReceiver();
                registerNetworkBroadcastForNougat();
            }
        }, DELAY_MILLIS);
    }

    private void init() {
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        textView = findViewById(R.id.textView);

        imageView.setAnimation(getAnimation(this, R.anim.top_splash_screen));
        imageView2.setAnimation(getAnimation(this, R.anim.right_splash_screen));
        imageView3.setAnimation(getAnimation(this, R.anim.left_splash_screen));
        imageView4.setAnimation(getAnimation(this, R.anim.bottom_splash_screen));
        textView.setAnimation(getAnimation(this, R.anim.blink));

        dialogUntil = new DialogUntil();
    }

    private Animation getAnimation(Context context, int anim) {
        animation = AnimationUtils.loadAnimation(context, anim);
        return animation;
    }

    public static void permissionGranted(Context context) {
        if (Build.VERSION.SDK_INT > 22) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1234);
            } else {
                context.startActivity(new Intent(context, MainActivity.class));
            }
        } else {
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1234 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

            } else {
                dialogUntil.showDialogWarningPermission(SplashScreenActivity.this);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterNetworkChanges();
    }

}

