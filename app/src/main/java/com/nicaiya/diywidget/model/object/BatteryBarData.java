

package com.nicaiya.diywidget.model.object;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class BatteryBarData extends ShapeRectData {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = BatteryBarData.class.getSimpleName();
    private static final String SUPER_TAG = BatteryBarData.class.getSuperclass().getSimpleName();

    public static final int MAX_GAUGE_BORDER = 50;
    private int gaugeColor = -0x777778;
    private int gaugeAlpha = 255;
    private int gaugeRadius = 0;
    private int gaugeBorder = 3;
    protected Paint gaugePaint = new Paint();
    protected boolean gaugePaintInvalidate = true;

    public BatteryBarData() {
        setOutLineWidth(0.0f);
        setName(ResourceUtil.getString(R.string.battery_bar));
    }

    public int getGaugeColor() {
        return gaugeColor;
    }

    public void setGaugeColor(int gaugeColor) {
        if (this.gaugeColor != gaugeColor) {
            gaugePaintInvalidate = true;
            this.gaugeColor = gaugeColor;
        }
    }

    public int getGaugeAlpha() {
        return gaugeAlpha;
    }

    public void setGaugeAlpha(int gaugeAlpha) {
        if (this.gaugeAlpha != gaugeAlpha) {
            gaugePaintInvalidate = true;
            this.gaugeAlpha = gaugeAlpha;
        }
    }

    public void setGaugeRadius(int radius) {
        if (gaugeRadius != radius) {
            gaugeRadius = radius;
        }
    }

    public int getGaugeRadius() {
        return gaugeRadius;
    }

    public int getMaxGaugeBorder() {
        return MAX_GAUGE_BORDER;
    }

    public int getGaugeBorder() {
        return gaugeBorder;
    }

    public void setGaugeBorder(int gaugeBorder) {
        this.gaugeBorder = gaugeBorder;
    }

    protected void initGaugePaint() {
        if (gaugePaintInvalidate) {
            gaugePaintInvalidate = false;
            gaugePaint.reset();
            gaugePaint.setColor(getGaugeColor());
            gaugePaint.setAntiAlias(true);
            gaugePaint.setAlpha(getGaugeAlpha());
        }
    }

    protected Paint getGaugePaint() {
        if (gaugePaintInvalidate) {
            initGaugePaint();
        }
        return gaugePaint;
    }

    public void draw(Canvas canvas) {
        float l = 0;
        float t = 0;
        float r = getWidth();
        float b = getHeight();
        float rx = (Math.min(r, b) * (float) getRadius()) / 200.0f;
        canvas.setMatrix(getMatrix());
        canvas.drawRoundRect(new RectF(l, t, r, b), rx, rx, getPaint());
        rx = (Math.min(r, b) * (float) getGaugeRadius()) / 200.0f;
        float levelR = ((r - (float) gaugeBorder) * (float) DiyWidgetApplication.getInstance().getBatteryLevelPercent()) / 100.0f;
        canvas.drawRoundRect(new RectF(((float) gaugeBorder + l), ((float) gaugeBorder + t), levelR, (b - (float) gaugeBorder)), rx, rx, getGaugePaint());
        if (isEnableOutline()) {
            canvas.drawRoundRect(new RectF(l, t, r, b), rx, rx, getOutlinePaint());
        }
    }

    public void deleteResource() {
        super.deleteResource();
        gaugePaint = null;
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_GAUGE_COLOR, String.valueOf(gaugeColor));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_GAUGE_ALPHA, String.valueOf(gaugeAlpha));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_GAUGE_RADIUS, String.valueOf(gaugeRadius));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_GAUGE_BORDER, String.valueOf(gaugeBorder));
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
                                if (XMLConst.ATTRIBUTE_GAUGE_COLOR.equals(attrName)) {
                                    setGaugeColor(Integer.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_GAUGE_ALPHA.equals(attrName)) {
                                    setGaugeAlpha(Integer.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_GAUGE_RADIUS.equals(attrName)) {
                                    setGaugeRadius(Integer.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_GAUGE_BORDER.equals(attrName)) {
                                    setGaugeBorder(Integer.valueOf(attrValue));
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
