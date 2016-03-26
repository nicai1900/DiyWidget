package com.nicaiya.diywidget.model.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.drawable.WidgetDrawable;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WidgetData {

    private static final boolean DEBUG = false;
    private static final String TAG = WidgetData.class.getSimpleName();

    public static final int ALL_WIDGET = 0;
    public static final int CLOCK_WIDGET = 1;
    public static final int BATTERY_WIDGET = 2;
    public static final int DATE_WIDGET = 3;
    public static final int WEATHER_WIDGET = 4;
    public static final int MUSIC_WIDGET = 5;

    private static final int WIDGET_DEFAULT_SIZE = 400;

    private BackgroundData background = null;
    private AbsObjectData focusData = null;

    private String name = ResourceUtil.getString(R.string.widget);
    private List<AbsObjectData> objects = new ArrayList<>();

    public WidgetData() {
        this(WIDGET_DEFAULT_SIZE, WIDGET_DEFAULT_SIZE);
    }

    public WidgetData(int width, int height) {
        initDefaultData(width, height);
    }

    private void initDefaultData(int width, int height) {
        BackgroundData backgroundData = new BackgroundData();
        backgroundData.setWidth(width);
        backgroundData.setHeight(height);
        setBackground(backgroundData);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setHeight(float height) {
        background.setHeight(height);
    }

    public float getHeight() {
        return background.getHeight();
    }

    public void setWidth(float width) {
        background.setWidth(width);
    }

    public float getWidth() {
        return background.getWidth();
    }

    public void setBackground(BackgroundData data) {
        if (objects != null) {
            background = data;
            int size = objects.size();
            for (int i = 0; i < size; i++) {
                AbsObjectData item = objects.get(i);
                if (item instanceof BackgroundData) {
                    objects.remove(i);
                    break;
                }
            }
            size = objects.size();
            if (size == 0) {
                objects.add(data);
            } else {
                objects.add(1, data);
            }
        }

    }

    public BackgroundData getBackground() {
        return background;
    }

    public void setFocusData(AbsObjectData data) {
        if (focusData != null) {
            focusData.setFocus(false);
        }
        focusData = data;
        if (focusData != null) {
            focusData.setFocus(true);
        }
    }

    public AbsObjectData getFocusData() {
        return focusData;
    }

    public void addData(AbsObjectData object) {
        if ((object != null) && (objects != null)) {
            objects.add(object);
        }
    }

    public void addDataAt(AbsObjectData object, int index) {
        if (object != null) {
            objects.add(index, object);
        }
    }

    public boolean removeData(AbsObjectData object) {
        if (objects.contains(object)) {
            objects.remove(object);
            object.deleteResource();
            return true;
        }
        return false;
    }

    public boolean removeDataAt(int index) {
        AbsObjectData object = objects.remove(index);
        if (object != null) {
            object.deleteResource();
            return true;
        }
        return false;
    }

    public AbsObjectData getDataAt(int index) {
        return objects.get(index);
    }

    public int getDataCount() {
        return objects.size();
    }

    public int getDataIndex(AbsObjectData object) {
        if (object != null) {
            return objects.indexOf(object);
        }
        return 0;
    }

    public boolean changeDataAt(AbsObjectData srcObj, AbsObjectData dstObj, int index) {
        if (objects.contains(srcObj)) {
            objects.remove(srcObj);
            objects.add(index, dstObj);
            return true;
        }
        return false;
    }

    public boolean changeDepth(AbsObjectData object, int depth) {
        if (objects.contains(object)) {
            objects.remove(object);
            objects.add(depth, object);
            return true;
        }
        return false;
    }

    public void deleteResource() {
        name = null;
        if (objects != null) {
            while (objects.size() > 0) {
                removeData(objects.get(0));
            }
            objects = null;
        }
    }

    public Bitmap createPreViewBitmap() {
        WidgetDrawable widgetDrawable = new WidgetDrawable(this);
        Bitmap bitmap = Bitmap.createBitmap((int) getWidth(), (int) getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        AbsObjectData focusData = getFocusData();
        setFocusData(null);
        widgetDrawable.draw(canvas);
        setFocusData(focusData);
        Bitmap scaledOrRotatedBitmap = ResourceUtil.createScaledOrRotatedBitmap(bitmap, 0, 400, 300);
        if (scaledOrRotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return scaledOrRotatedBitmap;
    }

    public ConfigFileData getConfigureFileData() {
        ConfigFileData fileData = new ConfigFileData();
        fileData.startXmlSerialize();
        try {
            putToXmlSerializer(fileData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileData.completeXmlSerialize();
        }
        return fileData;
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_NAME, name);
        for (AbsObjectData object : objects) {
            object.putToXmlSerializer(configFileData);
        }
        serializer.endTag(ConfigFileData.XML_NAMESPACE, TAG);
    }

    public static WidgetData createFromConfigFileData(ConfigFileData configFileData) {
        XmlPullParser parser = configFileData.startXmlParse();

        try {
            WidgetData widgetData = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (TAG.equals(tag)) {
                            widgetData = new WidgetData();
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String attrName = parser.getAttributeName(i);
                                String attrValue = parser.getAttributeValue(i);
                                if (XMLConst.ATTRIBUTE_NAME.equals(attrName)) {
                                    widgetData.setName(attrValue);
                                }
                            }
                        } else if (BackgroundData.TAG.equals(tag)) {
                            BackgroundData backgroundData = new BackgroundData();
                            backgroundData.updateFromXmlPullParser(configFileData);
                            if (widgetData != null) {
                                widgetData.setBackground(backgroundData);
                            }
                        } else if (ImageData.TAG.equals(tag)) {
                            ImageData imageData = new ImageData();
                            imageData.updateFromXmlPullParser(configFileData);
                            if (widgetData != null) {
                                widgetData.addData(imageData);
                            }
                        } else if (TextData.TAG.equals(tag)) {
                            TextData textData = new TextData();
                            textData.updateFromXmlPullParser(configFileData);
                            if (widgetData != null) {
                                widgetData.addData(textData);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(tag)) {
                            return widgetData;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            configFileData.completeXmlParse();
        }
        return null;
    }

    public static ConfigFileData getConfigFileDataWithNewName(ConfigFileData configFileData, String newName) {
        WidgetData widgetData = createFromConfigFileData(configFileData);
        if (widgetData != null) {
            widgetData.setName(newName);
            return widgetData.getConfigureFileData();
        }
        return null;
    }

    @Override
    public String toString() {
        return "WidgetData{" +
                "name='" + name + '\'' +
                ", objects=" + Arrays.toString(objects.toArray()) +
                '}';
    }
}