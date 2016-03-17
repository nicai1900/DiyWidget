package com.nicaiya.diywidget.model.object;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class TextData extends AbsFontData {

    private static final boolean DEBUG = false;
    public static final String TAG = TextData.class.getSimpleName();
    private static final String SUPER_TAG = TextData.class.getSuperclass().getSimpleName();

    public static final String DEFAULT_TEXT = "";
    public static final float MAX_SIZE = 320.0F;

    private Paint.Align align = Paint.Align.LEFT;
    private String text = DEFAULT_TEXT;

    public TextData() {
        setName(ResourceUtil.getString(R.string.text));
    }

    public void deleteResource() {
        super.deleteResource();
        this.align = null;
        this.text = null;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.setMatrix(getMatrix());
        canvas.drawText(convertStringToCaseType(text), 0.0F, 0.0F, getPaint());
    }

    public Paint.Align getAlign() {
        return align;
    }

    public float getMaxSize() {
        return MAX_SIZE;
    }

    public String getName() {
        return super.getName();
    }

    public String getText() {
        return text;
    }

    public boolean hitTest(int x, int y) {
        float[] touchPoint = new float[2];
        touchPoint[0] = x;
        touchPoint[1] = y;
        Matrix matrix = new Matrix();
        getMatrix().invert(matrix);
        matrix.mapPoints(touchPoint);
        return getBounds().contains((int) touchPoint[0], (int) touchPoint[1]);
    }

    protected void initBounds() {
        super.initBounds();
        if (boundsInvalidate) {
            boundsInvalidate = false;
            String str = convertStringToCaseType(text);
            getPaint().getTextBounds(str, 0, str.length(), bounds);
        }
    }

    protected void initMatrix() {
        super.initMatrix();
        if (this.matrixInvalidate) {
            this.matrixInvalidate = false;
            this.matrix.reset();
            PointF anchorOffset = getAnchorOffset();
            this.matrix.preTranslate(getLeft(), getTop());
            this.matrix.preRotate(getRotate());
            this.matrix.preTranslate(-anchorOffset.x, -anchorOffset.y);
        }
    }

    protected void initPaint() {
        super.initPaint();
        if (this.paintInvalidate) {
            this.paintInvalidate = false;
            this.paint.reset();
            this.paint.setAntiAlias(true);
            this.paint.setColor(getFontColor());
            this.paint.setAlpha(getAlpha());
            this.paint.setShadowLayer(getShadowRadius(), getShadowDx(), getShadowDy(), getShadowColor());
            this.paint.setTypeface(getFont().typeface);
            this.paint.setTextSize(getSize());
            this.paint.setTextAlign(align);
        }
    }

    public void setAlign(Paint.Align paramAlign) {
        if (this.align != paramAlign) {
            this.align = paramAlign;
            this.paintInvalidate = true;
            this.boundsInvalidate = true;
        }
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            this.boundsInvalidate = true;
            this.anchorOffsetInvalidate = true;
            this.matrixInvalidate = true;
        }
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer serializer = data.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ALIGN, align.name());
        serializer.text(text);
        super.putToXmlSerializer(data);
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
                                if (XMLConst.ATTRIBUTE_ALIGN.equals(attrName)) {
                                    setAlign(Paint.Align.valueOf(attrValue));
                                }
                            }
                            eventType = parser.next();
                            if (eventType == XmlPullParser.TEXT) {
                                setText(parser.getText());
                            }
                        } else if (SUPER_TAG.equals(parser.getName())) {
                            super.updateFromXmlPullParser(data);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(tag)) {
                            return;
                        }
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
        return "TextData{" +
                "align=" + align +
                ", text='" + text + '\'' +
                '}';
    }
}