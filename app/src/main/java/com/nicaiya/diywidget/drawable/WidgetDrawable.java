package com.nicaiya.diywidget.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.nicaiya.diywidget.model.object.AbsObjectData;
import com.nicaiya.diywidget.model.object.WidgetData;


public class WidgetDrawable extends Drawable {

    private static final boolean DEBUG = false;
    private WidgetData widgetData;
    private int alpha = 255;

    private int drawCount = 0;

    public WidgetDrawable(WidgetData widgetData) {
        this.widgetData = widgetData;
    }

    public void draw(Canvas canvas) {
        int count = widgetData.getDataCount();
        for (int i = 0; i < count; i++) {
            AbsObjectData objectData = widgetData.getDataAt(i);
            objectData.draw(canvas);
        }
        AbsObjectData focusData = widgetData.getFocusData();
        if (focusData != null) {
            focusData.drawFocus(canvas);
            focusData.drawAnchor(canvas);
        }
    }

    private void drawCount(Canvas canvas) {
        drawCount++;
        String str = "No. " + drawCount;
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        matrix.setTranslate((widgetData.getWidth() / 2.0f), (widgetData.getHeight() / 2.0f));
        canvas.setMatrix(matrix);
        paint.setColor(Color.BLACK);
        canvas.drawText(str, 0.0f, 0.0f, paint);
        canvas.drawText(str, 0.0f, 2.0f, paint);
        canvas.drawText(str, 2.0f, 0.0f, paint);
        canvas.drawText(str, 0.0f, 0.0f, paint);
        paint.setColor(-1);
        canvas.drawText(str, 0.0f, 0.0f, paint);
    }

    public int getOpacity() {
        if (alpha >= 255) {
            return -1;
        }
        return -3;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setColorFilter(ColorFilter arg0) {
    }

    public AbsObjectData findHitObjectData(int x, int y) {
        if ((x > 0) && ((float) x < widgetData.getWidth()) && (y > 0) && ((float) y < widgetData.getHeight())) {
            for (int i = (widgetData.getDataCount() - 0x1); i >= 0; i = i - 0x1) {
                AbsObjectData objectData = widgetData.getDataAt(i);
                if (objectData.hitTest(x, y)) {
                    return objectData;
                }
            }
        }
        return null;
    }

    public void deleteResource() {
        if (widgetData != null) {
            widgetData.deleteResource();
        }
    }
}

