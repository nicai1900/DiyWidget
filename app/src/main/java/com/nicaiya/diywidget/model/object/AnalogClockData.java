package com.nicaiya.diywidget.model.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.Calendar;

public class AnalogClockData extends ImageData {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = AnalogClockData.class.getSimpleName();
    private static final String SUPER_TAG = AnalogClockData.class.getSuperclass().getSimpleName();

    public static final int BITMAP_RES_MAX_SIZE = 800;
    public static final float MAX_HEIGHT = 640.0f;
    public static final float MAX_WIDTH = 640.0f;
    private static final int OVERSIZE = 2;
    private Bitmap bezel;
    private Matrix clockMatrix;
    private Paint clockPaint;
    private Bitmap hour;
    private Bitmap index;
    private Bitmap minute;
    private Rect clockRect = new Rect(0, 0, BITMAP_RES_MAX_SIZE, BITMAP_RES_MAX_SIZE);
    private Rect bezelSrcRect = new Rect();
    private Rect indexSrcRect = new Rect();
    private Rect hourSrcRect = new Rect();
    private Rect minuteSrcRect = new Rect();

    public AnalogClockData() {
        setName(ResourceUtil.getString(R.string.analog_clock));
        clockPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        clockMatrix = new Matrix();
        setBezel(ResourceUtil.getNoScaledBitmap(R.drawable.bezel2));
        setIndex(ResourceUtil.getNoScaledBitmap(R.drawable.index2));
        setHourHand(ResourceUtil.getNoScaledBitmap(R.drawable.hour_hand2));
        setMinuteHand(ResourceUtil.getNoScaledBitmap(R.drawable.minute_hand2));
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

    public void setBezel(Bitmap bitmap) {
        if (bezel != bitmap) {
            bezel = bitmap;
            bezelSrcRect.set(0, 0, bezel.getWidth(), bezel.getHeight());
        }
    }

    public void setIndex(Bitmap bitmap) {
        if (index != bitmap) {
            index = bitmap;
            indexSrcRect.set(0, 0, index.getWidth(), index.getHeight());
        }
    }

    public void setHourHand(Bitmap bitmap) {
        if (hour != bitmap) {
            hour = bitmap;
            hourSrcRect.set(0, 0, hour.getWidth(), hour.getHeight());
        }
    }

    public void setMinuteHand(Bitmap bitmap) {
        if (minute != bitmap) {
            minute = bitmap;
            minuteSrcRect.set(0, 0, minute.getWidth(), minute.getHeight());
        }
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(BITMAP_RES_MAX_SIZE, BITMAP_RES_MAX_SIZE, Bitmap.Config.ARGB_8888);
        super.setBitmap(bitmap);
        Canvas clockCanvas = new Canvas(bitmap);
        getBitmap().eraseColor(0xffffff);
        if ((bezel != null) && (bezel.getWidth() > OVERSIZE)) {
            clockMatrix.reset();
            clockCanvas.setMatrix(clockMatrix);
            clockCanvas.drawBitmap(bezel, bezelSrcRect, getCenterRect(clockRect, bezelSrcRect), clockPaint);
        }
        if ((index != null) && (index.getWidth() > OVERSIZE)) {
            clockCanvas.drawBitmap(index, indexSrcRect, getCenterRect(clockRect, indexSrcRect), clockPaint);
        }
        Calendar calendar = Calendar.getInstance();
        int dstHalfWidth = clockRect.width() / 2;
        int dstHalfHeight = clockRect.height() / 2;
        if ((hour != null) && (hour.getWidth() > OVERSIZE)) {
            Rect hourDstRect = getCenterRect(clockRect, hourSrcRect);
            clockMatrix.reset();
            clockMatrix.preTranslate((float) dstHalfWidth, (float) dstHalfHeight);
            clockMatrix.preRotate((float) ((((calendar.get(Calendar.HOUR) * 360) / 12) - 90) + ((calendar.get(Calendar.MINUTE) * 30) / 60)));
            clockMatrix.preTranslate((float) -dstHalfWidth, (float) -dstHalfHeight);
            clockCanvas.setMatrix(clockMatrix);
            clockCanvas.drawBitmap(hour, hourSrcRect, hourDstRect, clockPaint);
        }
        if ((minute != null) && (minute.getWidth() > OVERSIZE)) {
            Rect minuteDstRect = getCenterRect(clockRect, minuteSrcRect);
            clockMatrix.reset();
            clockMatrix.preTranslate((float) dstHalfWidth, (float) dstHalfHeight);
            clockMatrix.preRotate((float) (((calendar.get(Calendar.MINUTE) * 360) / 60) - 90));
            clockMatrix.preTranslate((float) -dstHalfWidth, (float) -dstHalfHeight);
            clockCanvas.setMatrix(clockMatrix);
            clockCanvas.drawBitmap(minute, minuteSrcRect, minuteDstRect, clockPaint);
        }
        super.draw(canvas);
        super.setBitmap(null);
        bitmap.recycle();
    }

    private Rect getCenterRect(Rect dst, Rect src) {
        int width = src.width() * 2;
        int height = src.height() * 2;
        int dstWidth = dst.width();
        int dstHeight = dst.height();
        if ((width > dstWidth) || (height > dstHeight)) {
            float scale = Math.min(((float) dstWidth / (float) width), ((float) dstHeight / (float) height));
            width = (int) ((float) width * scale);
            height = (int) ((float) height * scale);
        }
        int left = (dst.width() - width) / 2;
        int top = (dst.height() - height) / 2;
        return new Rect(left, top, (left + width), (top + height));
    }

    public void deleteResource() {
        super.deleteResource();
        if (bezel != null) {
            bezel.recycle();
            bezel = null;
        }
        if (index != null) {
            index.recycle();
            index = null;
        }
        if (hour != null) {
            hour.recycle();
            hour = null;
        }
        if (minute != null) {
            minute.recycle();
            minute = null;
        }
        clockPaint = null;
        clockMatrix = null;
        clockRect = null;
        bezelSrcRect = null;
        indexSrcRect = null;
        hourSrcRect = null;
        minuteSrcRect = null;
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        XMLBitmapUtil.putToXmlSerializer(configFileData, bezel, XMLConst.TAG_ANALOGCLOCK_BEZEL);
        XMLBitmapUtil.putToXmlSerializer(configFileData, index, XMLConst.TAG_ANALOGCLOCK_INDEX);
        XMLBitmapUtil.putToXmlSerializer(configFileData, hour, XMLConst.TAG_ANALOGCLOCK_HOUR);
        XMLBitmapUtil.putToXmlSerializer(configFileData, minute, XMLConst.TAG_ANALOGCLOCK_MINUTE);
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

                        } else if (XMLConst.TAG_ANALOGCLOCK_BEZEL.equals(tag)) {
                            setBezel(XMLBitmapUtil.updateFromXmlPullParser(data));
                        } else if ((XMLConst.TAG_ANALOGCLOCK_INDEX.equals(tag))) {
                            setIndex(XMLBitmapUtil.updateFromXmlPullParser(data));
                        } else if ((XMLConst.TAG_ANALOGCLOCK_HOUR.equals(tag))) {
                            setHourHand(XMLBitmapUtil.updateFromXmlPullParser(data));
                        } else if ((XMLConst.TAG_ANALOGCLOCK_MINUTE.equals(tag))) {
                            setMinuteHand(XMLBitmapUtil.updateFromXmlPullParser(data));
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
