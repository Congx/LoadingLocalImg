package com.loadlocalimgsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.library_loadinglocalimg.activities.ImageGridActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_btn) {
            ImageGridActivity.actionIntent(this, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra(ImageGridActivity.RESULT_KEY);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
