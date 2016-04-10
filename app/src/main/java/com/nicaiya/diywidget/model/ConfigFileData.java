package com.nicaiya.diywidget.model;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Xml;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.object.XMLBitmapUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigFileData extends ZipFileData {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = ConfigFileData.class.getSimpleName();

    private static final String APP_VERSION_KEY = "app";
    private static final String CONFIG_FILE_VERSION_KEY = "config";

    private static final String XML_FILE_NAME = "config.xml";
    private static final String VERSION_FILE_NAME = "version.txt";

    public static final int CONFIG_FILE_VERSION = 3;

    private static final String XML_ENCODING = "UTF-8";
    public static final String XML_NAMESPACE = "";

    private ZipFileData.FileData versionFileData;
    private ZipFileData.FileData xmlFileData;

    private int fileNameSeed = 1;

    private XmlSerializer xmlFileSerializer = null;
    private ByteArrayOutputStream os = null;

    private XmlPullParser xmlFileParser = null;
    private ByteArrayInputStream is = null;

    public ConfigFileData() {
        xmlFileData = new ZipFileData.FileData(XML_FILE_NAME, null);
        add(xmlFileData);
        initAppFileVersion();
    }

    public ConfigFileData(File dir) {
        loadFromDir(dir);
    }

    public ConfigFileData(InputStream is) {
        loadFromInputStream(is);
    }

    public ConfigFileData(byte[] data) {
        loadFromByteArray(data);
    }

    public ConfigFileData(AssetManager manager, String dir) {
        loadFromAsset(manager, dir);
    }

    private void initAppFileVersion() {
        JSONObject json = new JSONObject();
        try {
            json.put(CONFIG_FILE_VERSION_KEY, CONFIG_FILE_VERSION);
            json.put(APP_VERSION_KEY, ResourceUtil.getVersion());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        byte[] versionBuff = json.toString().getBytes();
        versionFileData = new ZipFileData.FileData(VERSION_FILE_NAME, versionBuff);
        add(versionFileData);
    }

    @Override
    public void loadFromDir(File dir) {
        super.loadFromDir(dir);
        for (ZipFileData.FileData data : this) {
            String name = data.getName();
            if (xmlFileData == null && (XML_FILE_NAME.equals(name))) {
                xmlFileData = data;
            } else if (versionFileData == null && VERSION_FILE_NAME.equals(name)) {
                versionFileData = data;
            }
        }
    }

    @Override
    public void loadFromInputStream(InputStream is) {
        super.loadFromInputStream(is);
        for (ZipFileData.FileData data : this) {
            String name = data.getName();
            if (xmlFileData == null && (XML_FILE_NAME.equals(name))) {
                xmlFileData = data;
            } else if (versionFileData == null && VERSION_FILE_NAME.equals(name)) {
                versionFileData = data;
            }
        }
    }

    @Override
    public void loadFromAsset(AssetManager manager, String dir) {
        super.loadFromAsset(manager, dir);
        for (ZipFileData.FileData data : this) {
            String name = data.getName();
            if (xmlFileData == null && (XML_FILE_NAME.equals(name))) {
                xmlFileData = data;
            } else if (versionFileData == null && VERSION_FILE_NAME.equals(name)) {
                versionFileData = data;
            }
        }
    }

    public void setXmlAppFileVersion() {
        JSONObject json = new JSONObject();
        try {
            json.put(CONFIG_FILE_VERSION_KEY, 0);
            json.put(APP_VERSION_KEY, 63);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        byte[] versionBuff = json.toString().getBytes();
        versionFileData = new ZipFileData.FileData(VERSION_FILE_NAME, versionBuff);
        add(versionFileData);
    }

    public int getAppVersion() {
        byte[] versionBuff = versionFileData.getData();
        String versionStr = new String(versionBuff);
        try {
            JSONObject json = new JSONObject(versionStr);
            return json.getInt(APP_VERSION_KEY);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return -1;
    }

    public int getFileVersion() {
        byte[] versionBuff = versionFileData.getData();
        String versionStr = new String(versionBuff);
        try {
            JSONObject json = new JSONObject(versionStr);
            return json.getInt(CONFIG_FILE_VERSION_KEY);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return -1;
    }

    public byte[] getXmlByteArray() {
        return xmlFileData.getData();
    }

    public void setXmlByteArray(byte[] data) {
        xmlFileData.setData(data);
    }

    private String getNewFileName() {
        String fileName = String.valueOf(fileNameSeed);
        fileNameSeed += 1;
        return fileName;
    }

    public String addBitmap(Bitmap bitmap) {
        String fileName = getNewFileName() + ".png";
        byte[] b = XMLBitmapUtil.BitmapToByteArray(bitmap);
        ZipFileData.FileData data = new ZipFileData.FileData(fileName, b);
        add(data);
        return fileName;
    }

    public Bitmap getBitmap(String fileName) {
        if (fileName == null) {
            return null;
        }
        for (ZipFileData.FileData data : this) {
            if (fileName.equals(data.getName())) {
                byte[] encodeBytes = data.getData();
                return XMLBitmapUtil.ByteArrayToBitmap(encodeBytes);
            }
        }
        return null;
    }

    public XmlSerializer startXmlSerialize() {
        xmlFileSerializer = Xml.newSerializer();
        os = new ByteArrayOutputStream();
        try {
            xmlFileSerializer.setOutput(os, XML_ENCODING);
            xmlFileSerializer.startDocument(XML_ENCODING, true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return xmlFileSerializer;
    }

    public XmlSerializer getXmlSerializer() {
        return xmlFileSerializer;
    }

    public void completeXmlSerialize() {
        try {
            xmlFileSerializer.endDocument();
            xmlFileSerializer.flush();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        xmlFileData.setData(os.toByteArray());
        try {
            os.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        xmlFileSerializer = null;
        os = null;
    }

    public XmlPullParser startXmlParse() {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            xmlFileParser = factory.newPullParser();
            is = new ByteArrayInputStream(xmlFileData.getData());
            xmlFileParser.setInput(is, XML_ENCODING);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return xmlFileParser;
    }

    public XmlPullParser getXmlParser() {
        return xmlFileParser;
    }

    public void completeXmlParse() {
        try {
            is.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        xmlFileParser = null;
        is = null;
    }
}