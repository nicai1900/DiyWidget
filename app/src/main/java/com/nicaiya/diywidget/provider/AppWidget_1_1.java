package com.nicaiya.diywidget.provider;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.nicaiya.AbsConfigDataProvider;
import android.os.Process;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.DiyWidgetConfigActivity;
import com.nicaiya.diywidget.DiyWidgetUpdater;
import com.nicaiya.diywidget.EditSelectActivity;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.database.ConfigDataBase;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.SharedPreferencesManager;
import com.nicaiya.diywidget.model.object.WidgetData;
import com.nicaiya.diywidget.view.MainMenuView;

import java.io.InputStream;
import java.io.OutputStream;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public class AppWidget_1_1 extends AbsConfigDataProvider {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = AppWidget_1_1.class.getSimpleName();

    public static final String ACTION_APPWIDGET_UPDATE_OPTIONS = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
    public static final String ACTION_ON_CLICK = "com.nicaiya.diywidget.appwidget.ON_CLICK";
    public static final String ACTION_ON_NEXT_CLICK = "com.nicaiya.diywidget.appwidget.NEXT_CLICK";
    public static final String ACTION_ON_PLAY_CLICK = "com.nicaiya.diywidget.appwidget.PLAY_PAUSE_CLICK";
    public static final String ACTION_ON_PREV_CLICK = "com.nicaiya.diywidget.appwidget.PREV_CLICK";
    public static final String ACTION_SET_DEFAULT_CONFIG_DATA = "com.nicaiya.diywidget.appwidget.SET_DEFAULT_CONFIG_DATA";

    private static final String EMPTY_FLAG = "empty";
    public static final String EXTRA_DEFAULT_FILE_NAME = "fileName";
    public static final String EXTRA_WIDGET_ID = "appWidgetId";
    public static final String UNINSTALL_TARGET_APP = "removeTargetApp";

    private static DiyWidgetApplication clockApplication;
    private static ConfigDataBase configDataBase;
    private static DiyWidgetUpdater updater;

    private void init() {
        if (clockApplication == null) {
            clockApplication = DiyWidgetApplication.getInstance();
        }
        if (configDataBase == null) {
            configDataBase = clockApplication.getConfigDataBase();
        }
        if (updater == null) {
            updater = DiyWidgetUpdater.getInstance(clockApplication);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((configDataBase == null || updater == null)) {
            init();
        }
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.d(TAG, "onReceive action:" + action);
        if (action != null) {
            if (ACTION_SET_DEFAULT_CONFIG_DATA.equals(action)) {
                onReceiveSetDefaultConfigData(context, intent);
            } else if ((ACTION_ON_CLICK.equals(action))
                    || (ACTION_ON_PLAY_CLICK.equals(action))
                    || (ACTION_ON_NEXT_CLICK.equals(action))
                    || (ACTION_ON_PREV_CLICK.equals(action))) {
                onClickWidget(context, intent);
            } else if (ACTION_APPWIDGET_UPDATE_OPTIONS.equals(action)) {
                updater.updateAllWidget();
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        try {
            updater.updateWidget(appWidgetIds);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        clockApplication.checkAllReceiverRegister();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        try {
            updater.removeWidget(appWidgetIds);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        clockApplication.checkAllReceiverRegister();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        clockApplication.checkAllReceiverRegister();
    }

    public void onGetConfigData(Context context, int appWidgetId, OutputStream out) {
        ConfigFileData configFileData = configDataBase.loadConfigFileDataByWidgetId(appWidgetId);
        if (configFileData == null) {
            try {
                out.write(EMPTY_FLAG.getBytes());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            configFileData.writeToOutPutStream(out);
        }
    }

    public boolean onSetConfigData(Context context, int appWidgetId, InputStream in) {
        ConfigFileData configFileData;
        try {
            Source source = Okio.source(in);
            BufferedSource bufferedSource = Okio.buffer(source);
            byte[] data = bufferedSource.readByteArray();
            if (data[0] == 'e' && data[1] == 'm' && data[2] == 'p' && data[3] == 't' && data[4] == 'y') {
                return false;
            } else if (data[0] == 'P' && data[1] == 'K' && data[2] == 3 && data[3] == 4) {
                configFileData = new ConfigFileData(data);
            } else if (data[0] == '<' && data[1] == '?' && data[2] == 'x' && data[3] == 'm' && data[4] == 'l') {
                configFileData = new ConfigFileData();
                configFileData.setXmlByteArray(data);
                configFileData.setXmlAppFileVersion();
            } else {
                throw new Exception("it is not xml or zip");
            }
            if (configFileData.getFileVersion() > 3) {
                throw new Exception("unknown version");
            }
            WidgetData widgetData = WidgetData.createFromConfigFileData(configFileData);
            if (widgetData == null) {
                throw new Exception("widgetData == null");
            }
            configDataBase.saveWidgetConfigData(widgetData);
            configDataBase.saveWidget(appWidgetId, widgetData.getName());
            updater.updateWidget(appWidgetId);
            clockApplication.checkAllReceiverRegister();
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private void updateMainList() {
        DiyWidgetConfigActivity clockConfigureActivity = ResourceUtil.getConfigActivity();
        if (clockConfigureActivity != null) {
            MainMenuView mainMenuView = clockConfigureActivity.getMainMenuView();
            if (mainMenuView != null) {
                mainMenuView.onUpdate();
            }
        }
    }

    private void onReceiveSetDefaultConfigData(Context context, Intent intent) {
        String fileName = intent.getStringExtra(EXTRA_DEFAULT_FILE_NAME);
        try {
            InputStream is = context.getAssets().open(fileName);
            boolean success = onSetConfigData(context, AppWidgetManager.INVALID_APPWIDGET_ID, is);
            if (success) {
                updateMainList();
            }
            is.close();
            configDataBase.removeWidgetByWidgetId(AppWidgetManager.INVALID_APPWIDGET_ID);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void onClickWidget(Context context, Intent intent) {
        String appWidgetIdStr = intent.getDataString();
        String action = intent.getAction();
        if (appWidgetIdStr != null) {
            int appWidgetId = Integer.valueOf(appWidgetIdStr);
            if (isClicked(context, appWidgetId)) {
                if ((ACTION_ON_PLAY_CLICK.equals(action))
                        || (ACTION_ON_NEXT_CLICK.equals(action))
                        || (ACTION_ON_PREV_CLICK.equals(action))) {
                    //updater.onClickMusicWidget(appWidgetId, action);
                    return;
                }
                updater.onClickWidget(appWidgetId);
                return;
            }
            ComponentName componentName = new ComponentName(context, EditSelectActivity.class);
            Intent launchIntent = new Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .setComponent(componentName);
            Intent editIntent = new Intent(launchIntent);
            editIntent.putExtra(EXTRA_WIDGET_ID, appWidgetId);
            editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(editIntent);
            if (ResourceUtil.getCurrentActivity() != null) {
                ResourceUtil.getCurrentActivity().finish();
                ResourceUtil.setConfigActivity(null);
                ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                    if (processInfo.processName.equals(clockApplication.getApplicationInfo().packageName)) {
                        Process.killProcess(processInfo.pid);
                    }
                }
            }
        }
    }

    private boolean isClicked(Context context, int appWidgetId) {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        return sharedPreferencesManager.getWidgetClick(appWidgetId);
    }
}


