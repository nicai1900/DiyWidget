package com.nicaiya.diywidget.model;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by zhengjie on 16/3/10.
 */
public class AppData {

    public ComponentName componentName;
    public Drawable icon;
    public ResolveInfo resolveInfo;
    public String label;

    public AppData(ResolveInfo info, PackageManager packageManager) {
        resolveInfo = info;
        label = resolveInfo.loadLabel(packageManager).toString();
        icon = resolveInfo.loadIcon(packageManager);
        componentName = new ComponentName(resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name);
    }

    @Override
    public String toString() {
        return label;
    }
}
