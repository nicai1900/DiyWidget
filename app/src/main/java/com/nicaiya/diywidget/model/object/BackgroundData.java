package com.nicaiya.diywidget.model.object;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;


import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableFillStyle;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class BackgroundData extends AbsRadiusData implements EditableFillStyle {

    private static final boolean DEBUG = false;
    private static final String SUPER_TAG = BackgroundData.class.getSuperclass().getSimpleName();
    public static final String TAG = BackgroundData.class.getSimpleName();

    public static final float MAX_HEIGHT = 640.0F;
    public static final float MAX_WIDTH = 720.0F;

    public static final int SCALE_DEFAULT = 0;
    public static final int SCALE_FIT_XY = 1;
    public static final int SCALE_FIT_START = 2;
    public static final int SCALE_FIT_CENTER = 3;
    public static final int SCALE_FIT_END = 4;
    public static final int SCALE_CENTER = 5;
    public static final int SCALE_CENTER_CROP = 6;
    public static final int SCALE_CENTER_INSIDE = 7;
    public static final int SCALE_MATRIX = 8;

    private Bitmap bitmap = null;
    private int color = Color.WHITE;
    private int scale = SCALE_FIT_CENTER;

    public BackgroundData() {
        setName(ResourceUtil.getString(R.string.background));
        setAlpha(127);
    }

    public float getMaxHeight() {
        return MAX_HEIGHT;
    }

    public float getMaxWidth() {
        return MAX_WIDTH;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmap != bitmap) {
            this.bitmap = bitmap;
            paintInvalidate = true;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
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

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }

    public void setLeft(float left) {
    }

    public void setTop(float top) {
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
            paint.setAntiAlias(true);
            paint.setAlpha(getAlpha());
        }
    }

    protected void initBounds() {
        super.initBounds();
        if (boundsInvalidate) {
            boundsInvalidate = false;
            int left = (int) getLeft();
            int top = (int) getTop();
            int right = (int) (left + getWidth());
            int bottom = (int) (top + getHeight());
            bounds.set(left, top, right, bottom);
        }
    }

    protected void initMatrix() {
        super.initMatrix();
        if (matrixInvalidate) {
            matrixInvalidate = false;
            matrix.reset();
            matrix.preTranslate(getLeft(), getTop());
            matrix.preRotate(getRotate());
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        float left = 0;
        float top = 0;
        float width = getWidth();
        float height = getHeight();
        float rx = Math.min(width, height) * getRadius() / 200.0F;
        canvas.setMatrix(getMatrix());
        canvas.drawRoundRect(new RectF(left, top, width, height), rx, rx, getPaint());
        if (isEnableOutline()) {
            canvas.drawRoundRect(new RectF(left, top, width, height), rx, rx, getOutlinePaint());
        }
    }

    public boolean hitTest(int x, int y) {
        return (x > 0) && (x < getWidth()) && (y > 0) && (y < getHeight());
    }

    public void deleteResource() {
        super.deleteResource();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer localXmlSerializer = data.getXmlSerializer();
        localXmlSerializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        localXmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_COLOR, String.valueOf(color));
        localXmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_SCALE, String.valueOf(scale));
        if (bitmap != null) {
            XMLBitmapUtil.putToXmlSerializer(data, bitmap);
        }
        super.putToXmlSerializer(data);
        localXmlSerializer.endTag(ConfigFileData.XML_NAMESPACE, TAG);
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
                                } else if (XMLConst.ATTRIBUTE_SCALE.equals(attrName)) {
                                    setScale(Integer.valueOf(attrValue));
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

    @Override
    public String toString() {
        return "BackgroundData{" +
                "bitmap=" + bitmap +
                ", color=" + color +
                ", scale=" + scale +
                '}';
    }
}