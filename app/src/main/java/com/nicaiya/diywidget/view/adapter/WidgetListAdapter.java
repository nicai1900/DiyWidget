package com.nicaiya.diywidget.view.adapter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nicaiya.diywidget.DiyWidgetApplication;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.database.ConfigDataBase;

import java.util.List;

/**
 * WidgetListAdapter
 * Created by zhengjie on 16/4/10.
 */
public class WidgetListAdapter extends RecyclerView.Adapter<WidgetListAdapter.ViewHolder> {

    public static final int CACHE_SIZE = 10 * 1024 * 1025;

    List<String> configNameList;
    List<String> widgetNameList;

    int prevCount;
    int prevSortType;
    int sortType;

    private ConfigDataBase configDataBase;

    final LruCache<String, Bitmap> preViewBitmapCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
        @Override
        protected Bitmap create(String key) {
            try {
                return configDataBase.loadWidgetConfigPreViewBitmap(key);
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (oldValue != null && !oldValue.isRecycled()) {
                oldValue.recycle();
            }
        }
    };

    public WidgetListAdapter() {
        sortType = 0;
        prevSortType = -1;
        prevCount = -1;
        configDataBase = DiyWidgetApplication.getInstance().getConfigDataBase();
    }

    public void notifyDataSetChangedNew() {
        super.notifyDataSetChanged();
        synchronized (preViewBitmapCache) {
            preViewBitmapCache.evictAll();
        }
        prevCount = -1;
        prevSortType = -1;
        widgetNameList = configDataBase.loadWidgetNameListByType(sortType);
    }

    public void notifySortTypeChanged(int type) {
        sortType = type;
        super.notifyDataSetChanged();
        synchronized (preViewBitmapCache) {
            preViewBitmapCache.evictAll();
        }
        widgetNameList = configDataBase.loadWidgetNameListByType(sortType);
        configNameList = configDataBase.loadWidgetConfigNameListByType(sortType);
    }

    @Override
    public WidgetListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_preview_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mWidgetNameTv.setText(configNameList.get(position));

        PreViewItemModel preViewItemModel = new PreViewItemModel(configNameList.get(position), holder);
        new PreViewLoadTask(this).execute(preViewItemModel);
    }

    @Override
    public int getItemCount() {
        if (configDataBase == null) {
            return 0;
        }
        if (prevSortType != sortType) {
            widgetNameList = configDataBase.loadWidgetNameListByType(sortType);
            if (prevCount != widgetNameList.size()) {
                configNameList = configDataBase.loadWidgetConfigNameListByType(sortType);
            }
        }
        prevCount = widgetNameList.size();
        return configNameList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mPreviewWidgetView;
        ProgressBar mProgressBar;
        TextView mWidgetNameTv;
        TextView mWidgetAddTv;
        TextView mWidgetEditTv;
        TextView mWidgetDeleteTv;

        public ViewHolder(View v) {
            super(v);
            mPreviewWidgetView = (ImageView) v.findViewById(R.id.widget_preview);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mWidgetNameTv = (TextView) v.findViewById(R.id.widget_name);
            mWidgetAddTv = (TextView) v.findViewById(R.id.add);
            mWidgetEditTv = (TextView) v.findViewById(R.id.edit);
            mWidgetDeleteTv = (TextView) v.findViewById(R.id.delete);
        }
    }


    static class PreViewItemModel {
        public ViewHolder viewHolder;
        public String name;
        public Bitmap previewBitmap;

        public PreViewItemModel(String name, ViewHolder viewHolder) {
            this.name = name;
            this.viewHolder = viewHolder;
        }
    }

    static class PreViewLoadTask extends AsyncTask<PreViewItemModel, Integer, PreViewItemModel> {

        private WidgetListAdapter widgetListAdapter;

        public PreViewLoadTask(WidgetListAdapter listAdapter) {
            this.widgetListAdapter = listAdapter;
        }

        @Override
        protected PreViewItemModel doInBackground(PreViewItemModel[] models) {
            WidgetListAdapter.PreViewItemModel model = models[0];
            synchronized (widgetListAdapter.preViewBitmapCache) {
                model.previewBitmap = widgetListAdapter.preViewBitmapCache.get(model.name);
            }
            return model;
        }

        protected void onPostExecute(WidgetListAdapter.PreViewItemModel model) {
            if ((model == null) || (model.previewBitmap == null) || (model.previewBitmap.isRecycled()) || (model.viewHolder == null)) {
                return;
            }
            model.viewHolder.mPreviewWidgetView.setImageBitmap(model.previewBitmap);
            model.viewHolder.mPreviewWidgetView.setVisibility(View.VISIBLE);
            model.viewHolder.mProgressBar.setVisibility(View.GONE);
        }
    }

}
