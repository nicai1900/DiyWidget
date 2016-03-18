package com.nicaiya.diywidget.model.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class ImageData extends AbsOutlineData {

    private static final boolean DEBUG = false;
    private static final String SUPER_TAG = ImageData.class.getSuperclass().getSimpleName();
    public static final String TAG = ImageData.class.getSimpleName();

    public static final float MAX_HEIGHT = 640.0F;
    public static final float MAX_WIDTH = 720.0F;

    public boolean isUseDefaultImage = false;
    private int reverseLR = 1;
    private int reverseUD = 1;

    private Bitmap bitmap;
    private Canvas canvas;
    private Rect dstRect = new Rect(0, 0, 0, 0);
    private Rect srcRect = new Rect();

    public ImageData() {
        setName(ResourceUtil.getString(R.string.image));
    }

    public void setReverseLR(int reverseLR) {
        this.reverseLR = reverseLR;
        this.matrixInvalidate = true;
    }

    public void setReverseUD(int reverseUD) {
        this.reverseUD = reverseUD;
        this.matrixInvalidate = true;
    }

    public int getReverseLR() {
        return this.reverseLR;
    }

    public int getReverseUD() {
        return this.reverseUD;
    }

    public float getMaxHeight() {
        return MAX_HEIGHT;
    }

    public float getMaxWidth() {
        return MAX_WIDTH;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmap != bitmap) {
            this.bitmap = bitmap;
            if (this.bitmap != null) {
                this.srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            }
        }
    }

    protected void initPaint() {
        super.initPaint();
        if (this.paintInvalidate) {
            this.paintInvalidate = false;
            this.paint.reset();
            this.paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
            this.paint.setAlpha(getAlpha());
            this.paint.setShadowLayer(getShadowRadius(), getShadowDx(), getShadowDy(), getShadowColor());
        }
    }

    protected void initBounds() {
        super.initBounds();
        if (this.boundsInvalidate) {
            this.boundsInvalidate = false;
            this.bounds.set(0, 0, (int) getWidth(), (int) getHeight());
        }
    }

    protected void initMatrix() {
        super.initMatrix();
        if (this.matrixInvalidate) {
            this.matrixInvalidate = false;
            this.matrix.reset();
            if (this.canvas != null) {
                this.matrix.setScale(getReverseLR(), getReverseUD(), canvas.getWidth() / 2, canvas.getHeight() / 2);
            }
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.canvas = canvas;
        this.dstRect.right = ((int) getWidth());
        this.dstRect.bottom = ((int) getHeight());
        canvas.setMatrix(getMatrix());
        if ((this.bitmap != null) && (!this.bitmap.isRecycled())) {
            canvas.drawBitmap(this.bitmap, this.srcRect, this.dstRect, getPaint());
        }
        if (isEnableOutline()) {
            canvas.drawRect(this.dstRect, getOutlinePaint());
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
        srcRect = null;
        dstRect = null;
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer localXmlSerializer = data.getXmlSerializer();
        localXmlSerializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        localXmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_REVERSE_LR, String.valueOf(this.reverseLR));
        localXmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_REVERSE_UD, String.valueOf(this.reverseUD));
        if (this.bitmap != null) {
            XMLBitmapUtil.putToXmlSerializer(data, this.bitmap);
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
                                if (XMLConst.ATTRIBUTE_REVERSE_LR.equals(attrName)) {
                                    setReverseLR(Integer.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_REVERSE_UD.equals(attrName)) {
                                    setReverseUD(Integer.valueOf(attrValue));
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
        return "ImageData{" +
                "isUseDefaultImage=" + isUseDefaultImage +
                ", reverseLR=" + reverseLR +
                ", reverseUD=" + reverseUD +
                ", bitmap=" + bitmap +
                ", canvas=" + canvas +
                ", dstRect=" + dstRect +
                ", srcRect=" + srcRect +
                '}';
    }
}