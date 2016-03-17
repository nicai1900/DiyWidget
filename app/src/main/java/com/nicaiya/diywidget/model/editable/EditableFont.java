package com.nicaiya.diywidget.model.editable;

import com.nicaiya.diywidget.font.FontItem;

public interface EditableFont {

    public int STRING_CASE_TYPE_NONE = 0;
    public int STRING_CASE_TYPE_FIRST_UPPER = 1;
    public int STRING_CASE_TYPE_LOWER = 2;
    public int STRING_CASE_TYPE_UPPER = 3;

    public void setFont(FontItem item);

    public FontItem getFont();

    public void setFontColor(int color);

    public int getFontColor();

    public void setStringCaseType(int type);

    public int getStringCaseType();

}