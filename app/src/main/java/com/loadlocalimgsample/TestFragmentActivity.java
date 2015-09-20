package com.loadlocalimgsample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.library_loadinglocalimg.activities.ListImgFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/20.
 */
public class TestFragmentActivity extends FragmentActivity implements ListImgFragment.OnResultListener {
    private FragmentManager fm;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tv = (TextView) findViewById(R.id.tv);
        fm = getSupportFragmentManager();
        ListImgFragment fragment = ListImgFragment.newInstance();
        fragment.setOnResultListener(this);
        fragment.setMaxSize(5);
        fm.beginTransaction().replace(R.id.content, fragment).commit();
    }
    @Override
    public void onSelect(ArrayList<String> result) {
        tv.setText(result.size()+"");
    }
}
