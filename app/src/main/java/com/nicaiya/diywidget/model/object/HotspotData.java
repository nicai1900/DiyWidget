package com.nicaiya.diywidget.model.object;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;


import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.DiyWidgetConfigActivity;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class HotspotData extends AbsObjectData {
    private static final boolean DEBUG = false;
    private Bitmap icon;
    private Intent intent;
    private String label;
    private int type;
    public static final String TAG = HotspotData.class.getSimpleName();
    private static final String SUPER_TAG = HotspotData.class.getSuperclass().getSimpleName();

    public HotspotData() {
        setName(ResourceUtil.getString(R.string.hotspot));
        initRemoveAction();
    }

    public void initEditWidgetAction() {
        type = 2;
        ComponentName componentName = new ComponentName(DiyWidgetApplication.getInstance(), DiyWidgetConfigActivity.class);
        intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(componentName);
        intent.putExtra("action", "edit");
        label = ResourceUtil.getString(R.string.hot_spot_widget_editor);
        icon = ResourceUtil.getBitmap(R.mipmap.ic_launcher);
    }

    public void initRemoveAction() {
        type = 2;
        intent = null;
        label = ResourceUtil.getString(R.string.hot_spot_remove);
        icon = ResourceUtil.getBitmap(R.drawable.widget_nonaction);
    }

    public void draw(Canvas canvas) {
    }

    public void deleteResource() {
        super.deleteResource();
        intent = null;
        label = null;
        if (icon != null) {
            icon.recycle();
            icon = null;
        }
    }

    public void setLeft(float left) {
    }

    public void setTop(float top) {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void setData(int type, Intent intent, String label, Bitmap icon) {
        this.type = type;
        this.intent = intent;
        this.label = label;
        this.icon = icon;
    }

    public void putToXmlSerializer(ConfigFileData configFileData) throws Exception {
        XmlSerializer serializer = configFileData.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_TYPE, String.valueOf(type));
        if (intent != null) {
            serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_INTENT, intent.toUri(0));
        }
        if (label != null) {
            serializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_LABEL, label);
        }
        if (icon != null) {
            XMLBitmapUtil.putToXmlSerializer(configFileData, icon);
        }
        super.putToXmlSerializer(configFileData);
        serializer.endTag(ConfigFileData.XML_NAMESPACE, TAG);
    }

    public void updateFromXmlPullParser(ConfigFileData data) {
        type = 2;
        intent = null;
        label = null;
        icon = null;
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
                                if (attrName.equals(XMLConst.ATTRIBUTE_TYPE)) {
                                    setType(Integer.valueOf(attrValue));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_INTENT)) {
                                    setIntent(Intent.parseUri(attrValue, 0));
                                } else if (attrName.equals(XMLConst.ATTRIBUTE_LABEL)) {
                                    setLabel(attrValue);
                                }
                            }
                        } else if (tag.equals(XMLBitmapUtil.TAG)) {
                            setIcon(XMLBitmapUtil.updateFromXmlPullParser(data));
                        } else if (SUPER_TAG.equals(parser.getName())) {
                            super.updateFromXmlPullParser(data);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(tag)) {
                            if (type == 2) {
                                if (intent == null) {
                                    initRemoveAction();
                                    return;
                                } else {
                                    initEditWidgetAction();
                                }
                            }
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

