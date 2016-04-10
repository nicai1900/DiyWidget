package com.nicaiya.diywidget.font;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;

import com.nicaiya.diywidget.BuildConfig;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengjie on 16/3/9.
 */
public class FontManager implements Comparator<FontItem>, FilenameFilter {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = FontManager.class.getSimpleName();

    public static final String ROBOTOCONDENSED_BOLD_NAME = "RobotoCondensed-Bold";
    public static final String ROBOTO_LIGHT_NAME = "Roboto-Light";
    public static final String ROBOTO_REGULAR_NAME = "Roboto-Regular";
    public static final String ROBOTO_THIN_NAME = "Roboto-Thin";

    public static final String MONOSPACE_NAME = "MONOSPACE (Android Default)";
    public static final String SANS_SERIF_NAME = "SANS (Android Default)";
    public static final String SERIF_NAME = "SERIF (Android Default)";

    public static FontItem ROBOTOCONDENSED_BOLD = null;
    public static FontItem ROBOTO_LIGHT = null;
    public static FontItem ROBOTO_REGULAR = null;
    public static FontItem ROBOTO_THIN = null;

    public static final FontItem SERIF = new FontItem(SERIF_NAME, Typeface.SERIF);
    public static final FontItem SANS_SERIF = new FontItem(SANS_SERIF_NAME, Typeface.SANS_SERIF);
    public static final FontItem MONOSPACE = new FontItem(MONOSPACE_NAME, Typeface.MONOSPACE);

    private final List<FontItem> fontItemList = new ArrayList<>();
    private final Map<String, FontItem> typefaceMap = new Hashtable<>();
    private static final List<FontManager.Listener> listenerList = new ArrayList<>();

    public static FontManager sInstance;

    public static FontManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (FontManager.class) {
                if (sInstance == null) {
                    sInstance = new FontManager(context);
                }
            }
        }
        return sInstance;
    }

    private FontManager(Context context) {
        ROBOTOCONDENSED_BOLD = new FontItem(ROBOTOCONDENSED_BOLD_NAME, Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Bold.ttf"));
        ROBOTO_LIGHT = new FontItem(ROBOTO_LIGHT_NAME, Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
        ROBOTO_THIN = new FontItem(ROBOTO_THIN_NAME, Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf"));
        ROBOTO_REGULAR = new FontItem(ROBOTO_REGULAR_NAME, Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
        loadFont();
    }

    public void loadFont() {
        fontItemList.clear();
        typefaceMap.clear();
        loadFontFile(new File(Environment.getRootDirectory() + "/fonts"));
        loadFontFile(new File(Environment.getExternalStorageDirectory().getPath() + "/fonts"));
        for (FontItem fontItem : typefaceMap.values()) {
            fontItemList.add(fontItem);
        }
        Collections.sort(fontItemList, this);

        fontItemList.add(0, ROBOTO_THIN);
        typefaceMap.put(ROBOTO_THIN.name, ROBOTO_THIN);
        fontItemList.add(0, ROBOTO_LIGHT);
        typefaceMap.put(ROBOTO_LIGHT.name, ROBOTO_LIGHT);
        fontItemList.add(0, ROBOTOCONDENSED_BOLD);
        typefaceMap.put(ROBOTOCONDENSED_BOLD.name, ROBOTOCONDENSED_BOLD);
        fontItemList.add(0, ROBOTO_REGULAR);
        typefaceMap.put(ROBOTO_REGULAR.name, ROBOTO_REGULAR);
        fontItemList.add(0, MONOSPACE);
        typefaceMap.put(MONOSPACE.name, MONOSPACE);
        fontItemList.add(0, SANS_SERIF);
        typefaceMap.put(SANS_SERIF.name, SANS_SERIF);
        fontItemList.add(0, SERIF);
        typefaceMap.put(SERIF.name, SERIF);

        for (FontManager.Listener listener : listenerList) {
            listener.onUpdate();
        }
    }

    private void loadFontFile(File dir) {
        // :( Parsing error. Please contact me.
    }

    public List getFontItemList() {
        return fontItemList;
    }

    public FontItem getFontItem(String name) {
        return (FontItem) typefaceMap.get(name);
    }

    public FontItem getDefaultFontItem() {
        return SANS_SERIF;
    }

    public void addListener(FontManager.Listener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeListener(FontManager.Listener listener) {
        listenerList.remove(listener);
    }

    @Override
    public int compare(FontItem lhs, FontItem rhs) {
        return lhs.name.compareTo(rhs.name);
    }

    @Override
    public boolean accept(File dir, String filename) {
        String name = filename.toLowerCase();
        return name.endsWith(".ttf") || name.endsWith(".otf");
    }

    public interface Listener {
        void onUpdate();
    }
}
