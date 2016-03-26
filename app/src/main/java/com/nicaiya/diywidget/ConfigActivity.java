package com.nicaiya.diywidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.database.ConfigDataBase;

import java.util.List;

public class ConfigActivity extends AppCompatActivity {

    private static final boolean DEG = false;
    private static final String TAG = ConfigActivity.class.getSimpleName();

    private ConfigDataBase configDataBase;
    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        configDataBase = WidgetApplication.getInstance().getConfigDataBase();
        configDataBase.updateDefaultFile();
        Button button = (Button) findViewById(R.id.add_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configDataBase.saveWidget(appWidgetId, "Trim metal");
                requestUpdateWidgetId(appWidgetId);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        handleIntent(getIntent());
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

    private void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                ConfigFileData configFileData = configDataBase.loadConfigFileDataByWidgetId(appWidgetId);
                if (configFileData != null) {
                    configDataBase.setLastConfigFileData(configFileData);
                    // TODO: 16/3/26 show edit view
                } else {

                }
            }
        }
    }

    public void requestUpdateWidgetByName(String name) {
        List<Integer> widgetIdList = configDataBase.loadWidgetIDListByName(name);
        for (Integer id : widgetIdList) {
            requestUpdateWidgetId(id);
        }
    }

    public void requestUpdateWidgetId(int appWidgetId) {
        Intent intent = new Intent(this, BaseAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] appWidgetIds = {appWidgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);
    }


}
