package com.nicaiya.diywidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nicaiya.diywidget.database.ConfigDataBase;
import com.nicaiya.diywidget.model.ConfigFileData;
import com.nicaiya.diywidget.provider.AppWidget_1_1;
import com.nicaiya.diywidget.view.MainMenuView;

import java.util.List;

public class DiyWidgetConfigActivity extends AppCompatActivity {

    private static final boolean DEG = BuildConfig.DEBUG;
    private static final String TAG = DiyWidgetConfigActivity.class.getSimpleName();

    private ConfigDataBase configDataBase;
    private int appWidgetId;

    private MainMenuView mainMenuView;
    private View editorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ResourceUtil.setConfigActivity(this);
        configDataBase = DiyWidgetApplication.getInstance().getConfigDataBase();
        configDataBase.updateDefaultFile();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onNewIntent(getIntent());
    }

    public ConfigDataBase getConfigDataBase() {
        return configDataBase;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                configDataBase.saveWidget(appWidgetId, "Trim metal");
//                requestUpdateWidgetId(appWidgetId);
//                Intent resultValue = new Intent();
//                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//                setResult(RESULT_OK, resultValue);
//                finish();
//            }
//        });


//        PreviewWidgetView previewWidgetView = (PreviewWidgetView) findViewById(R.id.widget_preview);
//        ConfigFileData configFileData = new ConfigFileData(getAssets(), "8default");
//        WidgetData widgetData = WidgetData.createFromConfigFileData(configFileData);
//        previewWidgetView.init(widgetData);
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

    @Override
    protected void onStart() {
        super.onStart();
        DiyWidgetApplication.getInstance().getFontManager().loadFont();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ResourceUtil.setConfigActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                ConfigFileData configFileData = configDataBase.loadConfigFileDataByWidgetId(appWidgetId);
                if (configFileData != null) {
                    configDataBase.setLastConfigFileData(configFileData);
                    //showEditorView(WidgetData.createFromConfigFileData(configFileData), "addObjectMessageEdit");
                    return;
                }
                showMainView();
            }
        }
        if (editorView != null) {
            editorView = null;
        }
        if (mainMenuView != null) {
            mainMenuView.removeAllViews();
            mainMenuView = null;
        }
        showMainView();
    }

    private void showMainView() {
        fragmentReplace("main_list");
    }

    private void fragmentReplace(String reqNewFragmentView) {
        Fragment newFragment = null;
        //currentViewStatus = reqNewFragmentView;
        if (reqNewFragmentView.equals("main_list")) {
            if (mainMenuView != null) {
                setCurrentView(mainMenuView);
                return;
            }
            newFragment = new ListFragment();
        } else if (reqNewFragmentView.equals("main_edit")) {
            if (editorView != null) {
                setCurrentView(editorView);
                //editorView.onUpdateBgColor();
                return;
            }
            newFragment = new EditFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, newFragment);
        transaction.commitAllowingStateLoss();
    }

    public void setCurrentView(View view) {
        if (view instanceof MainMenuView) {
            view.setVisibility(View.VISIBLE);
            if (editorView != null) {
                editorView.setVisibility(View.GONE);
            }
        }
    }

    public MainMenuView getMainMenuView() {
        return mainMenuView;
    }


    public void requestUpdateWidgetByName(String name) {
        List<Integer> widgetIdList = configDataBase.loadWidgetIDListByName(name);
        for (Integer id : widgetIdList) {
            requestUpdateWidgetId(id);
        }
    }

    public void requestUpdateWidgetId(int appWidgetId) {
        Intent intent = new Intent(this, AppWidget_1_1.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] appWidgetIds = {appWidgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);
    }

}
