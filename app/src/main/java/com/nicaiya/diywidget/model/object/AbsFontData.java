package com.nicaiya.diywidget.model.object;

import android.graphics.Color;
import android.util.Log;

import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.font.FontItem;
import com.nicaiya.diywidget.font.FontManager;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableFont;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class AbsFontData extends AbsSizeData implements EditableFont {

    private static final boolean DEBUG = false;
    public static final String TAG = AbsFontData.class.getSimpleName();
    private static final String SUPER_TAG = AbsFontData.class.getSuperclass().getSimpleName();

    private FontItem font = FontManager.SANS_SERIF;
    private int fontColor = Color.BLACK;
    private int stringCaseType = 0;

    public void setFont(FontItem item) {
        if (font != item) {
            font = item;
            paintInvalidate = true;
            boundsInvalidate = true;
            anchorOffsetInvalidate = true;
            matrixInvalidate = true;
        }
    }

    public FontItem getFont() {
        return font;
    }

    public void setStringCaseType(int type) {
        if (stringCaseType != type) {
            stringCaseType = type;
            paintInvalidate = true;
            boundsInvalidate = true;
            anchorOffsetInvalidate = true;
            matrixInvalidate = true;
        }
    }

    public int getStringCaseType() {
        return stringCaseType;
    }

    public void setFontColor(int color) {
        if (fontColor != color) {
            fontColor = color;
            paintInvalidate = true;
        }
    }

    public int getFontColor() {
        return fontColor;
    }

    public void deleteResource() {
        super.deleteResource();
        font = null;
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer serializer = data.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_FONT, font.name);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_CASE_TYPE, String.valueOf(stringCaseType));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_COLOR, String.valueOf(fontColor));
        super.putToXmlSerializer(data);
        serializer.endTag(ConfigFileData.XML_NAMESPACE, TAG);
    }

    public void updateFromXmlPullParser(ConfigFileData data) {
        XmlPullParser parser = data.getXmlParser();
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (TAG.equals(tag)) {
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String attrName = parser.getAttributeName(i);
                                String attrValue = parser.getAttributeValue(i);
                                if (attrName.equals(XMLConst.ATTRIBUTE_FONT)) {
                                    FontManager manager = DiyWidgetApplication.getInstance().getFontManager();
                                    FontItem fontItem = manager.getFontItem(attrValue);
                                    if (fontItem == null) {
                                        fontItem = manager.getDefaultFontItem();
                                    }
                                    setFont(fontItem);
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_CASE_TYPE)) {
                                    setStringCaseType(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_COLOR)) {
                                    setFontColor(Integer.valueOf(attrValue));
                                }
                            }
                        } else if (SUPER_TAG.equals(tag)) {
                            super.updateFromXmlPullParser(data);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
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

    protected String convertStringToCaseType(String str) {
        if (str == null) {
            return null;
        }
        switch (stringCaseType) {
            case STRING_CASE_TYPE_FIRST_UPPER:
                char[] chars = str.toLowerCase().toCharArray();
                chars[0] = Character.toUpperCase(chars[0]);
                return String.valueOf(chars);
            case STRING_CASE_TYPE_LOWER:
                return str.toLowerCase();
            case STRING_CASE_TYPE_UPPER:
                return str.toUpperCase();
            case STRING_CASE_TYPE_NONE:
            default:
                return str;
        }
    }

    @Override
    public String toString() {
        return "AbsFontData{" +
                "font=" + font +
                ", fontColor=" + fontColor +
                ", stringCaseType=" + stringCaseType +
                '}';
    }
}