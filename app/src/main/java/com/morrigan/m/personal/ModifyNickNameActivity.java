package com.morrigan.m.personal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.morrigan.m.R;
import com.morrigan.m.Toolbar2Activity;
import com.morrigan.m.c.UserController;

public class ModifyNickNameActivity extends Toolbar2Activity {

    private EditText editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_name);
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editView.setText(null);
            }
        });
        editView = (EditText) findViewById(R.id.edit);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmView.setEnabled(s.length() > 0);
            }
        });
        editView.setText(UserController.getInstance().getNickname(this));
        editView.setSelection(editView.getText().length());
    }

    @Override
    protected void onClickConfirm() {
        String nickname = editView.getText().toString().trim();
        UserController.getInstance().setNickname(this, nickname);
        finish();
    }
}
