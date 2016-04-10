package android.content.nicaiya;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;

import java.io.InputStream;
import java.io.OutputStream;


public abstract class AbsConfigDataProvider extends AppWidgetProvider implements ConfigDataHelper.ConfigDataListener {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = AbsConfigDataProvider.class.getSimpleName();

    private ConfigDataHelper helper;

    public abstract void onGetConfigData(Context context, int appwidgetId, OutputStream out);

    public abstract boolean onSetConfigData(Context context, int appwidgetId, InputStream in);

    public void onErrorOverSize(Context context, int appWidgetId) {
        Log.e(TAG, "onErrorOverSize appWidgetId: " + appWidgetId);
    }

    public void onChangeSourceBounds(Context context, int appWidgetId, Rect sourceBounds) {
        Log.d(TAG, "onChangeSourceBounds appWidgetId: " + appWidgetId + " bounds: " + sourceBounds);
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

