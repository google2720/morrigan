package com.morrigan.m.goal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;

public class GoalActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
    }
}
