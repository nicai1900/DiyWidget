package com.nicaiya.diywidget.database;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.provider.AppWidget_1_1;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.SharedPreferencesManager;
import com.nicaiya.diywidget.model.object.WidgetData;
import com.nicaiya.diywidget.model.object.XMLBitmapUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public class ConfigDataBase {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = ConfigDataBase.class.getSimpleName();

    private static final String DEFAULT_FILE_NAME = "";
    private static final String CONFIG_FILE_NAME_POSTFIX = ".zip";
    private static final String PREVIEW_FILE_NAME_POSTFIX = ".png";

    private static ConfigDataBase instance = null;
    private static ConfigDataBase.OpenHelper openHelper = null;
    private static SQLiteDatabase db = null;

    private static ConfigFileData savedConfigFileData = null;
    public static int defaultFileCount = 0;

    private Context mContext;

    public static ConfigDataBase getInstance(Context context) {
        if (instance == null) {
            synchronized (ConfigDataBase.class) {
                if (instance == null) {
                    instance = new ConfigDataBase(context);
                }
            }
        }
        return instance;
    }

    private ConfigDataBase(Context context) {
        mContext = context;
        if (openHelper == null) {
            openHelper = new ConfigDataBase.OpenHelper(context);
        }
        if (db == null) {
            db = openHelper.getWritableDatabase();
        }
    }

    private long syncInsert(String table, String nullColumnHack, ContentValues values) {
        synchronized (ConfigDataBase.class) {
            return db.insert(table, nullColumnHack, values);
        }
    }

    private int syncUpdate(String table, ContentValues values, String whereClause, String[] whereArgs) {
        synchronized (ConfigDataBase.class) {
            return db.update(table, values, whereClause, whereArgs);
        }
    }

    private int syncDelete(String table, String whereClause, String[] whereArgs) {
        synchronized (ConfigDataBase.class) {
            return db.delete(table, whereClause, whereArgs);
        }
    }

    public void saveWidgetConfigData(WidgetData widgetData) {
        String name = widgetData.getName();
        Bitmap preview = widgetData.createPreViewBitmap();
        ConfigFileData configFileData = widgetData.getConfigureFileData();
        if ((name == null)
                || (name.length() <= 0)
                || (preview == null)
                || (preview.isRecycled())
                || (configFileData == null)) {
            return;
        }
        saveWidgetConfigData(name, preview, configFileData);
        preview.recycle();
    }

    public void saveWidgetConfigData(String name, Bitmap preview, ConfigFileData configFileData) {
        if ((name == null)
                || (name.length() <= 0)
                || (preview == null)
                || (preview.isRecycled())
                || (configFileData == null)) {
            return;
        }
        List<String> nameList = loadWidgetConfigNameList();
        if (nameList.contains(name)) {
            Cursor c = loadConfigDataColumnPreViewConfigDataByName(name);
            try {
                if ((c != null) && (c.getCount() != 0)) {
                    c.moveToFirst();
                    int previewIndex = c.getColumnIndex(DataBase.BCONFIG_PREVIEW);
                    int configDataIndex = c.getColumnIndex(DataBase.BCONFIG_DATA);

                    String previewFileName = c.getString(previewIndex);
                    previewFileWriter(previewFileName, preview);

                    String configFileDataName = c.getString(configDataIndex);
                    configFileDataWriter(configFileDataName, configFileData);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            return;
        }
        long id = insertConfigDataColumn(name, DEFAULT_FILE_NAME, DEFAULT_FILE_NAME);
        if (id != DataBase.INVALID_ID) {
            String previewFileName = previewFileWriter(id, preview);
            String configFileDataName = configFileDataWriter(id, configFileData);
            updateConfigDataColumn(name, previewFileName, configFileDataName);
        }
    }

    public WidgetData loadWidgetConfigData(String name) {
        ConfigFileData configFileData = loadWidgetConfigFileData(name);
        if (configFileData != null) {
            return WidgetData.createFromConfigFileData(configFileData);
        }
        return null;
    }

    public ConfigFileData loadWidgetConfigFileData(String name) {
        ConfigFileData configFileData = null;
        Cursor c = null;
        try {
            c = loadConfigDataColumnConfigDataByName(name);
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(DataBase.BCONFIG_DATA);
                String configFileDataName = c.getString(columnIndex);
                configFileData = configFileDataReader(configFileDataName);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return configFileData;
    }

    public Bitmap loadWidgetConfigPreViewBitmap(String name) {
        Bitmap preview = null;
        Cursor c = null;
        try {
            c = loadConfigDataColumnPreViewByName(name);
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(DataBase.BCONFIG_PREVIEW);
                String previewFileName = c.getString(columnIndex);
                preview = previewFileReader(previewFileName);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return preview;
    }

    public List<String> loadWidgetConfigNameList() {
        ArrayList<String> nameList = new ArrayList<>();
        Cursor c = null;
        try {
            c = loadConfigDataColumnNameArray();
            if ((c != null) && (c.getCount() != 0)) {
                while (c.moveToNext()) {
                    int columnIndex = c.getColumnIndex(DataBase.BCONFIG_NAME);
                    String name = c.getString(columnIndex);
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return nameList;
    }

    public List<String> loadWidgetConfigNameListByType(int type) {
        Cursor c = null;
        try {
            c = loadConfigDataColumnNameArray();
            ArrayList<String> nameList = new ArrayList<>();
            if ((c != null) && (c.getCount() != 0)) {
                while (c.moveToNext()) {
                    int columnIndex = c.getColumnIndex(DataBase.BCONFIG_NAME);
                    String name = c.getString(columnIndex);
                    if (!nameList.contains(name)) {
                        if (type != 0) {
                            String itemType = "";
                            if (itemTypeMap.containsKey(name)) {
                                itemType = itemTypeMap.get(name);
                            } else {
                                itemType = getItemType(loadWidgetConfigFileData(name));
                                itemTypeMap.put(name, itemType);
                            }
                            if (itemType.contains(Integer.toString(type))) {
                                nameList.add(name);
                            }
                        } else {
                            nameList.add(name);
                        }
                    }
                }
            }
            return nameList;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public boolean removeWidgetConfigFileData(String name) {
        return deleteConfigDataColumnByName(name);
    }

    private String previewFileWriter(long id, Bitmap preview) {
        String fileName = id + PREVIEW_FILE_NAME_POSTFIX;
        return previewFileWriter(fileName, preview);
    }

    private String previewFileWriter(String fileName, Bitmap preview) {
        byte[] encodePreview = XMLBitmapUtil.BitmapToByteArray(preview);
        boolean isSuccess = fileWriter(fileName, encodePreview);
        if (isSuccess) {
            return fileName;
        }
        return fileName;
    }

    private String configFileDataWriter(long id, ConfigFileData configFileData) {
        String fileName = id + CONFIG_FILE_NAME_POSTFIX;
        return configFileDataWriter(fileName, configFileData);
    }

    public ConfigFileData getLastConfigFileData() {
        return savedConfigFileData;
    }

    public void setLastConfigFileData(ConfigFileData configFileData) {
        savedConfigFileData = configFileData;
    }

    private String configFileDataWriter(String fileName, ConfigFileData configFileData) {
        byte[] encodeConfigFileData = configFileData.dumpToByteArray();
        setLastConfigFileData((ConfigFileData) configFileData.clone());
        boolean isSuccess = fileWriter(fileName, encodeConfigFileData);
        if (isSuccess) {
            return fileName;
        }
        return fileName;
    }

    private boolean fileWriter(String fileName, byte[] data) {
        File file = new File(mContext.getFilesDir(), fileName);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data);
            os.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
        return false;
    }

    private Bitmap previewFileReader(String fileName) {
        byte[] encodePreview = fileReader(fileName);
        return XMLBitmapUtil.ByteArrayToBitmap(encodePreview);
    }

    private ConfigFileData configFileDataReader(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            byte[] encodeConfigFileData = fileReader(fileName);
            return new ConfigFileData(encodeConfigFileData);
        }
        return null;
    }

    private byte[] fileReader(String fileName) {
        File file = new File(mContext.getFilesDir(), fileName);
        BufferedSource bufferedSource = null;
        try {
            Source source = Okio.source(file);
            bufferedSource = Okio.buffer(source);
            return bufferedSource.readByteArray();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (bufferedSource != null) {
                try {
                    bufferedSource.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private boolean fileDelete(String fileName) {
        File file = new File(mContext.getFilesDir(), fileName);
        return file.delete();
    }

    private long insertConfigDataColumn(String name, String preview, String configdata) {
        ContentValues values = new ContentValues();
        values.put(DataBase.BCONFIG_NAME, name);
        values.put(DataBase.BCONFIG_PREVIEW, preview);
        values.put(DataBase.BCONFIG_DATA, configdata);
        try {
            return syncInsert(DataBase.BCONFIG_TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return DataBase.INVALID_ID;
    }

    private boolean updateConfigDataColumn(String name, String preview, String configdata) {
        ContentValues values = new ContentValues();
        values.put(DataBase.BCONFIG_PREVIEW, preview);
        values.put(DataBase.BCONFIG_DATA, configdata);
        String whereClause = DataBase.BCONFIG_NAME + "=?";
        String[] whereArgs = {name};
        try {
            int count = syncUpdate(DataBase.BCONFIG_TABLE_NAME, values, whereClause, whereArgs);
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private Cursor loadConfigDataColumnPreViewConfigDataByName(String name) {
        String[] columns = {DataBase.BCONFIG_PREVIEW, DataBase.BCONFIG_DATA};
        String selection = "name=\"" + name + "\"";
        try {
            return db.query(DataBase.BCONFIG_TABLE_NAME, columns, selection, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private Cursor loadConfigDataColumnConfigDataByName(String name) {
        String[] columns = {DataBase.BCONFIG_DATA};
        String selection = "name=\"" + name + "\"";
        try {
            return db.query(DataBase.BCONFIG_TABLE_NAME, columns, selection, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private Cursor loadConfigDataColumnNameArray() {
        String[] columns = {DataBase.BCONFIG_NAME};
        try {
            return db.query(DataBase.BCONFIG_TABLE_NAME, columns, null, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private Cursor loadConfigDataColumnPreViewByName(String name) {
        String[] columns = {DataBase.BCONFIG_PREVIEW};
        String selection = "name=\"" + name + "\"";
        try {
            return db.query(DataBase.BCONFIG_TABLE_NAME, columns, selection, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private boolean deleteConfigDataColumnByName(String name) {
        String selection = "name=\"" + name + "\"";
        Cursor c = null;
        try {
            String[] columns = {DataBase.BCONFIG_PREVIEW, DataBase.BCONFIG_DATA};
            c = db.query(DataBase.BCONFIG_TABLE_NAME, columns, selection, null, null, null, null);
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                int previewIndex = c.getColumnIndex(DataBase.BCONFIG_PREVIEW);
                String previewFileName = c.getString(previewIndex);
                fileDelete(previewFileName);
                int dataIndex = c.getColumnIndex(DataBase.BCONFIG_DATA);
                String dataFileName = c.getString(dataIndex);
                fileDelete(dataFileName);
            }
            return syncDelete(DataBase.BCONFIG_TABLE_NAME, selection, null) > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    public int loadBatteryLevel() {
        int batteryLevel = 0;
        Cursor c = null;
        try {
            c = loadBatteryColumnLevel();
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(DataBase.BATTERY_LEVEL);
                batteryLevel = c.getInt(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if ((c != null)) {
                c.close();
            }
        }

        return batteryLevel;
    }

    private static long batteryColumnID = DataBase.INVALID_ID;

    public void saveBatteryLevel(int batteryLevel) {
        if (batteryColumnID == DataBase.INVALID_ID) {
            Cursor c = null;
            try {
                c = loadBatteryColumnId();
                if ((c != null) && (c.getCount() != 0)) {
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(BaseColumns._ID);
                    batteryColumnID = (long) c.getInt(columnIndex);
                    updateBatteryColumn(batteryColumnID, batteryLevel);
                } else {
                    batteryColumnID = insertBatteryColumn(batteryLevel);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            return;
        }
        updateBatteryColumn(batteryColumnID, batteryLevel);
    }

    private long insertBatteryColumn(int batteryLevel) {
        ContentValues values = new ContentValues();
        values.put(DataBase.BATTERY_LEVEL, batteryLevel);
        try {
            return syncInsert(DataBase.BATTERY_TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return DataBase.INVALID_ID;
    }

    private boolean updateBatteryColumn(long id, int batteryLevel) {
        ContentValues values = new ContentValues();
        values.put(DataBase.BATTERY_LEVEL, batteryLevel);
        String whereClause = BaseColumns._ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        try {
            int count = syncUpdate(DataBase.BATTERY_TABLE_NAME, values, whereClause, whereArgs);
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private Cursor loadBatteryColumnLevel() {
        String[] columns = {DataBase.BATTERY_LEVEL};
        try {
            return db.query(DataBase.BATTERY_TABLE_NAME, columns, null, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private Cursor loadBatteryColumnId() {
        String[] columns = {BaseColumns._ID};
        try {
            return db.query(DataBase.BATTERY_TABLE_NAME, columns, null, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public void saveWidget(int widgetId, String name) {
        Cursor c = null;
        try {
            c = loadWidgetColumnNameByWidgetId(widgetId);
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                updateWidgetColumnByWidgetId(widgetId, name);
            } else {
                insertWidgetColumn(widgetId, name);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public int countWidget() {
        int count = 0;
        Cursor c = null;
        try {
            c = countWidgetRecode();
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(DataBase.BCOUNT);
                count = c.getInt(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }

    public void updateDefaultFile() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(mContext);
        if (sharedPreferencesManager != null) {
            SharedPreferencesManager.defaultFileVersion = sharedPreferencesManager.getCurrentDefaultFileVersion();
            if (SharedPreferencesManager.defaultFileVersion == SharedPreferencesManager.currentDefaultFileVersion) {
                sharedPreferencesManager.setNextDefaultFileVersion();
//                Intent intent = new Intent(BaseAppWidgetProvider.ACTION_SET_DEFAULT_CONFIG_DATA);
//                intent.setComponent(new ComponentName(mContext, AppWidget_2_2.class));
//                intent.putExtra(BaseAppWidgetProvider.EXTRA_DEFAULT_FILE_NAME, "8default.zip");
//                ConfigDataBase.defaultFileCount++;
//                mContext.sendBroadcast(intent);
                new InitTask().execute();
            }
        }
    }

    public String loadWidgetNameByWidgetId(int widgetId) {
        String name = null;
        Cursor c = null;
        try {
            c = loadWidgetColumnNameByWidgetId(widgetId);
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(DataBase.WIDGET_NAME);
                name = c.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if ((c != null)) {
                c.close();
            }
        }
        return name;
    }

    public List<String> loadWidgetNameList() {
        ArrayList<String> nameList = new ArrayList<>();
        Cursor c = loadWidgetColumnNameArray();
        try {
            if ((c != null) && (c.getCount() != 0)) {
                while (c.moveToNext()) {
                    int columnIndex = c.getColumnIndex(DataBase.WIDGET_NAME);
                    String name = c.getString(columnIndex);
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return nameList;
    }

    public List<Integer> loadWidgetIDListByName(String name) {
        Cursor c = loadWidgetColumnWidgetIdArrayByName(name);
        ArrayList<Integer> widgetIdList = new ArrayList<>();
        try {
            if ((c != null) && (c.getCount() != 0)) {
                c.moveToFirst();
                while (c.moveToNext()) {
                    int columnIndex = c.getColumnIndex(DataBase.WIDGET_ID);
                    int widget_id = c.getInt(columnIndex);
                    widgetIdList.add(widget_id);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return widgetIdList;
    }

    public ConfigFileData loadConfigFileDataByWidgetId(int widgetId) {
        try {
            String name = loadWidgetNameByWidgetId(widgetId);
            return loadWidgetConfigFileData(name);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public boolean removeWidgetByWidgetId(int widgetId) {
        return deleteWidgetColumnByWidgetId(widgetId);
    }

    public List<String> loadWidgetNameListByType(int type) {
        Cursor c = null;
        try {
            c = loadWidgetColumnNameArray();
            ArrayList<String> nameList = new ArrayList<>();
            if ((c != null) && (c.getCount() != 0)) {
                while (c.moveToNext()) {
                    int columnIndex = c.getColumnIndex(DataBase.WIDGET_NAME);
                    String name = c.getString(columnIndex);
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                }
            }
            return nameList;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    private Map<String, String> itemTypeMap = new HashMap<>();

    private long insertWidgetColumn(int widgetId, String name) {
        ContentValues values = new ContentValues();
        values.put(DataBase.WIDGET_ID, widgetId);
        values.put(DataBase.WIDGET_NAME, name);
        try {
            return syncInsert(DataBase.WIDGET_TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return DataBase.INVALID_ID;
    }

    private boolean updateWidgetColumnByWidgetId(int widgetId, String name) {
        ContentValues values = new ContentValues();
        values.put(DataBase.WIDGET_NAME, name);
        String whereClause = DataBase.WIDGET_ID + "=?";
        String[] whereArgs = {String.valueOf(widgetId)};
        try {
            int count = syncUpdate(DataBase.WIDGET_TABLE_NAME, values, whereClause, whereArgs);
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private Cursor countWidgetRecode() {
        try {
            return db.rawQuery(DataBase.RAW_QUERY_COUNT_WIDGET, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private Cursor loadWidgetColumnNameByWidgetId(int widgetId) {
        String[] columns = {DataBase.WIDGET_NAME};
        String selection = "id=\"" + widgetId + "\"";
        try {
            return db.query(DataBase.WIDGET_TABLE_NAME, columns, selection, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private Cursor loadWidgetColumnNameArray() {
        String[] columns = {DataBase.WIDGET_NAME};
        try {
            return db.query(DataBase.WIDGET_TABLE_NAME, columns, null, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private Cursor loadWidgetColumnWidgetIdArrayByName(String name) {
        String[] columns = {DataBase.WIDGET_ID};
        String selection = "name=\"" + name + "\"";
        try {
            return db.query(DataBase.WIDGET_TABLE_NAME, columns, selection, null, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private boolean deleteWidgetColumnByWidgetId(int widgetId) {
        String whereClause = DataBase.WIDGET_ID + "=?";
        String[] whereArgs = {String.valueOf(widgetId)};
        try {
            int count = db.delete(DataBase.WIDGET_TABLE_NAME, whereClause, whereArgs);
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private String getItemType(ConfigFileData configFileData) {
        String allType = "";
        if (configFileData == null) {
            return allType;
        }
        configFileData.startXmlParse();
        XmlPullParser parser = configFileData.getXmlParser();
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String tag = parser.getName();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            configFileData.completeXmlParse();
        }
        return null;
    }

    private class InitTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            //Util.toastMessageLong(ResourceUtil.getString(0x7f070247));
        }

        private void initDefaultConfigFileDataformAssets(String fileName) {
            Intent intent = new Intent(AppWidget_1_1.ACTION_SET_DEFAULT_CONFIG_DATA);
            intent.setComponent(new ComponentName(mContext, AppWidget_1_1.class));
            intent.putExtra(AppWidget_1_1.EXTRA_DEFAULT_FILE_NAME, fileName);
            ConfigDataBase.defaultFileCount++;
            mContext.sendBroadcast(intent);
        }

        @Override
        protected Void doInBackground(Void... params) {
            initDefaultConfigFileDataformAssets("8default.zip");
            initDefaultConfigFileDataformAssets("9default.zip");
            initDefaultConfigFileDataformAssets("10default.zip");
            initDefaultConfigFileDataformAssets("11default.zip");
            initDefaultConfigFileDataformAssets("12default.zip");
            initDefaultConfigFileDataformAssets("13default.zip");
            initDefaultConfigFileDataformAssets("14default.zip");
            initDefaultConfigFileDataformAssets("15default.zip");
            initDefaultConfigFileDataformAssets("16default.zip");
            initDefaultConfigFileDataformAssets("17default.zip");
            initDefaultConfigFileDataformAssets("18default.zip");
            initDefaultConfigFileDataformAssets("19default.zip");
            initDefaultConfigFileDataformAssets("20default.zip");
            SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(DiyWidgetApplication.getContext());
            if (sharedPreferencesManager != null) {
                sharedPreferencesManager.setNextDefaultFileVersion();
            }
            return null;
        }
    }

    public static class DataBase {

        public static final int DATABASE_VERSION_SHAREDPREFERENCE_TO_DATABASE = 1;

        public static final int BDATABASE_VERSION_BLOB_TO_FILE = 2;

        public static final int BDATABASE_VERSION_DEFAULT_FILE_VER_2 = 3;

        public static final long INVALID_ID = -1L;

        public static final String BDATABASE_NAME = "configdata.db";

        public static final String BCREATE_SQL = "CREATE TABLE configdata(_id integer primary key autoincrement, name text not null unique, preview text not null, data text not null );";

        public static final String BCREATE_SQL1 = "CREATE TABLE battery(_id integer primary key autoincrement, level integer);";

        public static final String BCREATE_SQL2 = "CREATE TABLE widget(id integer primary key,name text not null );";

        public static final String WIDGET_TABLE_NAME = "widget";
        public static final String WIDGET_ID = "id";
        public static final String WIDGET_NAME = "name";

        public static final String BATTERY_TABLE_NAME = "battery";
        public static final String BATTERY_LEVEL = "level";

        public static final String BCONFIG_TABLE_NAME = "configdata";
        public static final String BCONFIG_NAME = "name";
        public static final String BCONFIG_DATA = "data";
        public static final String BCONFIG_PREVIEW = "preview";

        public static final String BCOUNT = "COUNT(*)";

        public static final String RAW_QUERY_COUNT_WIDGET = "select COUNT(*) from widget";
    }

    public class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, DataBase.BDATABASE_NAME, null, DataBase.BDATABASE_VERSION_DEFAULT_FILE_VER_2);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBase.BCREATE_SQL);
            db.execSQL(DataBase.BCREATE_SQL1);
            db.execSQL(DataBase.BCREATE_SQL2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
