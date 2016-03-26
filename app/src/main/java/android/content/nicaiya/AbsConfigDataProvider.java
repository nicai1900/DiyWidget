package android.content.nicaiya;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Rect;

import java.io.OutputStream;

import android.content.Intent;

import java.io.InputStream;

public abstract class AbsConfigDataProvider extends AppWidgetProvider implements ConfigDataHelper.ConfigDataListener {

    private static final boolean DEBUG = false;
    private static final String TAG = AbsConfigDataProvider.class.getSimpleName();

    private ConfigDataHelper helper;

    public abstract void onGetConfigData(Context context, int appwidgetId, OutputStream out);

    public abstract boolean onSetConfigData(Context context, int appwidgetId, InputStream in);

    public void onErrorOverSize(Context context, int appWidgetId) {
    }

    public void onChangeSourceBounds(Context context, int appWidgetId, Rect sourceBounds) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ConfigDataHelper.isProcessableIntent(intent)) {
            if (helper == null) {
                helper = new ConfigDataHelper();
                helper.setListener(this);
            }
            helper.processIntent(context, this, intent);
        }
    }

}

