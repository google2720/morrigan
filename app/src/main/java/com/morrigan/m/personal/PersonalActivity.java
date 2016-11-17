package com.morrigan.m.personal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.ItemBean;
import com.github.yzeaho.common.ToastUtils;
import com.github.yzeaho.file.Closeables;
import com.github.yzeaho.file.FileApi;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.Dirs;
import com.morrigan.m.HttpProxy;
import com.morrigan.m.R;
import com.morrigan.m.ToolbarActivity;
import com.morrigan.m.UiResult;
import com.morrigan.m.c.UserController;
import com.morrigan.m.login.LoginActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 我的资料界面
 * Created by y on 2016/10/4.
 */
public class PersonalActivity extends ToolbarActivity implements SelectAvatarPopWindow.Listener {

    private static final String TAG = "Personal";
    private static final int REQUEST_CODE_PICK_PHOTO = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private static final int REQUEST_CODE_CUT = 3;
    private static final int MIN_WEIGHT = 20;
    private static final int MAX_WEIGHT = 199;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_HEIGHT = 299;
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
        if (TextUtils.isEmpty(imgUrl)) {
            Picasso.with(this).load(R.drawable.default_avatar).into(avatarView);
        } else {
            Picasso.with(this).load(imgUrl).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(avatarView);
        }
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
                PersonalModifyTask task = new PersonalModifyTask(PersonalActivity.this, "emotion", emotion, new Runnable() {
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
        int index = 0;
        try {
            index = Integer.parseInt(h) - MIN_HEIGHT;
            index = Math.max(0, Math.min(index, MAX_HEIGHT - MIN_HEIGHT));
        } catch (Exception e) {
            // ignore
        }
        final ArrayList<ItemBean> items = new ArrayList<>();
        for (int i = MIN_HEIGHT; i <= MAX_HEIGHT; i++) {
            items.add(new ItemBean(i, String.valueOf(i)));
        }
        OptionsPickerView<ItemBean> optionsPickerView = new OptionsPickerView<>(this);
        optionsPickerView.setPicker(items, null, null);
        optionsPickerView.setCyclic(false, false, false);
        optionsPickerView.setSelectOptions(index, 0, 0);
        optionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                final String h = items.get(options1).getName();
                PersonalModifyTask task = new PersonalModifyTask(PersonalActivity.this, "high", h, new Runnable() {
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
        int index = 0;
        try {
            index = Integer.parseInt(w) - MIN_WEIGHT;
            index = Math.max(0, Math.min(index, MAX_WEIGHT - MIN_WEIGHT));
        } catch (Exception e) {
            // ignore
        }
        final ArrayList<ItemBean> items = new ArrayList<>();
        for (int i = MIN_WEIGHT; i <= MAX_WEIGHT; i++) {
            items.add(new ItemBean(i, String.valueOf(i)));
        }
        OptionsPickerView<ItemBean> optionsPickerView = new OptionsPickerView<>(this);
        optionsPickerView.setPicker(items, null, null);
        optionsPickerView.setCyclic(false, false, false);
        optionsPickerView.setSelectOptions(index, 0, 0);
        optionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                final String w = items.get(options1).getName();
                PersonalModifyTask task = new PersonalModifyTask(PersonalActivity.this, "weight", w, new Runnable() {
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
                PersonalModifyTask task = new PersonalModifyTask(PersonalActivity.this, "age", age, new Runnable() {
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
                startPhotoZoom(data.getData(), getResources().getDimensionPixelSize(R.dimen.avatar_width));
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Uri outputUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", captureOutFile);
                startPhotoZoom(outputUri, getResources().getDimensionPixelSize(R.dimen.avatar_width));
            }
        } else if (requestCode == REQUEST_CODE_CUT) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap bitmap = bundle.getParcelable("data");
                    UploadTask task = new UploadTask(this, bitmap);
                    AsyncTaskCompat.executeParallel(task);
                }
            }
        }
    }

    public void onClickQuit(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您确定退出当前账号");
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UserController.getInstance().setAutoLogin(PersonalActivity.this, false);
                Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE_CUT);
    }

    class UploadTask extends AsyncTask<Void, Void, UiResult<String>> {

        private Context context;
        private Bitmap bitmap;
        private ProgressDialog dialog;

        UploadTask(Context context, Bitmap bitmap) {
            this.context = context;
            this.bitmap = bitmap;
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
                String userId = UserController.getInstance().getUserId(context);
                File dir = context.getCacheDir();
                FileApi.checkDir(dir);
                final File file = new File(dir, "tmp.png");
                OutputStream out = null;
                try {
                    out = new Base64OutputStream(new FileOutputStream(file), Base64.NO_WRAP);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } finally {
                    Closeables.close(out);
                }
                InputStream in = null;
                ByteArrayOutputStream bouts = new ByteArrayOutputStream();
                try {
                    in = new FileInputStream(file);
                    int v;
                    byte[] bytes = new byte[1024];
                    while ((v = in.read(bytes)) != -1) {
                        bouts.write(bytes, 0, v);
                    }
                } finally {
                    Closeables.close(in);
                }
                FormBody.Builder b = new FormBody.Builder();
                b.add("userId", userId);
                b.add("img", new String(bouts.toByteArray()));
                RequestBody requestBody = b.build();
                String url = context.getString(R.string.host) + "/rest/moli/upload-img";
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(requestBody);
                UploadResult r = new HttpProxy().execute(context, builder.build(), UploadResult.class);
                uiResult.success = r.isSuccessful();
                uiResult.message = r.retMsg;
                uiResult.t = r.imgUrl;
            } catch (Exception e) {
                Lg.w(TAG, "failed to update img", e);
                uiResult.message = HttpProxy.parserError(context, e);
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
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }

    @Override
    public void onClickCapture() {
        try {
            File dir = Dirs.getCaptureDir(this);
            FileApi.checkDir(dir);
            captureOutFile = new File(dir, "tmp.png");
            // noinspection ResultOfMethodCallIgnored
            captureOutFile.createNewFile();
            Uri outputUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", captureOutFile);
            Lg.i(TAG, "capture:" + outputUri.toString());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE);
        } catch (IOException e) {
            Lg.w(TAG, "", e);
        }
    }
}
