package com.nicaiya.diywidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zhengjie on 16/3/24.
 */
public class WidgetUpdateReceiver extends BroadcastReceiver {

    public static final String TIME_CHANGE = "com.nicaiya.diywidget.TIME_CHANGE";
    public static final String BATTERY_CHANGE = "com.nicaiya.diywidget.BATTERY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TIME_CHANGE.equals(action) || BATTERY_CHANGE.equals(action)) {
            AppWidgetUpdater.getInstance(context).updateAllWidget();
        }
    }
}
