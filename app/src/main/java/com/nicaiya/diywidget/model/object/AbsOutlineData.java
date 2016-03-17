package com.nicaiya.diywidget.model.object;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableOutline;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class AbsOutlineData extends AbsWidthHeightData implements EditableOutline {

    private static final boolean DEBUG = false;
    private static final String SUPER_TAG = AbsOutlineData.class.getSuperclass().getSimpleName();
    private static final String TAG = AbsOutlineData.class.getSimpleName();

    private int outlineAlpha = 255;
    private int outlineColor = Color.BLACK;
    private float outlineWidth = 0.0F;

    protected Paint outlinePaint = new Paint();
    protected boolean outlinePaintInvalidate = true;

    public void deleteResource() {
        super.deleteResource();
        outlinePaint = null;
    }

    public void setOutLineAlpha(int alpha) {
        if (outlineAlpha != alpha) {
            outlineAlpha = alpha;
            outlinePaintInvalidate = true;
        }
    }

    public int getOutLineAlpha() {
        return outlineAlpha;
    }

    public void setOutLineWidth(float width) {
        if (outlineWidth != width) {
            outlineWidth = width;
            outlinePaintInvalidate = true;
        }
    }

    public float getOutLineWidth() {
        return outlineWidth;
    }

    public void setOutlineColor(int color) {
        if (outlineColor != color) {
            outlineColor = color;
            outlinePaintInvalidate = true;
        }
    }

    public int getOutlineColor() {
        return outlineColor;
    }

    public Paint getOutlinePaint() {
        if (outlinePaintInvalidate) {
            initOutlinePaint();
        }
        return outlinePaint;
    }

    protected void initOutlinePaint() {
        if (outlinePaintInvalidate) {
            outlinePaintInvalidate = false;
            outlinePaint.reset();
            outlinePaint.setStyle(Paint.Style.STROKE);
            outlinePaint.setColor(outlineColor);
            outlinePaint.setStrokeWidth(outlineWidth - 1.0F);
            outlinePaint.setAntiAlias(true);
            outlinePaint.setAlpha(outlineAlpha);
        }
    }

    public void setDisableOutline() {
        outlineWidth = 0.0F;
    }

    public boolean isEnableOutline() {
        return outlineWidth >= 1.0F;
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer serializer = data.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_COLOR, String.valueOf(outlineColor));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_WIDTH, String.valueOf(outlineWidth));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ALPHA, String.valueOf(outlineAlpha));
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
                                if (XMLConst.ATTRIBUTE_WIDTH.equals(attrName)) {
                                    setOutLineWidth(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_ALPHA.equals(attrName)) {
                                    setOutLineAlpha(Integer.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_COLOR.equals(attrName)) {
                                    setOutlineColor(Integer.valueOf(attrValue));
                                }
                            }
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
        return "AbsOutlineData{" +
                "outlineAlpha=" + outlineAlpha +
                ", outlineColor=" + outlineColor +
                ", outlineWidth=" + outlineWidth +
                ", outlinePaint=" + outlinePaint +
                ", outlinePaintInvalidate=" + outlinePaintInvalidate +
                '}';
    }
}