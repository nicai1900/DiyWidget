package com.nicaiya.diywidget.model.editable;

import android.graphics.Bitmap;

public interface EditableFillStyle {

    public void setAlpha(int alpha);

    public int getAlpha();

    public void setColor(int color);

    public int getColor();

    public void setBitmap(Bitmap bitmap);

    public Bitmap getBitmap();

}