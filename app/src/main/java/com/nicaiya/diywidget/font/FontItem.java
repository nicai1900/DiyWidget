package com.nicaiya.diywidget.font;

import android.graphics.Typeface;

/**
 * Created by zhengjie on 16/3/9.
 */
public class FontItem {

    public String name;
    public Typeface typeface;

    public FontItem(String name, Typeface typeface) {
        this.name = name;
        this.typeface = typeface;
    }

    @Override
    public String toString() {
        return name;
    }
}
