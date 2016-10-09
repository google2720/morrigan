package com.morrigan.m.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.WebViewActivity;
import com.squareup.picasso.Picasso;

import java.util.Locale;

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

    public void onClickProductTeam(View view) {
        WebViewActivity.start(this, getString(R.string.about_product_team), "file:///android_asset/product-team.html");
    }

    public void onClickPrivate(View view) {
        WebViewActivity.start(this, getString(R.string.about_private), "file:///android_asset/private.html");
    }

    public void onClickService(View view) {
        WebViewActivity.start(this, getString(R.string.about_service), "file:///android_asset/service.html");
    }
}
