package com.nicaiya.diywidget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.nicaiya.diywidget.model.object.ImageData;

import java.util.Locale;

public class ResourceUtil {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = ResourceUtil.class.getSimpleName();

    private static final int ICON_SIZE = 256;
    private static DiyWidgetConfigActivity configActivity;
    private static Context context;

    private static Activity currentActivity;

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static void setConfigActivity(DiyWidgetConfigActivity configActivity) {
        ResourceUtil.configActivity = configActivity;
    }

    public static DiyWidgetConfigActivity getConfigActivity() {
        return configActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        ResourceUtil.currentActivity = currentActivity;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static String getString(int resId) {
        return context.getString(resId);
    }

    public static String[] getStringArray(int resId) {
        return context.getResources().getStringArray(resId);
    }

    public static int getColor(int resId) {
        return context.getResources().getColor(resId);
    }

    public static float getDimension(int resId) {
        return context.getResources().getDimension(resId);
    }

    public static Bitmap getBitmap(int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Bitmap getNoScaledBitmap(int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, opts);
    }

    public static Bitmap decodeSampledBitmapFromResource(View view, int resId, int sampleSize) {
        Rect outRect = new Rect();
        view.getDrawingRect(outRect);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (sampleSize > 1) {
            options.inSampleSize = sampleSize;
        } else {
            options.inSampleSize = calculateInSampleSize(options, outRect.width(), outRect.height());
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static int getBitmapInSampleSize(int bitmapWidth, int bitmapHeight, int maxWidth, int maxHeight) {
        if ((maxWidth != 0) && (maxHeight != 0)) {
            int widthScale = bitmapWidth / maxWidth;
            int heightScale = bitmapHeight / maxHeight;
            int sampleSize = Math.min(widthScale, heightScale);
            if (sampleSize > 0x1) {
                int testSampleSize = 0x1;
                while (sampleSize > testSampleSize) {
                    testSampleSize = testSampleSize << 0x1;
                }
                return (testSampleSize >> 0x1);
            }
        }
        return -0x2;
    }

    public static Bitmap drawableToIcon(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public static void changeAndresizeImageData(Bitmap bitmap, ImageData curData) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthMax = curData.getWidth();
        float heightMax = curData.getHeight();
        float ration = Math.min((widthMax / (float) width), (heightMax / (float) height));
        curData.setWidth(((float) width * ration));
        curData.setHeight(((float) height * ration));
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

    public static int getVersion() {
        PackageManager manager = context.getPackageManager();
        try {
            return manager.getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return 0;
    }

    public static Locale getCurrentLocale() {
        return context.getResources().getConfiguration().locale;
    }

}