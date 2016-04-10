package com.nicaiya.diywidget.model.object;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.Calendar;

public class CircularArcClockData extends ImageData {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = CircularArcClockData.class.getSimpleName();
    private static final String SUPER_TAG = CircularArcClockData.class.getSuperclass().getSimpleName();

    private static final int BLUR = 3;
    public static final int MAX_ARC_BORDER = 200;
    public static final int MAX_ARC_RADIUS = 200;
    public static final float MAX_HEIGHT = 640.0f;
    public static final float MAX_WIDTH = 640.0f;
    private static final int SIZE = 400;
    private Paint clockPaint;

    private int bezelAlpha = 117;
    private int bezelBorder = 200;
    private int bezelColor = -8420720;
    private int bezelRadius = 162;

    private int hourAlpha = 71;
    private int hourBorder = 158;
    private int hourColor = Color.RED;
    private int hourRadius = 123;

    private int minuteAlpha = 61;
    private int minuteBorder = 118;
    private int minuteColor = Color.BLUE;
    private int minuteRadius = 85;

    public CircularArcClockData() {
        setName(ResourceUtil.getString(R.string.circular_arc_clock));
        clockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public float getMaxWidth() {
        return MAX_WIDTH;
    }

    public float getMaxHeight() {
        return MAX_HEIGHT;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
    }

    public int getMaxArcRadius() {
        return MAX_ARC_RADIUS;
    }

    public int getMaxArcBorder() {
        return MAX_ARC_BORDER;
    }

    public int getBezelColor() {
        return bezelColor;
    }

    public void setBezelColor(int bezelColor) {
        this.bezelColor = bezelColor;
    }

    public int getBezelAlpha() {
        return bezelAlpha;
    }

    public void setBezelAlpha(int bezelAlpha) {
        this.bezelAlpha = bezelAlpha;
    }

    public int getBezelRadius() {
        return bezelRadius;
    }

    public void setBezelRadius(int bezelRadius) {
        this.bezelRadius = bezelRadius;
    }

    public int getBezelBorder() {
        return bezelBorder;
    }

    public void setBezelBorder(int bezelBorder) {
        this.bezelBorder = bezelBorder;
    }

    public int getHourColor() {
        return hourColor;
    }

    public void setHourColor(int hourColor) {
        this.hourColor = hourColor;
    }

    public int getHourAlpha() {
        return hourAlpha;
    }

    public void setHourAlpha(int hourAlpha) {
        this.hourAlpha = hourAlpha;
    }

    public int getHourRadius() {
        return hourRadius;
    }

    public void setHourRadius(int hourRadius) {
        this.hourRadius = hourRadius;
    }

    public int getHourBorder() {
        return hourBorder;
    }

    public void setHourBorder(int hourBorder) {
        this.hourBorder = hourBorder;
    }

    public int getMinuteColor() {
        return minuteColor;
    }

    public void setMinuteColor(int minuteColor) {
        this.minuteColor = minuteColor;
    }

    public int getMinuteAlpha() {
        return minuteAlpha;
    }

    public void setMinuteAlpha(int minuteAlpha) {
        this.minuteAlpha = minuteAlpha;
    }

    public int getMinuteRadius() {
        return minuteRadius;
    }

    public void setMinuteRadius(int minuteRadius) {
        this.minuteRadius = minuteRadius;
    }

    public int getMinuteBorder() {
        return minuteBorder;
    }

    public void setMinuteBorder(int minuteBorder) {
        this.minuteBorder = minuteBorder;
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(806, 806, Bitmap.Config.ARGB_8888);
        Canvas clockCanvas = new Canvas(bitmap);
        super.setBitmap(bitmap);
        int eraseColor = 0xffffff;
        getBitmap().eraseColor(eraseColor);
        Calendar calendar = Calendar.getInstance();

        RectF arcRect = new RectF();
        clockPaint.setAlpha(bezelAlpha);
        clockPaint.setStyle(Paint.Style.STROKE);
        clockPaint.setMaskFilter(new BlurMaskFilter(BLUR, BlurMaskFilter.Blur.NORMAL));
        clockPaint.setColor(bezelColor);
        clockPaint.setAlpha(bezelAlpha);
        float strokeWidth = Math.max(2.0f * (bezelBorder - bezelRadius), 2.0f);
        clockPaint.setStrokeWidth(strokeWidth);
        float strokehalfWidth = strokeWidth / 2.0f;
        float left = strokehalfWidth + (403 - 2 * bezelBorder);
        float right = 403 + 2 * bezelBorder - strokehalfWidth;
        arcRect.set(left, left, right, right);
        clockCanvas.drawArc(arcRect, 0.0f, 360.0f, false, clockPaint);

        clockPaint.setColor(hourColor);
        clockPaint.setAlpha(hourAlpha);
        strokeWidth = Math.max(2.0F * (this.hourBorder - this.hourRadius), 2.0F);
        clockPaint.setStrokeWidth(strokeWidth);
        strokehalfWidth = strokeWidth / 2.0F;
        left = strokehalfWidth + (403 - 2 * this.hourBorder);
        right = 403 + 2 * this.hourBorder - strokehalfWidth;
        arcRect.set(left, left, right, right);
        clockCanvas.drawArc(arcRect, -90.0F, 360 * calendar.get(Calendar.HOUR) / 12 + 30 * calendar.get(Calendar.MINUTE) / 60, false, this.clockPaint);

        clockPaint.setColor(minuteColor);
        clockPaint.setAlpha(minuteAlpha);
        strokeWidth = Math.max(2.0F * (this.minuteBorder - this.minuteRadius), 2.0F);
        clockPaint.setStrokeWidth(strokeWidth);
        strokehalfWidth = strokehalfWidth / 2.0F;
        left = strokehalfWidth + (403 - 2 * this.minuteBorder);
        right = 403 + 2 * this.minuteBorder - strokehalfWidth;
        arcRect.set(left, left, right, right);
        clockCanvas.drawArc(arcRect, -90.0F, 360 * calendar.get(Calendar.MINUTE) / 60, false, this.clockPaint);
        super.draw(canvas);
        super.setBitmap(null);
        bitmap.recycle();
    }

    public void deleteResource() {
        super.deleteResource();
        clockPaint = null;
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_BEZEL_COLOR, String.valueOf(bezelColor));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_BEZEL_ALPHA, String.valueOf(bezelAlpha));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_BEZEL_RADIUS, String.valueOf(bezelRadius));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_BEZEL_BORDER, String.valueOf(bezelBorder));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_HOUR_COLOR, String.valueOf(hourColor));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_HOUR_ALPHA, String.valueOf(hourAlpha));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_HOUR_RADIUS, String.valueOf(hourRadius));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_HOUR_BORDER, String.valueOf(hourBorder));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_MINUTE_COLOR, String.valueOf(minuteColor));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_MINUTE_ALPHA, String.valueOf(minuteAlpha));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_MINUTE_RADIUS, String.valueOf(minuteRadius));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ARC_MINUTE_BORDER, String.valueOf(minuteBorder));
        Bitmap bitmap = super.getBitmap();
        super.setBitmap(null);
        super.putToXmlSerializer(configFileData);
        super.setBitmap(bitmap);
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
                                if (attrName.equals(XMLConst.ATTRIBUTE_ARC_BEZEL_COLOR)) {
                                    setBezelColor(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_BEZEL_ALPHA)) {
                                    setBezelAlpha(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_BEZEL_RADIUS)) {
                                    setBezelRadius(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_BEZEL_BORDER)) {
                                    setBezelBorder(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_HOUR_COLOR)) {
                                    setHourColor(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_HOUR_ALPHA)) {
                                    setHourAlpha(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_HOUR_RADIUS)) {
                                    setHourRadius(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_HOUR_BORDER)) {
                                    setHourBorder(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_MINUTE_COLOR)) {
                                    setMinuteColor(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_MINUTE_ALPHA)) {
                                    setMinuteAlpha(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_MINUTE_RADIUS)) {
                                    setMinuteRadius(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_ARC_MINUTE_BORDER)) {
                                    setMinuteBorder(Integer.valueOf(attrValue));
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
