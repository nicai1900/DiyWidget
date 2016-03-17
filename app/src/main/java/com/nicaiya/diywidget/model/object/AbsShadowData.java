package com.nicaiya.diywidget.model.object;


import android.graphics.Color;
import android.util.Log;

import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableShadow;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;


public class AbsShadowData extends AbsObjectData implements EditableShadow {

    private static final boolean DEBUG = false;
    private static final String SUPER_TAG = AbsShadowData.class.getSuperclass().getSimpleName();
    private static final String TAG = AbsShadowData.class.getSimpleName();

    private static final float SHADOW_MAX_DX = 50.0F;
    private static final float SHADOW_MAX_DY = 50.0F;
    private static final float SHADOW_MAX_RADIOUS = 50.0F;

    private int shadowColor = Color.BLACK;
    private float shadowDx;
    private float shadowDy;
    private float shadowRadius;

    public void deleteResource() {
        super.deleteResource();
    }

    public float getShadowMaxDx() {
        return SHADOW_MAX_DX;
    }

    public float getShadowMaxDy() {
        return SHADOW_MAX_DY;
    }

    public float getShadowMaxRadius() {
        return SHADOW_MAX_RADIOUS;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int color) {
        if (shadowColor != color) {
            shadowColor = color;
            paintInvalidate = true;
        }
    }

    public float getShadowDx() {
        return shadowDx;
    }

    public void setShadowDx(float dx) {
        if (shadowDx != dx) {
            shadowDx = dx;
            paintInvalidate = true;
        }
    }

    public float getShadowDy() {
        return shadowDy;
    }

    public void setShadowDy(float dy) {
        if (shadowDy != dy) {
            shadowDy = dy;
            paintInvalidate = true;
        }
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float radius) {
        if (shadowRadius != radius) {
            shadowRadius = radius;
            paintInvalidate = true;
        }
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_RADIUS, String.valueOf(shadowRadius));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_DX, String.valueOf(shadowDx));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_DY, String.valueOf(shadowDy));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_COLOR, String.valueOf(shadowColor));
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
                                if (XMLConst.ATTRIBUTE_RADIUS.equals(attrName)) {
                                    setShadowRadius(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_DX.equals(attrName)) {
                                    setShadowDx(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_DY.equals(attrName)) {
                                    setShadowDy(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_COLOR.equals(attrName)) {
                                    setShadowColor(Integer.valueOf(attrValue));
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
        return "AbsShadowData{" +
                "shadowColor=" + shadowColor +
                ", shadowDx=" + shadowDx +
                ", shadowDy=" + shadowDy +
                ", shadowRadius=" + shadowRadius +
                '}';
    }
}