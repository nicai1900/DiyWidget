package com.nicaiya.diywidget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.util.Locale;

public class ResourceUtil {
    private static final boolean DEBUG = false;
    private static final String TAG = ResourceUtil.class.getSimpleName();

    private static Context context;

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static Bitmap getBitmap(int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static int getColor(int resId) {
        return context.getResources().getColor(resId);
    }


    public static Locale getCurrentLocale() {
        return context.getResources().getConfiguration().locale;
    }

    public static float getDimension(int resId) {
        return context.getResources().getDimension(resId);
    }


    public static String getString(int resId) {
        return context.getString(resId);
    }

    public static String[] getStringArray(int resId) {
        return context.getResources().getStringArray(resId);
    }

    public static int getVersion() {
        PackageManager manager = context.getPackageManager();
        try {
            return manager.getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return 0;
    }

    public static Bitmap createScaledOrRotatedBitmap(Bitmap input, int orientation, int maxWidth, int maxHeight) {
        int orgWidth = input.getWidth();
        int orgHeight = input.getHeight();
        float scaleWidth = (float) ((double) maxWidth / (double) orgWidth);
        float scaleHeight = (float) ((double) maxHeight / (double) orgHeight);
        float scale = Math.min(scaleWidth, scaleHeight);
        if (scale > 1.0f) {
            scale = 1.0f;
        }
        try {
            Matrix m = new Matrix();
            m.postScale(scale, scale);
            if (orientation != 0) {
                m.postRotate((float) orientation);
            }
            return Bitmap.createBitmap(input, 0, 0, orgWidth, orgHeight, m, true);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, e);
            return null;
        } catch (OutOfMemoryError e) {
            Log.w(TAG, e);
        }
        return null;
    }

}