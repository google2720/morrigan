package com.morrigan.m.c;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.HttpProxy;
import com.morrigan.m.HttpResult;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.ble.db.DBHelper;
import com.morrigan.m.device.DeviceController;
import com.morrigan.m.login.LoginResult;
import com.morrigan.m.login.UserInfo;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * 用户信息控制器
 * Created by y on 2016/10/3.
 */
public class UserController {

    private static final String TAG = "UserController";

    private static UserController sInstance = new UserController();

    private UserController() {
    }

    public static UserController getInstance() {
        return sInstance;
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    public void clear(Context context) {
        getSharedPreferences(context).edit().clear().apply();
        DBHelper.getInstance(context).clear();
    }

    public void quit(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("modify_user_info", false);
        editor.putBoolean("auto_login", false);
        editor.apply();
    }

    public void setUserId(Context context, String userId) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("userId", userId).apply();
    }

    public String getUserId(Context context) {
        return getSharedPreferences(context).getString("userId", null);
    }

    public boolean isAutoLogin(Context context) {
        return getSharedPreferences(context).getBoolean("auto_login", false);
    }

    public void setAutoLogin(Context context, boolean b) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putBoolean("auto_login", b).apply();
    }

    private void saveUserInfo(Context context, UserInfo userInfo, String mobile, String password) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", userInfo.userId);
        editor.putString("nickname", userInfo.nickName);
        editor.putString("sex", userInfo.sex);
        editor.putString("age", userInfo.age);
        editor.putString("emotion", userInfo.emotion);
        editor.putString("height", userInfo.high);
        editor.putString("weight", userInfo.weight);
        editor.putString("target", userInfo.target);
        editor.putString("authCode", userInfo.authCode);
        editor.putString("imgUrl", userInfo.imgUrl);
        editor.putBoolean("auto_login", true);
        editor.putString("mobile", mobile);
        editor.putString("password", password);
        editor.apply();
    }

    public String getImgUrl(Context context) {
        return getSharedPreferences(context).getString("imgUrl", null);
    }

    public void setImgUrl(Context context, String imgUrl) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("imgUrl", imgUrl).apply();
    }

    public String getMobile(Context context) {
        return getSharedPreferences(context).getString("mobile", null);
    }

    public String getPassword(Context context) {
        return getSharedPreferences(context).getString("password", null);
    }

    public String getNickname(Context context) {
        return getSharedPreferences(context).getString("nickname", null);
    }

    public void setNickname(Context context, String nickname) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("nickname", nickname);
        editor.putBoolean("modify_user_info", true);
        editor.apply();
        UploadUserInfoService.startAction(context);
    }

    public String getAge(Context context) {
        return getSharedPreferences(context).getString("age", "");
    }

    public void setAge(Context context, String age) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("age", age);
        editor.putBoolean("modify_user_info", true);
        editor.apply();
        UploadUserInfoService.startAction(context);
    }

    public String getEmotion(Context context) {
        return getSharedPreferences(context).getString("emotion", "");
    }

    public void setEmotion(Context context, String emotion) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("emotion", emotion);
        editor.putBoolean("modify_user_info", true);
        editor.apply();
        UploadUserInfoService.startAction(context);
    }

    public String getHeight(Context context) {
        return getSharedPreferences(context).getString("height", "");
    }

    public void setHeight(Context context, String h) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("height", h);
        editor.putBoolean("modify_user_info", true);
        editor.apply();
        UploadUserInfoService.startAction(context);
    }

    public String getWeight(Context context) {
        return getSharedPreferences(context).getString("weight", "");
    }

    public void setWeight(Context context, String w) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("weight", w);
        editor.putBoolean("modify_user_info", true);
        editor.apply();
        UploadUserInfoService.startAction(context);
    }

    public String getTarget(Context context) {
        return getSharedPreferences(context).getString("target", "60");
    }

    public int getTargetInt(Context context) {
        int value = 60;
        try {
            value = Integer.parseInt(getTarget(context));
        } catch (Exception e) {
            // ignore
        }
        return value;
    }

    public void setTarget(Context context, String target) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("target", target);
        editor.putBoolean("modify_user_info", true);
        editor.apply();
        UploadUserInfoService.startAction(context);
    }

    private boolean getModifyUserInfo(Context context) {
        return getSharedPreferences(context).getBoolean("modify_user_info", false);
    }

    public void setModifyUserInfo(Context context, boolean modify) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putBoolean("modify_user_info", modify).apply();
    }

    public void setMusicTimeInterval(Context context, long timeInterval) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putLong("MusicTimeInterval", timeInterval).apply();
    }

    public long getMusicTimeInterval(Context context) {
        return getSharedPreferences(context).getLong("MusicTimeInterval", 2000);
    }

    public UiResult modify(Context context, String col, String value) {
        UiResult uiResult = new UiResult();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/eidt-user-info";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", UserController.getInstance().getUserId(context));
            b.add(col, value);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpResult r = new HttpProxy().execute(context, builder.build(), HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
        } catch (Exception e) {
            Lg.w("user", "failed to modify user info", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }

    public UiResult<Void> logout(Context context) {
        UiResult<Void> uiResult = new UiResult<>();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/cancel";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", UserController.getInstance().getUserId(context));
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpResult r = new HttpProxy().execute(context, builder.build(), HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
        } catch (Exception e) {
            Lg.w("user", "failed to logout user", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }

    public UiResult<LoginResult> login(Context context, String mobile, String pw) {
        UiResult<LoginResult> uiResult = new UiResult<>();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/login";
            FormBody.Builder b = new FormBody.Builder();
            b.add("mobile", mobile);
            b.add("password", pw);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            LoginResult r = new HttpProxy().execute(context, builder.build(), LoginResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
            uiResult.t = r;
            if (uiResult.success) {
                if (getModifyUserInfo(context)) {
                    UploadUserInfoService.startAction(context);
                } else {
                    saveUserInfo(context, r.userInfo, mobile, pw);
                }
                DeviceController.getInstance().fetchAsync(context);
            }
        } catch (Exception e) {
            Lg.w(TAG, "failed to login", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }

    public HttpResult sendSmsCode(Context context, String mobile) throws IOException {
        String url = context.getString(R.string.host) + "/rest/moli/send-msg";
        FormBody.Builder b = new FormBody.Builder();
        b.add("mobile", mobile);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(b.build());
        return new HttpProxy().execute(context, builder.build(), HttpResult.class);
    }

    public boolean checkRegister(Context context, String mobile) throws IOException {
        String url = context.getString(R.string.host) + "/rest/moli/isregister";
        FormBody.Builder b = new FormBody.Builder();
        b.add("mobile", mobile);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(b.build());
        HttpResult r = new HttpProxy().execute(context, builder.build(), HttpResult.class);
        return r.isSuccessful();
    }

    public UiResult<Void> upload(Context context) {
        UiResult<Void> uiResult = new UiResult<>();
        try {
            String url = context.getString(R.string.host) + "/rest/moli/eidt-user-info";
            FormBody.Builder b = new FormBody.Builder();
            b.add("userId", getUserId(context));
            b.add("high", getHeight(context));
            b.add("weight", getWeight(context));
            b.add("age", getAge(context));
            b.add("nickName", getNickname(context));
            b.add("target", getTarget(context));
            b.add("emotion", getEmotion(context));
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(b.build());
            HttpResult r = new HttpProxy().execute(context, builder.build(), HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
        } catch (Exception e) {
            Lg.w("user", "failed to modify user info", e);
            uiResult.message = HttpProxy.parserError(context, e);
        }
        return uiResult;
    }
}
