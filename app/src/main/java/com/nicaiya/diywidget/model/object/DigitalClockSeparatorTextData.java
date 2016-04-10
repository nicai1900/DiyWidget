package com.nicaiya.diywidget.model.object;

import android.graphics.Canvas;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class DigitalClockSeparatorTextData extends TextData {

    private static final String SUPER_TAG = DigitalClockSeparatorTextData.class.getSuperclass().getSimpleName();
    public static final String TAG = DigitalClockSeparatorTextData.class.getSimpleName();

    private static final String SEPARATOR = ResourceUtil.getString(R.string.separator);

    public DigitalClockSeparatorTextData() {
        setName(ResourceUtil.getString(R.string.digital_clock_separator_text));
    }

    public void deleteResource() {
        super.deleteResource();
    }

    public void draw(Canvas paramCanvas) {
        setText(SEPARATOR);
        super.draw(paramCanvas);
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer localXmlSerializer = configFileData.getXmlSerializer();
        localXmlSerializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        setText("");
        super.putToXmlSerializer(configFileData);
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
                        if (SUPER_TAG.equals(parser.getName())) {
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