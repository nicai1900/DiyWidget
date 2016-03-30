/**
 * Generated by smali2java 1.0.0.558
 * Copyright (C) 2013 Hensence.com
 */

package com.nicaiya.diywidget.model.object;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableFillStyle;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class ShapeRectData extends AbsRadiusData implements EditableFillStyle {

    private static final boolean DEBUG = false;
    public static final String TAG = ShapeRectData.class.getSimpleName();
    private static final String SUPER_TAG = ShapeRectData.class.getSuperclass().getSimpleName();

    public static final float MAX_WIDTH = 720.0f;
    public static final float MAX_HEIGHT = 640.0f;
    private int color = Color.WHITE;
    private Bitmap bitmap = null;

    public ShapeRectData() {
        setOutLineWidth(4.0f);
        setName(ResourceUtil.getString(R.string.shape_rect));
    }

    public float getMaxWidth() {
        return MAX_WIDTH;
    }

    public float getMaxHeight() {
        return MAX_HEIGHT;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        if (this.color != color) {
            this.color = color;
            paintInvalidate = true;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmap != bitmap) {
            this.bitmap = bitmap;
            paintInvalidate = true;
        }
    }

    protected void initPaint() {
        super.initPaint();
        if (paintInvalidate) {
            paintInvalidate = false;
            paint.reset();
            if (bitmap == null) {
                paint.setColor(color);
            } else {
                paint.setFlags(Paint.FILTER_BITMAP_FLAG);
                paint.setShader(new BitmapShader(bitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR));
            }
            paint.setShadowLayer(getShadowRadius(), getShadowDx(), getShadowDy(), getShadowColor());
            paint.setAntiAlias(true);
            paint.setAlpha(getAlpha());
        }
    }

    protected void initMatrix() {
        super.initMatrix();
        if (matrixInvalidate) {
            matrixInvalidate = false;
            matrix.reset();
            PointF anchorOffset = getAnchorOffset();
            matrix.preTranslate(getLeft(), getTop());
            matrix.preRotate(getRotate());
            matrix.preTranslate(-anchorOffset.x, -anchorOffset.y);
        }
    }

    protected void initBounds() {
        super.initBounds();
        if (boundsInvalidate) {
            boundsInvalidate = false;
            bounds.set(0, 0, (int) getWidth(), (int) getHeight());
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        float l = 0;
        float t = 0;
        float r = getWidth();
        float b = getHeight();
        float rx = (Math.min(r, b) * (float) getRadius()) / 200.0f;
        canvas.setMatrix(getMatrix());
        canvas.drawRoundRect(new RectF(l, t, r, b), rx, rx, getPaint());
        if (isEnableOutline()) {
            canvas.drawRoundRect(new RectF(l, t, r, b), rx, rx, getOutlinePaint());
        }
    }

    public boolean hitTest(int x, int y) {
        float[] pts = {(float) x, (float) y};
        Matrix invertMatrix = new Matrix();
        getMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(pts);
        return getBounds().contains((int) pts[0], (int) pts[1]);
    }

    public void deleteResource() {
        super.deleteResource();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_COLOR, String.valueOf(color));
        if (bitmap != null) {
            XMLBitmapUtil.putToXmlSerializer(configFileData, bitmap);
        }
        super.putToXmlSerializer(configFileData);
        serializer.endTag(ConfigFileData.XML_NAMESPACE, TAG);
    }

    public void updateFromXmlPullParser(ConfigFileData data) {
        XmlPullParser parser = data.getXmlParser();
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (TAG.equals(tag)) {
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String attrName = parser.getAttributeName(i);
                                String attrValue = parser.getAttributeValue(i);
                                if (XMLConst.ATTRIBUTE_COLOR.equals(attrName)) {
                                    setColor(Integer.valueOf(attrValue));
                                }
                            }
                        } else if (XMLBitmapUtil.TAG.equals(tag)) {
                            setBitmap(XMLBitmapUtil.updateFromXmlPullParser(data));
                        } else if (SUPER_TAG.equals(parser.getName())) {
                            super.updateFromXmlPullParser(data);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(tag)) {
                            return;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
