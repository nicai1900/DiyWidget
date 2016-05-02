package com.nicaiya.diywidget;

import android.widget.Toast;

/**
 * Created by zhengjie on 16/4/12.
 */
public class Util {

    public static void toastMessage(String msg) {
        Toast.makeText(DiyWidgetApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastMessageLong(String msg) {
        Toast.makeText(DiyWidgetApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
    }
}
