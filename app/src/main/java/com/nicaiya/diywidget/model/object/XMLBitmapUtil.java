package com.nicaiya.diywidget.model.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;

public class XMLBitmapUtil {

    private static final boolean DEBUG = false;
    public static final String TAG = XMLBitmapUtil.class.getSimpleName();

    public static final String FILE = "file:";

    public static byte[] BitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap ByteArrayToBitmap(byte[] bytes) {
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static Bitmap StringToBitMap(String s) {
        try {
            return ByteArrayToBitmap(Base64.decode(s, 0));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static void putToXmlSerializer(ConfigFileData data, Bitmap bitmap) throws Exception {
        putToXmlSerializer(data, bitmap, TAG);
    }

    public static void putToXmlSerializer(ConfigFileData data, Bitmap bitmap, String tag) throws Exception {
        if ((bitmap == null) || (bitmap.isRecycled())) {
            return;
        }
        XmlSerializer serializer = data.getXmlSerializer();
        serializer.startTag(ConfigFileData.XML_NAMESPACE, tag);
        String fileName = data.addBitmap(bitmap);
        serializer.text(FILE + fileName);
        serializer.endTag(ConfigFileData.XML_NAMESPACE, tag);
    }

    public static Bitmap updateFromXmlPullParser(ConfigFileData configFileData) {
        XmlPullParser parser = configFileData.getXmlParser();
        try {
            int eventType = parser.next();
            if (eventType == XmlPullParser.TEXT) {
                String data = parser.getText();
                if (data.startsWith(FILE)) {
                    String fileName = data.substring(FILE.length(), data.length());
                    return configFileData.getBitmap(fileName);
                }
                return StringToBitMap(parser.getText());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


}