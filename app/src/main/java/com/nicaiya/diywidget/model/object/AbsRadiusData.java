package com.nicaiya.diywidget.model.object;

import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableRadius;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class AbsRadiusData extends AbsOutlineData implements EditableRadius {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String SUPER_TAG = AbsRadiusData.class.getSuperclass().getSimpleName();
    private static final String TAG = AbsRadiusData.class.getSimpleName();

    private int radius = 0;

    public void setRadius(int radius) {
        if (this.radius != radius) {
            this.radius = radius;
        }
    }

    public int getRadius() {
        return this.radius;
    }

    public void deleteResource() {
        super.deleteResource();
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer serializer = data.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_RADIUS, String.valueOf(radius));
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
                                if (XMLConst.ATTRIBUTE_RADIUS.equals(attrName)) {
                                    setRadius(Integer.valueOf(attrValue));
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
        return "AbsRadiusData{" +
                "radius=" + radius +
                '}';
    }
}