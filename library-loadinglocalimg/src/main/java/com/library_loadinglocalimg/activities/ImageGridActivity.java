package com.library_loadinglocalimg.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.library_loadinglocalimg.AlbumHelper;
import com.library_loadinglocalimg.Entity.ImageBucket;
import com.library_loadinglocalimg.Entity.ImageItem;
import com.library_loadinglocalimg.PhotoBucketWindow;
import com.library_loadinglocalimg.R;
import com.library_loadinglocalimg.adapter.ImageGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageGridActivity extends Activity implements OnClickListener, View.OnTouchListener,
        PhotoBucketWindow.OnBucketSelectedListener {
    public final String TAG = getClass().getSimpleName();

    public static final String RESULT_KEY = "result_key";
    private List<ImageItem> dataList = new ArrayList<ImageItem>();
    private GridView gridView;
    private ImageGridAdapter adapter;
    private AlbumHelper helper;
    private PhotoBucketWindow bucketWindow;
    private int mScreenWidth;
    private int mScreenHeight;
    //topBar
    private ImageView backBtn;
    private TextView titleTv;
    private Button sendBtn;

    private Button chooseBucketBtn;
    private ImageView bottomIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_pics);
        initView();
        setUpView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.topbar_back);
        titleTv = (TextView) findViewById(R.id.topbar_title);
        sendBtn = (Button) findViewById(R.id.topbar_right);
        gridView = (GridView) findViewById(R.id.select_pics_grid);
        gridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        chooseBucketBtn = (Button) findViewById(R.id.select_pics_bucket);
        bottomIcon = (ImageView) findViewById(R.id.select_pics_bucket_icon);
    }

    private void setUpView() {
        setTopBar();
        helper = AlbumHelper.getHelper();
        helper.init(this);
        DisplayMetrics outMetrics = getResources().getDisplayMetrics();
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        List<ImageBucket> list = helper.getImageBucketList(false);
        bucketWindow = new PhotoBucketWindow(this, chooseBucketBtn, list,
                mScreenWidth, (int) (mScreenHeight * 0.618));
        bucketWindow.setSelectedListener(this);
        dataList.clear();
        dataList.addAll(helper.getAllImageList(true));
        adapter = new ImageGridAdapter(this, dataList, getResources().getDisplayMetrics().widthPixels / 3);
        adapter.setMaxSelect(getIntent().getIntExtra("maxSize", 9));
        gridView.setAdapter(adapter);
        adapter.setResultCallBack(new ImageGridAdapter.ResultCallBack() {
            @Override
            public void onListen(ArrayList<String> result) {
                if (result.size() > 0) {
                    if (!sendBtn.isEnabled()) {
                        sendBtn.setEnabled(true);
                        sendBtn.setBackgroundResource(R.drawable.btn_bg_choose);
                        sendBtn.setTextColor(getResources().getColor(R.color.white));
                    }
                    sendBtn.setText("发送(" + result.size() + "/" + adapter.getMaxSize() + ")");
                } else {
                    sendBtn.setText("发送");
                    sendBtn.setBackgroundResource(R.drawable.btn_bg_normal);
                    if (sendBtn.isEnabled())
                        sendBtn.setEnabled(false);
                    sendBtn.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        });
        chooseBucketBtn.setOnClickListener(this);
        chooseBucketBtn.setOnTouchListener(this);
    }

    private void setTopBar() {
        titleTv.setText("选择图片");
        backBtn.setImageResource(R.drawable.ic_back_press);
        sendBtn.setBackgroundResource(R.drawable.btn_bg_normal);
        sendBtn.setEnabled(false);
        sendBtn.setText("发送");
        sendBtn.setTextColor(getResources().getColor(R.color.gray));
        backBtn.setOnTouchListener(this);
        backBtn.setOnClickListener(this);
        sendBtn.setOnTouchListener(this);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.select_pics_bucket) {
            if (!bucketWindow.isShowing())
                bucketWindow.show();
        } else if (id == R.id.topbar_back) {
            setResult(RESULT_OK);
            finish();
        } else if (id == R.id.topbar_right) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_KEY, adapter.getSelectResults());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (id == R.id.topbar_back)
                backBtn.setImageResource(R.drawable.ic_back_press);
            else if (id == R.id.topbar_right && sendBtn.isEnabled()) {
                sendBtn.setBackgroundResource(R.drawable.btn_bg_press);
                sendBtn.setTextColor(getResources().getColor(R.color.white));
            } else if (id == R.id.select_pics_bucket) {
                chooseBucketBtn.setTextColor(getResources().getColor(R.color.dark_gray));
                bottomIcon.setImageResource(R.drawable.ic_bottom_mark_press);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (id == R.id.topbar_back)
                backBtn.setImageResource(R.drawable.ic_back_normal);
            else if (id == R.id.topbar_right && sendBtn.isEnabled()) {
                sendBtn.setTextColor(getResources().getColor(R.color.white));
                sendBtn.setBackgroundResource(R.drawable.btn_bg_choose);
            } else if (id == R.id.select_pics_bucket) {
                bottomIcon.setImageResource(R.drawable.ic_bottom_mark_normal);
                chooseBucketBtn.setTextColor(getResources().getColor(R.color.gray));
                if (bucketWindow.isShowing())
                    bucketWindow.dismiss();
            }
        }
        return false;
    }


    /**
     * 跳转到图片列表页面,并将结果返回给前一个页面，
     * 跳转使用 startActivityForResult
     *
     * @param activity
     * @param requestCode 请求码
     */

    public static void actionIntent(Activity activity, int requestCode, int maxSize) {
        Intent intent = new Intent(activity, ImageGridActivity.class);
        intent.putExtra("maxSize", maxSize);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void actionIntent(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, ImageGridActivity.class), requestCode);
    }

    @Override
    public void onBucketSelected(List<ImageItem> imageList) {
        dataList.clear();
        dataList.addAll(imageList);
        gridView.smoothScrollToPosition(0);
        adapter.refresh();
    }
}
