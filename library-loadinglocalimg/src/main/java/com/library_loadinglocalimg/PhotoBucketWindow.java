package com.library_loadinglocalimg;

import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.library_loadinglocalimg.Entity.ImageBucket;
import com.library_loadinglocalimg.Entity.ImageItem;

/**
 * 相册列表
 */
public class PhotoBucketWindow extends PopupWindow implements
        OnItemClickListener {
    private final int[] location;
    private Context context;
    private View view;
    private View anchor;
    private ListView listView;
    private List<ImageBucket> bucketList;
    private OnBucketSelectedListener selectedListener;
    private ImageBucket selectedBucket;

    public void setSelectedListener(OnBucketSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }


    @SuppressLint("InflateParams")
    public PhotoBucketWindow(Context context, final View anchor,
                             List<ImageBucket> bucketList, int width,
                             int height) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.common_listview, null);
        listView = (ListView) view.findViewById(R.id.common_listview_lv);
        setContentView(view);
        setWidth(width);
        setHeight(height);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(android.R.style.Animation_InputMethod);
        this.anchor = anchor;
        getContentView().measure(0, 0);
        location = new int[2];
        anchor.getLocationOnScreen(location);
        this.bucketList = bucketList;
        this.context = context;
        ImageBucketAdapter adapter = new ImageBucketAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        selectedBucket = bucketList.get(0);
        //必须设置背景才能监听pop的点击事件
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (y > getHeight()) {//pop点击底部无效，点击事件拦截
                    if (x < anchor.getWidth())//点击我的图片时有效
                        return anchor.dispatchTouchEvent(event);
                    else
                        return true;
                }
                return false;
            }
        });
    }

    public void show() {
        showAtLocation(anchor, Gravity.BOTTOM, 0, anchor.getHeight());
    }

    class ImageBucketAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (bucketList != null)
                return bucketList.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageBucket bucket = bucketList.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.img_bucket_item, null);
            }

            ImageView imageView = (ImageView) convertView
                    .findViewById(R.id.img_bucket_item_img);
            ImageView imageView2 = (ImageView) convertView
                    .findViewById(R.id.img_bucket_item_choose_flag);
            if (bucket.isSelected)
                imageView2.setImageResource(R.drawable.ic_selected);
            else
                imageView2.setImageBitmap(null);

            TextView tv1 = (TextView) convertView
                    .findViewById(R.id.img_bucket_item_name);
            TextView tv2 = (TextView) convertView
                    .findViewById(R.id.img_bucket_item_num);
            tv1.setText(bucket.bucketName);
            tv2.setText(bucket.count + "张");
            //TODO 加载图片
            LocalImageLoader.getInstance().loadImage(bucket.imageList.get(0).imagePath, imageView);
            return convertView;
        }

    }


    public interface OnBucketSelectedListener {
        public void onBucketSelected(List<ImageItem> imageList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ImageBucket curBucket = bucketList.get(position);
        ImageView imageView2 = (ImageView) view
                .findViewById(R.id.img_bucket_item_choose_flag);
        List<ImageItem> imageList = curBucket.imageList;
        if (selectedListener != null) {
            selectedListener.onBucketSelected(imageList);
        }
        imageView2.setImageResource(R.drawable.ic_selected);
        curBucket.isSelected = true;
        if (curBucket != selectedBucket)
            selectedBucket.isSelected = false;
        selectedBucket = curBucket;
        dismiss();
    }

    @Override
    public boolean isAboveAnchor() {
        return super.isAboveAnchor();
    }
}
