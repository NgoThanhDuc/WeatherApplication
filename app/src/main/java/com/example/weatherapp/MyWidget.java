package com.example.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.weatherapp.activity.MainActivity;

import io.paperdb.Paper;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals("update_widget")) {
            // Manual or automatic widget update started

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);

            String temp = Paper.book().read("temp");
            int image = Paper.book().read("image");

            // Update text, images, whatever - here
            remoteViews.setTextViewText(R.id.temp, temp);
            remoteViews.setImageViewResource(R.id.image, image);

            // Trigger widget layout update
            AppWidgetManager.getInstance(context).updateAppWidget(
                    new ComponentName(context, MyWidget.class), remoteViews);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
            views.setOnClickPendingIntent(R.id.linerWidget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

            // init Paper
            Paper.init(context);

            // Read content
            String temp = Paper.book().read("temp");
            int image = Paper.book().read("image");

            // Construct the RemoteViews object

            views.setTextViewText(R.id.temp, temp);
            views.setImageViewResource(R.id.image, image);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

