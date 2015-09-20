package com.library_loadinglocalimg.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.library_loadinglocalimg.AlbumHelper;
import com.library_loadinglocalimg.Entity.ImageBucket;
import com.library_loadinglocalimg.Entity.ImageItem;
import com.library_loadinglocalimg.PhotoBucketWindow;
import com.library_loadinglocalimg.R;
import com.library_loadinglocalimg.adapter.ImageGridAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/20.
 */
public class ListImgFragment extends Fragment implements View.OnClickListener, View.OnTouchListener,
        PhotoBucketWindow.OnBucketSelectedListener {
    public final String TAG = getClass().getSimpleName();

    private int maxSize = 9;
    private List<ImageItem> dataList = new ArrayList<ImageItem>();
    private GridView gridView;
    private ImageGridAdapter adapter;
    private AlbumHelper helper;
    private PhotoBucketWindow bucketWindow;
    private int mScreenWidth;
    private int mScreenHeight;
    private Button chooseBucketBtn;
    private ImageView bottomIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_select_pics, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        setUpView();
    }

    private void initView(View view) {
        view.findViewById(R.id.select_pics_topcontaner).setVisibility(View.GONE);
        gridView = (GridView) view.findViewById(R.id.select_pics_grid);
        gridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        chooseBucketBtn = (Button) view.findViewById(R.id.select_pics_bucket);
        bottomIcon = (ImageView) view.findViewById(R.id.select_pics_bucket_icon);
    }

    private void setUpView() {
        helper = AlbumHelper.getHelper();
        helper.init(getActivity());
        DisplayMetrics outMetrics = getResources().getDisplayMetrics();
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        List<ImageBucket> list = helper.getImageBucketList(false);
        bucketWindow = new PhotoBucketWindow(getActivity(), chooseBucketBtn, list,
                mScreenWidth, (int) (mScreenHeight * 0.618));
        bucketWindow.setSelectedListener(this);
        dataList.clear();
        dataList.addAll(helper.getAllImageList(true));
        adapter = new ImageGridAdapter(getActivity(), dataList, getResources().getDisplayMetrics().widthPixels / 3);
        adapter.setMaxSelect(maxSize);
        gridView.setAdapter(adapter);
        adapter.setResultCallBack(new ImageGridAdapter.ResultCallBack() {
            @Override
            public void onListen(ArrayList<String> result) {
                if (listener != null)
                    listener.onSelect(result);
            }
        });
        chooseBucketBtn.setOnClickListener(this);
        chooseBucketBtn.setOnTouchListener(this);
    }

    @Override
    public void onBucketSelected(List<ImageItem> imageList) {
        dataList.clear();
        dataList.addAll(imageList);
        gridView.smoothScrollToPosition(0);
        adapter.refresh();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.select_pics_bucket) {
            if (!bucketWindow.isShowing())
                bucketWindow.show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (id == R.id.select_pics_bucket) {
                chooseBucketBtn.setTextColor(getResources().getColor(R.color.dark_gray));
                bottomIcon.setImageResource(R.drawable.ic_bottom_mark_press);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (id == R.id.select_pics_bucket) {
                bottomIcon.setImageResource(R.drawable.ic_bottom_mark_normal);
                chooseBucketBtn.setTextColor(getResources().getColor(R.color.gray));
                if (bucketWindow.isShowing())
                    bucketWindow.dismiss();
            }
        }
        return false;
    }

    private OnResultListener listener;

    public interface OnResultListener {
        void onSelect(ArrayList<String> result);
    }

    public void setOnResultListener(OnResultListener listener) {
        this.listener = listener;
    }

    public static ListImgFragment newInstance() {
        ListImgFragment fragment = new ListImgFragment();
        return fragment;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
