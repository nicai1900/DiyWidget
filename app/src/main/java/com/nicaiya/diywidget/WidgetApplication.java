package com.nicaiya.diywidget;

import android.app.Application;

import com.nicaiya.diywidget.font.FontManager;

/**
 * Created by zhengjie on 16/3/16.
 */
public class WidgetApplication extends Application {

    private static WidgetApplication sInstance;

    public static WidgetApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ResourceUtil.setContext(this);
    }

    public FontManager getFontManager() {
        return FontManager.getInstance(this);
    }
}
