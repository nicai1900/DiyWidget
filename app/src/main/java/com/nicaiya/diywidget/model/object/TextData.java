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

    public float getMaxSize() {
        return MAX_SIZE;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            this.boundsInvalidate = true;
            this.anchorOffsetInvalidate = true;
            this.matrixInvalidate = true;
        }
    }

    public Paint.Align getAlign() {
        return align;
    }

    public void setAlign(Paint.Align paramAlign) {
        if (this.align != paramAlign) {
            this.align = paramAlign;
            this.paintInvalidate = true;
            this.boundsInvalidate = true;
        }
    }

    public String getName() {
        return super.getName();
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

    protected void initBounds() {
        super.initBounds();
        if (boundsInvalidate) {
            boundsInvalidate = false;
            String drawString = convertStringToCaseType(text);
            getPaint().getTextBounds(drawString, 0, drawString.length(), bounds);
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.setMatrix(getMatrix());
        String drawString = convertStringToCaseType(text);
        canvas.drawText(drawString, 0.0F, 0.0F, getPaint());
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
        this.align = null;
        this.text = null;
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
                            setText(parser.nextText());
                        } else if (SUPER_TAG.equals(parser.getName())) {
                            super.updateFromXmlPullParser(data);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(tag)) {
                            return;
                        }
                    case XmlPullParser.TEXT:
                        setText(parser.getText());
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
        return "TextData{" +
                "align=" + align +
                ", text='" + text + '\'' +
                '}';
    }
}