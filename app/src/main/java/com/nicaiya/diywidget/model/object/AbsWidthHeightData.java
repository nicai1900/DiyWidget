package com.nicaiya.diywidget.model.object;


import android.util.Log;

import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableWidthHeight;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class AbsWidthHeightData extends AbsShadowData implements EditableWidthHeight {
    private static final boolean DEBUG = false;

    private static final String SUPER_TAG = AbsWidthHeightData.class.getSuperclass().getSimpleName();
    private static final String TAG = AbsWidthHeightData.class.getSimpleName();

    private float height = 72.0F;
    private float width = 72.0F;

    public float getHeight() {
        return height;
    }

    public float getMaxHeight() {
        return 640.0F;
    }

    public float getMaxWidth() {
        return 720.0F;
    }

    public float getMinHeight() {
        return 1.0F;
    }

    public float getMinWidth() {
        return 1.0F;
    }

    public float getWidth() {
        return width;
    }

    public void setHeight(float height) {
        if (this.height != height) {
            this.height = height;
            boundsInvalidate = true;
            anchorOffsetInvalidate = true;
            matrixInvalidate = true;
        }
    }

    public void setWidth(float width) {
        if (this.width != width) {
            this.width = width;
            boundsInvalidate = true;
            anchorOffsetInvalidate = true;
            matrixInvalidate = true;
        }
    }

    public void deleteResource() {
        super.deleteResource();
    }

    public void putToXmlSerializer(ConfigFileData data)
            throws Exception {
        XmlSerializer serializer = data.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_WIDTH, String.valueOf(width));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_HEIGHT, String.valueOf(height));
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
                                    setWidth(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_HEIGHT.equals(attrName)) {
                                    setHeight(Float.valueOf(attrValue));
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
        return "AbsWidthHeightData{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }
}