package com.nicaiya.diywidget.model;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileData extends ArrayList<ZipFileData.FileData> {

    private static final boolean DEBUG = false;
    private static final String TAG = ZipFileData.class.getSimpleName();

    public static final int FILE_SIGNATURE = 67324752;
    public static final byte FILE_SIGNATURE0 = 80;
    public static final byte FILE_SIGNATURE1 = 75;
    public static final byte FILE_SIGNATURE2 = 3;
    public static final byte FILE_SIGNATURE3 = 4;
    private static final long serialVersionUID = 0x1L;

    public void loadFromAsset(AssetManager manager, String dir) {
        try {
            String[] names = manager.list(dir);
            for (String name : names) {
                try {
                    InputStream is = manager.open(dir + File.separator + name);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = bis.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                    }
                    byte[] bytes = baos.toByteArray();
                    ZipFileData.FileData data = new ZipFileData.FileData(name, bytes);
                    add(data);

                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

        } catch (IOException e) {
        }
    }

    public void loadFromDir(File dir) {
        if (!dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;
                while ((count = bis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }
                String filename = file.getName();
                byte[] bytes = baos.toByteArray();
                ZipFileData.FileData data = new ZipFileData.FileData(filename, bytes);
                add(data);

            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void loadFromInputStream(InputStream is) {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;
                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }
                String filename = ze.getName();
                byte[] bytes = baos.toByteArray();
                ZipFileData.FileData data = new ZipFileData.FileData(filename, bytes);
                add(data);
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                zis.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void loadFromByteArray(byte[] data) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        loadFromInputStream(is);
    }

    public byte[] dumpToByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeToOutPutStream(baos);
        return baos.toByteArray();
    }

    public void writeToOutPutStream(OutputStream os) {
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
        try {
            for (ZipFileData.FileData data : this) {
                ZipEntry ze = new ZipEntry(data.name);
                zos.putNextEntry(ze);
                zos.write(data.data);
                zos.closeEntry();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                zos.close();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public boolean equals(Object obj) {
        return false;
    }

    public static class FileData {

        public String name;
        public byte[] data;

        public FileData(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public byte[] getData() {
            return data;
        }

        public String getDataAsText() {
            return new String(data, Charset.forName("UTF-8"));
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof FileData) {
                FileData other = (FileData) o;
                if (this.name.equals(other.name)) {
                    if (Arrays.equals(this.data, other.data)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
