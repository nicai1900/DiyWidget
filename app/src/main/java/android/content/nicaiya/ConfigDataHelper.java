package android.content.nicaiya;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConfigDataHelper {

    protected static final boolean DEBUG = false;
    private static final String TAG = ConfigDataHelper.class.getSimpleName();

    public static final String ACTION_CHANGE_SOURCE_BOUNDS = "com.nicaiya.diywidget.appwidget.CHANGE_SOURCE_BOUNDS";
    public static final String ACTION_GET_CONFIG_DATA = "com.nicaiya.diywidget.appwidget.GET_CONFIG_DATA";
    public static final String ACTION_GET_VERSION = "com.nicaiya.diywidget.appwidget.GET_VERSION";
    public static final String ACTION_SET_CONFIG_DATA = "com.nicaiya.diywidget.appwidget.SET_CONFIG_DATA";

    public static final int CONFIG_MAX_SIZE = 0x500000;

    public static final int INVALID_APP_ID = 0;

    public static final String EXTRA_APP_ID = "appWidgetId";
    public static final String EXTRA_VERSION = "EXTRA_VERSION";
    public static final int RESULT_CONFIG_COMPLETE = 0x64;
    public static final int RESULT_CONFIG_NEEDED = 0xc8;
    public static final int RESULT_ERROR_OVER_SIZE = 0x1f4;
    public static final int RESULT_FAIL = 0x190;
    public static final int RESULT_SUCCESS = 0x12c;
    private static final int VERSION = 0x1040;
    private ConfigDataHelper.ConfigDataListener listener;

    public ConfigDataHelper.ConfigDataListener getListener() {
        return listener;
    }

    public void setListener(ConfigDataHelper.ConfigDataListener listener) {
        this.listener = listener;
    }

    public static boolean isProcessableIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (ACTION_GET_VERSION.equals(action)
                    || ACTION_GET_CONFIG_DATA.equals(action)
                    || ACTION_SET_CONFIG_DATA.equals(action)
                    || ACTION_CHANGE_SOURCE_BOUNDS.equals(action)) {
                return true;
            }
        }
        return false;
    }

    public void processIntent(Context context, BroadcastReceiver receiver, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (ACTION_GET_VERSION.equals(action)) {
                onReceiveGetVersion(context, receiver, intent);
                return;
            }
            if (ACTION_GET_CONFIG_DATA.equals(action)) {
                onReceiveGetConfigData(context, receiver, intent);
                return;
            }
            if (ACTION_SET_CONFIG_DATA.equals(action)) {
                onReceiveSetConfigData(context, receiver, intent);
                return;
            }
            if (ACTION_CHANGE_SOURCE_BOUNDS.equals(action)) {
                onReceiveChangeSourceBounds(context, receiver, intent);
            }
        }
    }

    private void pendingResultFinishWithExtras(BroadcastReceiver.PendingResult pendingResult, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putInt(EXTRA_VERSION, VERSION);
        pendingResult.setResultExtras(extras);
        pendingResult.finish();
    }

    private void onReceiveGetVersion(Context context, BroadcastReceiver receiver, Intent intent) {
        final BroadcastReceiver.PendingResult pendingResult = receiver.goAsync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle extras = new Bundle();
                pendingResultFinishWithExtras(pendingResult, extras);
            }
        }).start();
    }

    private void onReceiveGetConfigData(final Context context, BroadcastReceiver receiver, Intent intent) {
        if (listener == null) {
            return;
        }
        final int appWidgetId = intent.getIntExtra(EXTRA_APP_ID, INVALID_APP_ID);
        if (appWidgetId != INVALID_APP_ID) {
            Uri configFileUri = intent.getData();
            if (configFileUri != null) {
                final String filePath = configFileUri.getPath();
                if (filePath != null) {
                    final BroadcastReceiver.PendingResult pendingResult = receiver.goAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bundle extras = new Bundle();
                            extras.putInt(EXTRA_APP_ID, appWidgetId);
                            pendingResultFinishWithExtras(pendingResult, extras);
                            try {
                                WriteSizeCountBufferedOutputStream bos = new WriteSizeCountBufferedOutputStream(new FileOutputStream(new File(filePath)));
                                listener.onGetConfigData(context, appWidgetId, bos);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }
                    }).start();
                }
            }
        }
    }

    private void onReceiveSetConfigData(final Context context, BroadcastReceiver receiver, Intent intent) {
        if (listener == null) {
            return;
        }
        final int appWidgetId = intent.getIntExtra(EXTRA_APP_ID, INVALID_APP_ID);
        if (appWidgetId != INVALID_APP_ID) {
            Uri configFileUri = intent.getData();
            if (configFileUri != null) {
                final String filePath = configFileUri.getPath();
                if (filePath != null) {
                    final BroadcastReceiver.PendingResult pendingResult = receiver.goAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bundle extras = new Bundle();
                            extras.putInt(EXTRA_APP_ID, appWidgetId);
                            pendingResultFinishWithExtras(pendingResult, extras);
                            try {
                                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
                                listener.onSetConfigData(context, appWidgetId, bis);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }
                    }).start();
                }
            }
        }
    }

    private void onReceiveChangeSourceBounds(final Context context, BroadcastReceiver receiver, Intent intent) {
        if (listener == null) {
            return;
        }
        final int appWidgetId = intent.getIntExtra(EXTRA_APP_ID, INVALID_APP_ID);
        if (appWidgetId != INVALID_APP_ID) {
            final Rect boundary = intent.getSourceBounds();
            if (boundary != null) {
                final BroadcastReceiver.PendingResult pendingResult = receiver.goAsync();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle extras = new Bundle();
                        extras.putInt(EXTRA_APP_ID, appWidgetId);
                        pendingResultFinishWithExtras(pendingResult, extras);

                        try {
                            listener.onChangeSourceBounds(context, appWidgetId, boundary);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }).start();

            }
        }
    }

    public interface ConfigDataListener {

        void onChangeSourceBounds(Context context, int appWidgetId, Rect boundary);

        void onErrorOverSize(Context context, int appWidgetId);

        void onGetConfigData(Context context, int appWidgetId, OutputStream out);

        boolean onSetConfigData(Context context, int appWidgetId, InputStream in);

    }

    static class WriteSizeCountBufferedOutputStream extends BufferedOutputStream {

        private int writeSize = 0;

        public WriteSizeCountBufferedOutputStream(OutputStream out) {
            super(out);
        }

        public int getWriteSize() {
            return writeSize;
        }

        public void write(int a) throws IOException {
            try {
                super.write(a);
                writeSize += 1;
            } catch (IOException e) {
                throw e;
            }
        }

        public void write(byte[] buffer, int offset, int length) throws IOException {
            try {
                super.write(buffer, offset, length);
                writeSize += length;
            } catch (IOException e) {
                throw e;
            }
        }
    }
}
