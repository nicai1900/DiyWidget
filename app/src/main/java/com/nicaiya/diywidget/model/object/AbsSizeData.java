package com.nicaiya.diywidget.model.object;

import android.util.Log;

import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableSize;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class AbsSizeData extends AbsShadowData implements EditableSize {
    private static final boolean DEBUG = false;
    private static final String SUPER_TAG = AbsSizeData.class.getSuperclass().getSimpleName();
    private static final String TAG = AbsSizeData.class.getSimpleName();

    private float size = 50.0F;

    public float getMaxSize() {
        return 1.0F;
    }

    public float getMinSize() {
        return 1.0F;
    }

    public void setSize(float size) {
        if (this.size != size) {
            this.size = size;
            paintInvalidate = true;
            boundsInvalidate = true;
            anchorOffsetInvalidate = true;
            matrixInvalidate = true;
        }
    }

    public float getSize() {
        return this.size;
    }

    public void deleteResource() {
        super.deleteResource();
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer serializer = data.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_SIZE, String.valueOf(getSize()));
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
                                if (XMLConst.ATTRIBUTE_SIZE.equals(attrName)) {
                                    setSize(Float.valueOf(attrValue));
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
        return "AbsSizeData{" +
                "size=" + size +
                '}';
    }
}