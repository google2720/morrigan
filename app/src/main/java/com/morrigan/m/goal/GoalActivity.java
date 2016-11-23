package com.morrigan.m.goal;

import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.view.View;

import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.c.UserController;
import com.morrigan.m.personal.PersonalModifyTask;

public class GoalActivity extends ToolbarActivity {

    private GoalView goalView;
    private String defaultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        goalView = (GoalView) findViewById(R.id.goal);
        defaultValue = UserController.getInstance().getTarget(this);
        int value = 0;
        try {
            value = Integer.parseInt(defaultValue);
        } catch (Exception e) {
            // ignore
        }
        goalView.setValue(value);
    }

    public void onClickComplete(View view) {
        final String value = String.valueOf(goalView.getValue());
        if (defaultValue == null || value.equals(defaultValue)) {
            finish();
            return;
        }
        PersonalModifyTask task = new PersonalModifyTask(this, "target", value, new Runnable() {
            @Override
            public void run() {
                UserController.getInstance().setTarget(GoalActivity.this, value);
                finish();
            }
        });
        AsyncTaskCompat.executeParallel(task);
    }
}
