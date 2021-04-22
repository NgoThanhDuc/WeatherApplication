package com.example.weatherapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.example.weatherapp.R;
import com.example.weatherapp.activities.MainActivity;
import com.example.weatherapp.network.CheckConnection;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DialogUtil {

    private Dialog dialogCheckConnection = null;

    public void showConnectDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_check_connect, null);

        Button btn_retry = view.findViewById(R.id.btn_retry);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(context)) {
                    dialogCheckConnection.dismiss();

                    context.startActivity(new Intent(context, MainActivity.class));

                } else {
                    dialogCheckConnection.dismiss();
                    dialogCheckConnection.show();
                }
            }
        });

        builder.setView(view);
        dialogCheckConnection = builder.create();
        dialogCheckConnection.setCanceledOnTouchOutside(false);
        dialogCheckConnection.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogCheckConnection.getWindow().setWindowAnimations(R.style.ConnectionDialogAnimation);
        dialogCheckConnection.show();

    }

    public void showDialogNoUpdateData(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_update_data, null);

        builder.setView(view);


        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow().setWindowAnimations(R.style.ConnectionDialogAnimation);
        alertDialog.show();
    }

    public void showDialogExit(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_exit, null);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btn_dialogExitYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                context.startActivity(intent);
            }
        });

        view.findViewById(R.id.btn_dialogExitNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow().setWindowAnimations(R.style.ConnectionDialogAnimation);
        alertDialog.show();
    }

    public void showDialogWarningPermission(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_warning_permission_granted, null);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btn_warningYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);

            }
        });

        view.findViewById(R.id.btn_warningNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                context.startActivity(intent);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow().setWindowAnimations(R.style.ConnectionDialogAnimation);
        alertDialog.show();
    }

}
