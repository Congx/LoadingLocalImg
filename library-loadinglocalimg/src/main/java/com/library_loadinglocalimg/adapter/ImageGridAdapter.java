package com.library_loadinglocalimg.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.library_loadinglocalimg.Entity.ImageItem;
import com.library_loadinglocalimg.LocalImageLoader;
import com.library_loadinglocalimg.R;

import java.util.ArrayList;
import java.util.List;


public class ImageGridAdapter extends BaseAdapter {
    final String FILTER_COLOR = "#88000000";
    private int maxSize = 9;
    private ResultCallBack resultCallBack = null;
    // 选择后的图片的路径集合
    private ArrayList<String> selectPicPaths = new ArrayList<String>();
    private List<ImageItem> dataList;// 数据源
    private Context context;
    private RelativeLayout.LayoutParams lp = null;

    public void setMaxSelect(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public interface ResultCallBack {
        public void onListen(ArrayList<String> result);
    }

    public ArrayList<String> getSelectResults() {
        return selectPicPaths;
    }

    public void setSelectResultsAndRefresh(ArrayList<String> result) {
        this.selectPicPaths.addAll(result);
        refresh();
    }

    public void setResultCallBack(ResultCallBack resultCallBack) {
        this.resultCallBack = resultCallBack;
    }

    public ImageGridAdapter(Context context, List<ImageItem> dataList, int imgSize) {
        this.dataList = dataList;
        this.context = context;
        lp = new RelativeLayout.LayoutParams(imgSize, imgSize);
    }

    public void refresh() {
        initDataList();
        notifyDataSetChanged();
    }

    //TODO 优化
    private void initDataList() {
        int size = selectPicPaths.size();
        for (ImageItem img : dataList) {
            for (int i = 0; i < size; i++) {
                if ((img.imagePath != null && img.imagePath
                        .equals(selectPicPaths.get(i)))) {
                    img.isSelected = true;
                    break;
                }
            }
        }
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ImageItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final ImageItem item = dataList.get(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.gridview_select_pic_item, parent, false);
            holder.iv = (ImageView) convertView
                    .findViewById(R.id.select_pic_item_iv_img);
            holder.selected = (ImageView) convertView
                    .findViewById(R.id.select_pic_item_iv_check);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.iv.setLayoutParams(lp);
        if (item.isSelected) {
            holder.selected.setImageResource(R.drawable.ic_select_yes);
            //设置选中变暗
            holder.iv.setColorFilter(Color.parseColor(FILTER_COLOR));
        } else {
            holder.selected.setImageResource(R.drawable.ic_select_no);
            holder.iv.setColorFilter(null);
        }
        holder.iv.setImageResource(R.drawable.default_img);
        LocalImageLoader.getInstance().loadImage(item.imagePath, holder.iv);
        holder.iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isSelected) {
                    holder.selected.setImageResource(R.drawable.ic_select_no);
                    selectPicPaths.remove(item.imagePath);
                    holder.iv.setColorFilter(null);
                } else {
                    if (selectPicPaths.size() >= maxSize) {
                        Toast.makeText(context, "最多选择" + maxSize + "张图片", Toast.LENGTH_SHORT).show();
                    } else {
                        holder.selected.setImageResource(R.drawable.ic_select_yes);
                        holder.iv.setColorFilter(Color.parseColor(FILTER_COLOR));
                        selectPicPaths.add(item.imagePath);
                    }
                }
                item.isSelected = !item.isSelected;
                if (resultCallBack != null)
                    resultCallBack.onListen(selectPicPaths);
            }
        });
        return convertView;
    }


    final static class Holder {
        private ImageView iv;
        private ImageView selected;
    }

}
