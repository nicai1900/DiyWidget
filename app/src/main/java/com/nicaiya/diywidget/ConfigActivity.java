package com.nicaiya.diywidget;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.model.object.WidgetData;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = ConfigActivity.class.getSimpleName();
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.widget_img);

        ConfigFileData configFileData = new ConfigFileData(getAssets(), "8default");
//        ConfigFileData configFileData = null;
//        try {
//            configFileData = new ConfigFileData(getAssets().open("12default.zip"));
//        } catch (IOException e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
        WidgetData widgetData = WidgetData.createFromXmlPullParser(configFileData);
        if (widgetData != null) {
            Bitmap bmp = widgetData.createPreViewBitmap();
            mImageView.setImageBitmap(bmp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
