package com.nicaiya.diywidget.model.object;

import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Log;

import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.editable.EditableHeaderFooter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.Calendar;
import java.util.Locale;

public class DateTimeSetData extends TextData implements EditableHeaderFooter {

    private static final boolean DEBUG = false;
    public static final String TAG = DateTimeSetData.class.getSimpleName();
    private static final String SUPER_TAG = DateTimeSetData.class.getSuperclass().getSimpleName();

    private static final int TT_NONE = 0;
    private static final int TT_NUM_4 = 1;
    private static final int TT_NUM_2 = 2;
    private static final int TT_NUM = 3;
    private static final int TT_ENG_LONG = 4;
    private static final int TT_ENG_SHORT = 5;
    private static final int TT_LANGUAGE_LONG = 6;
    private static final int TT_LANGUAGE_SHORT = 7;

    private DateTimeSetData.TIME_SET[] timeSetList = new DateTimeSetData.TIME_SET[]{
            DateTimeSetData.TIME_SET.TYPE_1, DateTimeSetData.TIME_SET.TYPE_2, DateTimeSetData.TIME_SET.TYPE_3,
            DateTimeSetData.TIME_SET.TYPE_4, DateTimeSetData.TIME_SET.TYPE_5, DateTimeSetData.TIME_SET.TYPE_6,
            DateTimeSetData.TIME_SET.TYPE_7, DateTimeSetData.TIME_SET.TYPE_8, DateTimeSetData.TIME_SET.TYPE_9,
            DateTimeSetData.TIME_SET.TYPE_10, DateTimeSetData.TIME_SET.TYPE_11, DateTimeSetData.TIME_SET.TYPE_12,
            DateTimeSetData.TIME_SET.TYPE_13, DateTimeSetData.TIME_SET.TYPE_14, DateTimeSetData.TIME_SET.TYPE_15,
            DateTimeSetData.TIME_SET.TYPE_16, DateTimeSetData.TIME_SET.TYPE_17, DateTimeSetData.TIME_SET.TYPE_18,
            DateTimeSetData.TIME_SET.TYPE_19, DateTimeSetData.TIME_SET.TYPE_20, DateTimeSetData.TIME_SET.TYPE_21,
            DateTimeSetData.TIME_SET.TYPE_22};

    private DateTimeSetData.TIME_SET curTimeSet;
    private String gapString = "";
    private int gap = 0;

    private int type = 0;

    private String header = "";
    private String footer = "";

    public DateTimeSetData() {
        setType(type);
        setName(ResourceUtil.getString(R.string.date_time_set));
        setSize(25.0f);
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

    public void setGap(int gap) {
        this.gap = gap;
        gapString = "";
        for (int i = 0; i < gap; i++) {
            gapString = gapString + " ";
        }
    }

    public int getGap() {
        return gap;
    }

    public void setType(int type) {
        this.type = type;
        curTimeSet = timeSetList[type];
    }

    public int getType() {
        return type;
    }

    public String getDateText() {
        String year = getYear();
        String month = getMonth();
        String day = getDay();
        String dayOfWeek = getDayOfWeek();
        if (!year.equals("")) {
            year = year + ".";
        }
        if (!month.equals("")) {
            month = month + ".";
        }
        if (!dayOfWeek.equals("")) {
            dayOfWeek = "." + dayOfWeek;
        }
        return year + month + day + dayOfWeek;
    }

    private String getYear() {
        int yearType = curTimeSet.yearType;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String yearString = String.valueOf(year);
        switch (yearType) {
            case TT_NUM_4: {
                return yearString;
            }
            case TT_NUM_2: {
                year = year % 100;
                yearString = String.valueOf((year % 100));
                if (year < 10) {
                    return "0" + yearString;
                }
                return yearString;
            }
        }
        return yearString;
    }

    private String getMonth() {
        int monthType = curTimeSet.monthType;
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String monthString = String.valueOf(month);
        switch (monthType) {
            case TT_NUM_2: {
                if (month < 10) {
                    return "0" + monthString;
                }
                return monthString;
            }
            case TT_NUM: {
                return monthString;
            }
            case TT_ENG_LONG: {
                monthString = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
                return monthString;
            }
            case TT_ENG_SHORT: {
                monthString = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
                return monthString;
            }
            case TT_LANGUAGE_LONG: {
                Locale locale = ResourceUtil.getCurrentLocale();
                monthString = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
                return monthString;
            }
            case TT_LANGUAGE_SHORT: {
                Locale locale = ResourceUtil.getCurrentLocale();
                monthString = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.SHORT, locale);
                return monthString;
            }
        }
        return monthString;
    }

    private String getDay() {
        int dayType = curTimeSet.dayType;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String dayString = String.valueOf(day);
        switch (dayType) {
            case TT_NUM_2: {
                if (day < 10) {
                    return dayString;
                }
                return dayString;
            }
            case TT_NUM: {
                return dayString;
            }
        }
        return dayString;
    }

    private String getDayOfWeek() {
        int dayOfWeekType = curTimeSet.dayOfWeekType;
        String dayOfWeekString = "";
        switch (dayOfWeekType) {
            case TT_ENG_LONG: {
                dayOfWeekString = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
                return dayOfWeekString;
            }
            case TT_ENG_SHORT: {
                dayOfWeekString = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
                return dayOfWeekString;
            }
            case TT_LANGUAGE_LONG: {
                Locale locale = ResourceUtil.getCurrentLocale();
                dayOfWeekString = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
                return dayOfWeekString;
            }
            case TT_LANGUAGE_SHORT: {
                Locale locale = ResourceUtil.getCurrentLocale();
                dayOfWeekString = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
                return dayOfWeekString;
            }
        }
        return dayOfWeekString;
    }

    public void draw(Canvas canvas) {
        String year = getYear();
        String month = getMonth();
        String day = getDay();
        String dayOfWeek = getDayOfWeek();
        if (!TextUtils.isEmpty(year)) {
            year = year + gapString + "." + gapString;
        }
        if (!TextUtils.isEmpty(month)) {
            month = month + gapString + "." + gapString;
        }
        if (!TextUtils.isEmpty(dayOfWeek)) {
            dayOfWeek = gapString + "." + gapString + dayOfWeek;
        }
        String info = header + year + month + day + dayOfWeek + footer;
        setText(info);
        super.draw(canvas);
    }

    public void deleteResource() {
        super.deleteResource();
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_TIME_SET, String.valueOf(type));
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_TEXT_HEADER, header);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_TEXT_FOOTER, footer);
        setType(type);
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
                                if (XMLConst.ATTRIBUTE_TIME_SET.equals(attrName)) {
                                    setType(Integer.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_TEXT_HEADER.equals(attrName)) {
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

    public enum TIME_SET {
        TYPE_1(1, 2, 2, 1),
        TYPE_2(1, 3, 3, 4),
        TYPE_3(1, 2, 2, 5),
        TYPE_4(1, 3, 3, 5),
        TYPE_5(2, 2, 2, 4),
        TYPE_6(2, 3, 3, 4),
        TYPE_7(2, 2, 2, 5),
        TYPE_8(2, 3, 3, 5),
        TYPE_9(1, 2, 2, 0),
        TYPE_10(2, 3, 3, 0),
        TYPE_11(0, 2, 2, 4),
        TYPE_12(0, 3, 3, 4),
        TYPE_13(0, 2, 2, 5),
        TYPE_14(0, 3, 3, 5),
        TYPE_15(0, 4, 2, 4),
        TYPE_16(0, 5, 3, 4),
        TYPE_17(0, 4, 2, 5),
        TYPE_18(0, 5, 3, 5),
        TYPE_19(1, 2, 2, 6),
        TYPE_20(1, 2, 2, 7),
        TYPE_21(0, 2, 2, 6),
        TYPE_22(0, 2, 2, 7);

        public final int dayOfWeekType;
        public final int dayType;
        public final int monthType;
        public final int yearType;

        TIME_SET(int yearType, int monthType, int dayType, int dayOfWeekType) {
            this.yearType = yearType;
            this.monthType = monthType;
            this.dayType = dayType;
            this.dayOfWeekType = dayOfWeekType;
        }
    }
}
