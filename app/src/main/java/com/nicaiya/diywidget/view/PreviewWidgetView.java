package com.nicaiya.diywidget.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nicaiya.diywidget.drawable.WidgetDrawable;
import com.nicaiya.diywidget.model.object.AbsObjectData;
import com.nicaiya.diywidget.model.object.WidgetData;

/**
 * Created by zhengjie on 16/3/26.
 */
public class PreviewWidgetView extends ImageView {

    private Bitmap bitmap;
    private Canvas drawableCanvas;
    private WidgetData widgetData;
    private WidgetDrawable widgetDrawable;

    private boolean isRegisterReceiver = false;
    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            invalidate();
        }
    };

    public PreviewWidgetView(Context context) {
        super(context);
        init();
    }

    public PreviewWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewWidgetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    public void init(WidgetData widgetData) {
        this.widgetData = widgetData;
        if (widgetDrawable != null) {
            widgetDrawable.deleteResource();
        }
        widgetDrawable = new WidgetDrawable(widgetData);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if ((bitmap == null)
                || ((float) bitmap.getWidth() != widgetData.getWidth())
                || ((float) bitmap.getHeight()
                != widgetData.getHeight())) {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            if ((widgetData != null) && (widgetData.getWidth() > 0x0) && (widgetData.getHeight() > 0x0)) {
                bitmap = Bitmap.createBitmap((int) widgetData.getWidth(), (int) widgetData.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            }
            setImageBitmap(bitmap);
            drawableCanvas = new Canvas(bitmap);
        }
        bitmap.eraseColor(0x0);
        if (widgetDrawable != null) {
            widgetDrawable.draw(drawableCanvas);
        }
        super.onDraw(canvas);
    }

    public void convertViewDifferentialToImageDifferential(float[] differential) {
    }

    public void convertViewPointToImagePoint(float[] point) {
        Matrix invertMatrix = new Matrix();
        getImageMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(point);
    }

    public AbsObjectData findHitObjectData(int x, int y) {
        float[] point = {(float) x, (float) y};
        convertViewPointToImagePoint(point);
        int imageX = (int) point[0];
        int imageY = (int) point[1];
        return widgetDrawable.findHitObjectData(imageX, imageY);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isRegisterReceiver) {
            isRegisterReceiver = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(timeReceiver, filter);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isRegisterReceiver) {
            isRegisterReceiver = false;
            getContext().unregisterReceiver(timeReceiver);
        }
    }
}

