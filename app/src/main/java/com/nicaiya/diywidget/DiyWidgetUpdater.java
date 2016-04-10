package com.nicaiya.diywidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import com.nicaiya.diywidget.database.ConfigDataBase;
import com.nicaiya.diywidget.drawable.WidgetDrawable;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.SharedPreferencesManager;
import com.nicaiya.diywidget.model.object.WidgetData;

import java.util.HashMap;
import java.util.Map;

public class DiyWidgetUpdater {

    private static final boolean DEG = BuildConfig.DEBUG;
    private static final String TAG = DiyWidgetUpdater.class.getSimpleName();

    private static final int UPDATE_PERIOD_ONE = 0;
    private static final int UPDATE_PERIOD_TWO = 1;
    private static final int UPDATE_PERIOD_FOUR = 2;

    public static int displayHeight;
    public static int displayWidth;

    private static DiyWidgetUpdater sInstance;

    private ConfigDataBase configDataBase;

    private AppWidgetManager appWidgetManager;

    private Context context;

    private final Map<Integer, RemoteViewData> remoteViewDataMap = new HashMap<>();

    public static DiyWidgetUpdater getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DiyWidgetUpdater.class) {
                if (sInstance == null) {
                    sInstance = new DiyWidgetUpdater(context);
                }
            }
        }
        return sInstance;
    }

    private DiyWidgetUpdater(final Context context) {
        this.context = context;
        configDataBase = ConfigDataBase.getInstance(context);
        appWidgetManager = AppWidgetManager.getInstance(context);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                updateWidget(appWidgetManager.getAppWidgetIds(new ComponentName(context, DiyWidgetProvider.class)));
                return null;
            }
        }.execute();

    }

    public void updateAllWidget() {
        synchronized (remoteViewDataMap) {
            for (RemoteViewData data : remoteViewDataMap.values()) {
                data.doDraw();
            }
        }
    }

    public void updateWidget(int[] appWidgetIds) {
        if (appWidgetIds != null) {
            for (int id : appWidgetIds) {
                updateWidget(id);
            }
        }
    }

    public void updateWidget(int appWidgetId) {
        synchronized (remoteViewDataMap) {
            RemoteViewData data;
            if (remoteViewDataMap.containsKey(appWidgetId)) {
                data = remoteViewDataMap.get(appWidgetId);
            } else {
                data = new RemoteViewData(appWidgetId);
                remoteViewDataMap.put(appWidgetId, data);
            }
            data.update();
            data.doDraw();
        }
    }


    public void removeWidget(int[] appWidgetIds) {
        if (appWidgetIds != null) {
            for (int id : appWidgetIds) {
                removeWidget(id);
            }
        }
    }

    public void removeWidget(int appWidgetId) {
        synchronized (remoteViewDataMap) {
            if (remoteViewDataMap.containsKey(appWidgetId)) {
                RemoteViewData data = remoteViewDataMap.remove(appWidgetId);
                data.deleteResource();
                SharedPreferencesManager.getInstance(context).removeWidgetId(appWidgetId);
                configDataBase.removeWidgetByWidgetId(appWidgetId);
            }
        }
    }

    public void onClickWidget(int appWidgetId) {
        synchronized (remoteViewDataMap) {
            RemoteViewData data;
            if (remoteViewDataMap.containsKey(appWidgetId)) {
                data = remoteViewDataMap.get(appWidgetId);
            } else {
                data = new RemoteViewData(appWidgetId);
                remoteViewDataMap.put(appWidgetId, data);
            }
            data.update();
            data.onClick();
        }
    }

    private class RemoteViewData {

        private WidgetData appWidgetData;
        private WidgetDrawable appWidgetDrawable;
        private final int appWidgetId;
        private Bitmap bitmap;
        private Bitmap resizeBitmap;
        private final Canvas bitmapCanvas = new Canvas();

        private PendingIntent pendingIntent;

        public RemoteViewData(int widgetId) {
            appWidgetId = widgetId;
            Intent intent = new Intent(context, DiyWidgetProvider.class);
            intent.setAction(DiyWidgetProvider.ACTION_ON_CLICK);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        }

        public void onClick() {
            if (appWidgetData == null) {
                update();
            }
            if (appWidgetData == null) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName(context, DiyWidgetConfigActivity.class));
                intent.putExtra("action", "select");
                intent.putExtra("appWidgetId", appWidgetId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                // TODO: 16/3/24 添加设置了插件内容的跳转实现
            }
        }

        public void doDrawDefault() {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_default);
            remoteViews.setOnClickPendingIntent(R.id.loading_tv, pendingIntent);
            try {
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        public void doDraw() {
            if (appWidgetData == null) {
                update();
            }
            if (appWidgetData == null) {
                doDrawDefault();
                return;
            }
            if (bitmap == null) {
                doDrawDefault();
                return;
            }
            try {
                bitmap.eraseColor(Color.TRANSPARENT);
                appWidgetDrawable.draw(bitmapCanvas);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
                if (bitmap.getWidth() > displayWidth || bitmap.getHeight() > displayHeight) {
                    if (resizeBitmap != null) {
                        resizeBitmap.recycle();
                        resizeBitmap = null;
                    }
                    resizeBitmap = ResourceUtil.createScaledOrRotatedBitmap(bitmap, 0, displayWidth, displayHeight);
                    remoteViews.setImageViewBitmap(R.id.imageView, resizeBitmap);
                } else {
                    remoteViews.setImageViewBitmap(R.id.imageView, bitmap);
                }
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        private void update() {
            ConfigFileData configFileData = configDataBase.loadConfigFileDataByWidgetId(appWidgetId);
            if (configFileData == null) {
                if (appWidgetData != null) {
                    appWidgetData.deleteResource();
                    appWidgetData = null;
                }
                return;
            }
            setWidgetData(WidgetData.createFromConfigFileData(configFileData));
        }

        public void setWidgetData(WidgetData widgetData) {
            if (widgetData == null) {
                return;
            }
            try {
                if (appWidgetData != null) {
                    appWidgetData.deleteResource();
                }
                appWidgetData = widgetData;
                if (appWidgetDrawable != null) {
                    appWidgetDrawable.deleteResource();
                }
                appWidgetDrawable = new WidgetDrawable(widgetData);
                if ((bitmap == null)
                        || (bitmap.getWidth() != (int) widgetData.getWidth())
                        || (bitmap.getHeight() != (int) widgetData.getHeight())) {
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = Bitmap.createBitmap((int) widgetData.getWidth(), (int) widgetData.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmapCanvas.setBitmap(bitmap);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        public void deleteResource() {
            if (appWidgetData != null) {
                appWidgetData.deleteResource();
            }
            if (appWidgetDrawable != null) {
                appWidgetDrawable.deleteResource();
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            if (resizeBitmap != null) {
                resizeBitmap.recycle();
                resizeBitmap = null;
            }
            pendingIntent = null;
        }
    }
}
