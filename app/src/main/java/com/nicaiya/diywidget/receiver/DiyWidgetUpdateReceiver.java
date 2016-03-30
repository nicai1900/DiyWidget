package com.nicaiya.diywidget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.DiyWidgetUpdater;

/**
 * Created by zhengjie on 16/3/24.
 */
public class DiyWidgetUpdateReceiver extends BroadcastReceiver {

    public static final String TIME_CHANGE = "com.nicaiya.diywidget.TIME_CHANGE";
    public static final String BATTERY_CHANGE = "com.nicaiya.diywidget.BATTERY_CHANGE";

    private static DiyWidgetApplication diyWidgetApplication;
    private static DiyWidgetUpdater appWidgetUpdater;

    public static void init() {
        if (diyWidgetApplication == null) {
            diyWidgetApplication = DiyWidgetApplication.getInstance();
        }
        if (appWidgetUpdater == null) {
            appWidgetUpdater = DiyWidgetUpdater.getInstance(diyWidgetApplication);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TIME_CHANGE.equals(action)
                || BATTERY_CHANGE.equals(action)) {
            appWidgetUpdater.updateAllWidget();
        }
    }
}
