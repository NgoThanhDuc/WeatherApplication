package com.example.weatherapp.network;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.weatherapp.R;
import com.example.weatherapp.activities.MainActivity;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.weatherapp.activities.SplashScreenActivity.permissionGranted;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private Dialog dialogCheckConnection = null;

    @Override
    public void onReceive(final Context context, Intent intent) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_check_connect, null);

        Button btn_retry = view.findViewById(R.id.btn_retry);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(context)) {
                    dialogCheckConnection.dismiss();
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            }
        });

        builder.setView(view);
        dialogCheckConnection = builder.create();
        dialogCheckConnection.setCanceledOnTouchOutside(false);
        dialogCheckConnection.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogCheckConnection.getWindow().setWindowAnimations(R.style.ConnectionDialogAnimation);


        try {
            if (isOnline(context)) {
                dialogCheckConnection.dismiss();
                permissionGranted(context);
            } else {
                dialogCheckConnection.show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}