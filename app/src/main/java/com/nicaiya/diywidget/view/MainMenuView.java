package com.nicaiya.diywidget.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.nicaiya.diywidget.DiyWidgetConfigActivity;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.database.ConfigDataBase;
import com.nicaiya.diywidget.view.adapter.WidgetListAdapter;

/**
 * MainMenuView
 * Created by zhengjie on 16/4/10.
 */
public class MainMenuView extends FrameLayout {

    private ConfigDataBase configDataBase;

    private DiyWidgetConfigActivity activity;

    private WidgetListAdapter widgetListAdapter;

    public MainMenuView(Context context) {
        super(context);
    }

    public MainMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.widget_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        widgetListAdapter = new WidgetListAdapter();
        widgetListAdapter.setOnItemChildViewClickListener(new WidgetListAdapter.OnItemChildViewClickListener() {
            @Override
            public void onChildViewClick(View view, int position, long itemId) {
                switch (view.getId()) {
                    case R.id.add_btn:
                        activity.addWidget(widgetListAdapter.getItem(position));
                        break;
                    case R.id.delete_btn:
                        break;
                    case R.id.edit_btn:
                        break;
                }
            }
        });
        recyclerView.setAdapter(widgetListAdapter);

        setActivity(ResourceUtil.getConfigActivity());
    }

    public void onUpdate() {
        widgetListAdapter.notifyDataSetChangedNew();
    }

    public void setActivity(DiyWidgetConfigActivity activity) {
        this.activity = activity;
        this.configDataBase = activity.getConfigDataBase();
        this.widgetListAdapter.notifyDataSetChangedNew();
    }

    public ConfigDataBase getConfigDataBase() {
        return configDataBase;
    }

}
