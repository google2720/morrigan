package com.morrigan.m.personal;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.ItemBean;
import com.github.yzeaho.common.BitmapCompress;
import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.file.Closeables;
import com.github.yzeaho.file.FileApi;
import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.UiResult;
import com.morrigan.m.UserController;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * 我的资料界面
 * Created by y on 2016/10/4.
 */
public class PersonalActivity extends ToolbarActivity implements SelectAvatarPopWindow.Listener {

    private static final String TAG = "Personal";
    private static final int REQUEST_CODE_PICK_PHOTO = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private TextView heightView;
    private TextView weightView;
    private TextView emotionView;
    private TextView ageView;
    private TextView nicknameView;
    private File captureOutFile;
    private ImageView avatarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        avatarView = (ImageView) findViewById(R.id.avatar);
        nicknameView = (TextView) findViewById(R.id.nickname);
        ageView = (TextView) findViewById(R.id.age);
        emotionView = (TextView) findViewById(R.id.emotion);
        heightView = (TextView) findViewById(R.id.height);
        weightView = (TextView) findViewById(R.id.weight);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setViewState();
    }

    private void loadImg(String imgUrl) {
        Picasso.with(this).load(imgUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .centerCrop().into(avatarView);
    }

    private void setViewState() {
        UserController c = UserController.getInstance();
        nicknameView.setText(c.getNickname(this));
        loadImg(c.getImgUrl(this));

        String age = UserController.getInstance().getAge(this);
        if (TextUtils.isEmpty(age)) {
            ageView.setText(R.string.please_input);
        } else {
            ageView.setText(age + "岁");
        }

        // 情感
        final String[] emotionNames = getResources().getStringArray(R.array.emotion_names);
        final String[] emotionValues = getResources().getStringArray(R.array.emotion_values);
        String emotion = UserController.getInstance().getEmotion(this);
        int index = 0;
        int length = emotionValues.length;
        for (int i = 0; i < length; i++) {
            if (emotionValues[i].equals(emotion)) {
                index = i;
                break;
            }
        }
        emotionView.setText(emotionNames[index]);

        // 身高
        String h = c.getHeight(this);
        if (TextUtils.isEmpty(h)) {
            heightView.setText(R.string.please_input);
        } else {
            heightView.setText(h + "cm");
        }

        // 体重
        String w = c.getWeight(this);
        if (TextUtils.isEmpty(w)) {
            weightView.setText(R.string.please_input);
        } else {
            weightView.setText(w + "kg");
        }
    }

    public void onClickModifyNickName(View view) {
        Intent intent = new Intent(this, ModifyNickNameActivity.class);
        startActivity(intent);
    }

    public void onClickEmotion(View view) {
        final String[] emotionValues = getResources().getStringArray(R.array.emotion_values);
        String emotion = UserController.getInstance().getEmotion(this);
        int index = 0;
        int length = emotionValues.length;
        for (int i = 0; i < length; i++) {
            if (emotionValues[i].equals(emotion)) {
                index = i;
                break;
            }
        }
        final ArrayList<ItemBean> items = new ArrayList<>();
        final String[] emotionNames = getResources().getStringArray(R.array.emotion_names);
        length = emotionNames.length;
        for (int i = 0; i < length; i++) {
            items.add(new ItemBean(i, emotionNames[i]));
        }
        OptionsPickerView<ItemBean> optionsPickerView = new OptionsPickerView<>(this);
        optionsPickerView.setPicker(items, null, null);
        optionsPickerView.setCyclic(false, false, false);
        optionsPickerView.setSelectOptions(index, 0, 0);
        optionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                final String emotion = emotionValues[options1];
                final String emotionText = emotionNames[options1];
                ModifyTask task = new ModifyTask(PersonalActivity.this, "emotion", emotion, new Runnable() {
                    @Override
                    public void run() {
                        UserController.getInstance().setEmotion(PersonalActivity.this, emotion);
                        emotionView.setText(emotionText);
                    }
                });
                AsyncTaskCompat.executeParallel(task);
            }
        });
        optionsPickerView.setCancelable(true);
        optionsPickerView.show();
    }

    public void onClickHeight(View view) {
        String h = UserController.getInstance().getHeight(this);
        int index = 160;
        try {
            index = Integer.parseInt(h);
            index = Math.max(1, Math.min(index, 250));
        } catch (Exception e) {
            // ignore
        }
        final ArrayList<ItemBean> items = new ArrayList<>();
        for (int i = 1; i <= 250; i++) {
            items.add(new ItemBean(i, String.valueOf(i)));
        }
        OptionsPickerView<ItemBean> optionsPickerView = new OptionsPickerView<>(this);
        optionsPickerView.setPicker(items, null, null);
        optionsPickerView.setCyclic(false, false, false);
        optionsPickerView.setSelectOptions(index - 1, 0, 0);
        optionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                final String h = items.get(options1).getName();
                ModifyTask task = new ModifyTask(PersonalActivity.this, "high", h, new Runnable() {
                    @Override
                    public void run() {
                        UserController.getInstance().setHeight(PersonalActivity.this, h);
                        heightView.setText(h + "cm");
                    }
                });
                AsyncTaskCompat.executeParallel(task);
            }
        });
        optionsPickerView.setCancelable(true);
        optionsPickerView.show();
    }

    public void onClickWeight(View view) {
        String w = UserController.getInstance().getWeight(this);
        int index = 45;
        try {
            index = Integer.parseInt(w);
            index = Math.max(1, Math.min(index, 125));
        } catch (Exception e) {
            // ignore
        }
        final ArrayList<ItemBean> items = new ArrayList<>();
        for (int i = 1; i <= 125; i++) {
            items.add(new ItemBean(i, String.valueOf(i)));
        }
        OptionsPickerView<ItemBean> optionsPickerView = new OptionsPickerView<>(this);
        optionsPickerView.setPicker(items, null, null);
        optionsPickerView.setCyclic(false, false, false);
        optionsPickerView.setSelectOptions(index - 1, 0, 0);
        optionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                final String w = items.get(options1).getName();
                ModifyTask task = new ModifyTask(PersonalActivity.this, "weight", w, new Runnable() {
                    @Override
                    public void run() {
                        UserController.getInstance().setWeight(PersonalActivity.this, w);
                        weightView.setText(w + "kg");
                    }
                });
                AsyncTaskCompat.executeParallel(task);
            }
        });
        optionsPickerView.setCancelable(true);
        optionsPickerView.show();
    }

    public void onClickAge(View view) {
        String age = UserController.getInstance().getAge(this);
        int index = 18;
        try {
            index = Integer.parseInt(age);
            index = Math.max(1, Math.min(index, 120));
        } catch (Exception e) {
            // ignore
        }
        final ArrayList<ItemBean> items = new ArrayList<>();
        for (int i = 1; i <= 120; i++) {
            items.add(new ItemBean(i, String.valueOf(i)));
        }
        OptionsPickerView<ItemBean> optionsPickerView = new OptionsPickerView<>(this);
        optionsPickerView.setPicker(items, null, null);
        optionsPickerView.setCyclic(false, false, false);
        optionsPickerView.setSelectOptions(index - 1, 0, 0);
        optionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                final String age = items.get(options1).getName();
                ModifyTask task = new ModifyTask(PersonalActivity.this, "age", age, new Runnable() {
                    @Override
                    public void run() {
                        UserController.getInstance().setAge(PersonalActivity.this, age);
                        ageView.setText(age + "岁");
                    }
                });
                AsyncTaskCompat.executeParallel(task);
            }
        });
        optionsPickerView.setCancelable(true);
        optionsPickerView.show();
    }

    public void onClickAvatar(View view) {
        SelectAvatarPopWindow popWindow = new SelectAvatarPopWindow(this);
        popWindow.setListener(this);
        popWindow.show(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                UploadTask task = new UploadTask(this, data.getData());
                AsyncTaskCompat.executeParallel(task);
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                UploadTask task = new UploadTask(this, Uri.fromFile(captureOutFile));
                AsyncTaskCompat.executeParallel(task);
            }
        }
    }

    class UploadTask extends AsyncTask<Void, Void, UiResult<String>> {

        private Context context;
        private Uri uri;
        private ProgressDialog dialog;

        UploadTask(Context context, Uri uri) {
            this.context = context;
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.uploading));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult<String> doInBackground(Void... params) {
            UiResult<String> uiResult = new UiResult<>();
            try {
                final String userId = UserController.getInstance().getUserId(context);
                File dir = context.getCacheDir();
                FileApi.checkDir(dir);
                final File file = new File(dir, "tmp.png");
                InputStream in = null;
                Base64OutputStream out = null;
                try {
                    in = BitmapCompress.compress(context.getContentResolver(), uri, 144, 144);
                    out = new Base64OutputStream(new FileOutputStream(file), Base64.NO_WRAP);
                    FileApi.copy(in, out);
                } finally {
                    Closeables.close(in);
                    Closeables.close(out);
                }

                RequestBody requestBody = new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("application/x-www-form-urlencoded");
                    }

                    @Override
                    public long contentLength() throws IOException {
                        return ("userId=" + userId + "&img=").length() + file.length();
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.writeUtf8("userId=");
                        sink.writeUtf8(userId);
                        sink.writeUtf8("&img=");
                        InputStream in = null;
                        try {
                            in = new FileInputStream(file);
                            int v;
                            byte[] bytes = new byte[1024];
                            while ((v = in.read(bytes)) != -1) {
                                sink.write(bytes, 0, v);
                            }
                        } finally {
                            Closeables.close(in);
                        }
                    }
                };
                String url = context.getString(R.string.host) + "/rest/moli/upload-img";
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(requestBody);
                HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
                UploadResult r = result.parse(UploadResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
                uiResult.t = r.imgUrl;
            } catch (Exception e) {
                Lg.w(TAG, "failed to update img", e);
                uiResult.message = e.getMessage();
            }
            return uiResult;
        }

        @Override
        protected void onPostExecute(UiResult<String> result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtils.show(context, result.message);
            if (result.success) {
                UserController.getInstance().setImgUrl(context, result.t);
                loadImg(result.t);
            }
        }
    }

    @Override
    public void onClickPickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }

    @Override
    public void onClickCapture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestExternalStoragePermission();
        } else {
            try {
                File dir = getExternalCacheDir();
                FileApi.checkDir(dir);
                captureOutFile = new File(dir, "tmp.png");
                //noinspection ResultOfMethodCallIgnored
                captureOutFile.createNewFile();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureOutFile));
                startActivityForResult(intent, REQUEST_CODE_CAPTURE);
            } catch (IOException e) {
                Lg.w(TAG, "", e);
            }
        }
    }

    private void requestExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.capture_permission);
            builder.setNegativeButton(R.string.action_cancel, null);
            builder.setPositiveButton(R.string.action_setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestExternalStoragePermissionImpl();
                }
            });
            builder.show();
        } else {
            requestExternalStoragePermissionImpl();
        }
    }

    private void requestExternalStoragePermissionImpl() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onClickCapture();
        } else {
            ToastUtils.show(this, R.string.capture_permission, Toast.LENGTH_LONG);
        }
    }

    class ModifyTask extends AsyncTask<Void, Void, UiResult> {

        private Activity activity;
        private String col;
        private String value;
        private Runnable runnable;
        private ProgressDialog dialog;

        ModifyTask(Activity activity, String col, String value, Runnable runnable) {
            this.activity = activity;
            this.col = col;
            this.value = value;
            this.runnable = runnable;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getString(R.string.changing));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected UiResult doInBackground(Void... params) {
            return UserController.getInstance().modify(activity, col, value);
        }

        @Override
        protected void onPostExecute(UiResult result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtils.show(activity, result.message);
            if (result.success) {
                runnable.run();
            }
        }
    }
}
