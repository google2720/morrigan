package com.morrigan.m.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;
import com.squareup.picasso.Picasso;

/**
 * 关于界面
 * Created by y on 2016/10/2.
 */
public class AboutActivity extends ToolbarActivity {

    private ImageView aboutTopView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        aboutTopView = (ImageView) findViewById(R.id.aboutTop);
        Picasso.with(this).load(R.drawable.about_top).into(aboutTopView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelRequest(aboutTopView);
        aboutTopView.setImageDrawable(null);
    }
}
