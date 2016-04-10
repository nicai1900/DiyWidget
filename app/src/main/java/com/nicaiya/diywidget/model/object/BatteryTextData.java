

package com.nicaiya.diywidget.model.object;

import android.graphics.Canvas;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableHeaderFooter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class BatteryTextData extends TextData implements EditableHeaderFooter {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = BatteryTextData.class.getSimpleName();
    private static final String SUPER_TAG = BatteryTextData.class.getSuperclass().getSimpleName();
    private String header = "";
    private String footer = "";

    public BatteryTextData() {
        setName(ResourceUtil.getString(R.string.battery_text));
    }

    public void setHeader(String header) {
        if (!this.header.equals(header)) {
            this.header = header;
        }
    }

    public String getHeader() {
        return header;
    }

    public void setFooter(String footer) {
        if (!this.footer.equals(footer)) {
            this.footer = footer;
        }
    }

    public String getFooter() {
        return footer;
    }

    public void draw(Canvas canvas) {
        int batteryLevel = DiyWidgetApplication.getInstance().getBatteryLevelPercent();
        String batteryText = header + String.valueOf(batteryLevel) + footer;
        setText(batteryText);
        super.draw(canvas);
    }

    public void deleteResource() {
        super.deleteResource();
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_TEXT_HEADER, header);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_TEXT_FOOTER, footer);
        setText("");
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
                                if (XMLConst.ATTRIBUTE_TEXT_HEADER.equals(attrName)) {
                                    setHeader(String.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_TEXT_FOOTER.equals(attrName)) {
                                    setFooter(String.valueOf(attrValue));
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
}
