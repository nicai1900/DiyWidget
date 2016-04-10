package com.nicaiya.diywidget;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.util.Log;

import com.nicaiya.diywidget.database.ConfigDataBase;
import com.nicaiya.diywidget.font.FontManager;
import com.nicaiya.diywidget.receiver.DiyWidgetUpdateReceiver;

/**
 * Created by zhengjie on 16/3/16.
 */
public class DiyWidgetApplication extends Application {

    private static final boolean DEG = BuildConfig.DEBUG;
    private static final String TAG = DiyWidgetApplication.class.getSimpleName();

    private static boolean isAllReceiverRegistered = false;

    private int batteryLevelPercent;

    private int versionCode;
    private String versionName;

    private static IntentFilter timeActionFilter;
    private static IntentFilter batteryFilter;

    private BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEG) {
                Log.d(TAG, "action: " + intent.getAction());
            }
            Intent newIntent = new Intent(DiyWidgetUpdateReceiver.TIME_CHANGE);
            newIntent.setComponent(new ComponentName(context, DiyWidgetUpdateReceiver.class));
            sendBroadcast(newIntent);
        }
    };

    private BroadcastReceiver batteryChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int scaledLevel = scale != 0 ? (level * 100 / scale) : level;
            if (batteryLevelPercent != scaledLevel) {
                setBatteryLevelPercent(scaledLevel);
                getConfigDataBase().saveBatteryLevel(batteryLevelPercent);

                Intent newIntent = new Intent(DiyWidgetUpdateReceiver.BATTERY_CHANGE);
                newIntent.setComponent(new ComponentName(context, DiyWidgetUpdateReceiver.class));
                sendBroadcast(newIntent);
            }
            Log.d(TAG, "action: " + intent.getAction() + " level: " + level);
        }
    };

    private static Context context;
    private static DiyWidgetApplication sInstance;

    public static DiyWidgetApplication getInstance() {
        return sInstance;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sInstance = this;
        ResourceUtil.setContext(this);
        timeActionFilter = new IntentFilter();
        timeActionFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeActionFilter.addAction(Intent.ACTION_TIME_TICK);
        timeActionFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        registerAllReceiver();
        DiyWidgetUpdateReceiver.init();
    }

    public FontManager getFontManager() {
        return FontManager.getInstance(this);
    }

    public ConfigDataBase getConfigDataBase() {
        return ConfigDataBase.getInstance(this);
    }

    public void registerAllReceiver() {
        if (!isAllReceiverRegistered) {
            try {
                registerReceiver(timeChangeReceiver, timeActionFilter);
                registerReceiver(batteryChangeReceiver, batteryFilter);
                isAllReceiverRegistered = true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void unregisterAllReceiver() {
        if (isAllReceiverRegistered) {
            try {
                unregisterReceiver(timeChangeReceiver);
                unregisterReceiver(batteryChangeReceiver);
                isAllReceiverRegistered = false;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void checkAllReceiverRegister() {
        if (getConfigDataBase().countWidget() == 0) {
            unregisterAllReceiver();
            return;
        }
        registerAllReceiver();
    }

    public int getBatteryLevelPercent() {
        if (batteryLevelPercent == 0) {
            try {
                batteryLevelPercent = getConfigDataBase().loadBatteryLevel();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return batteryLevelPercent;
    }

    public void setBatteryLevelPercent(int battery) {
        batteryLevelPercent = battery;
    }

    private void updateVersionInfo() {
        if (versionName == null) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                if (info != null) {
                    versionName = info.versionName;
                    versionCode = info.versionCode;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public String getVersionName() {
        updateVersionInfo();
        return versionName;
    }

    public int getVersionCode() {
        updateVersionInfo();
        return versionCode;
    }
}
