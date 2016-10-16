package com.morrigan.m;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.yzeaho.file.Closeables;
import com.github.yzeaho.http.HttpInterface;
import com.github.yzeaho.log.Lg;
import com.morrigan.m.historyrecord.TodayRecord;
import com.morrigan.m.login.UserInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void setUserId(Context context, String userId) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("userId", userId).apply();
    }

    public String getUserId(Context context) {
        return getSharedPreferences(context).getString("userId", null);
    }

    public void setUserInfo(Context context, UserInfo userInfo) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", userInfo.userId);
        editor.putString("password", userInfo.password);
        editor.putString("nickname", userInfo.nickName);
        editor.putString("mobile", userInfo.mobile);
        editor.putString("sex", userInfo.sex);
        editor.putString("age", userInfo.age);
        editor.putString("emotion", userInfo.emotion);
        editor.putString("height", userInfo.high);
        editor.putString("weight", userInfo.weight);
        editor.putString("target", userInfo.target);
        editor.putString("authCode", userInfo.authCode);
        editor.putString("imgUrl", userInfo.imgUrl);
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
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("nickname", nickname).apply();
    }

    public String getAge(Context context) {
        return getSharedPreferences(context).getString("age", null);
    }

    public void setAge(Context context, String age) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("age", age).apply();
    }

    public String getEmotion(Context context) {
        return getSharedPreferences(context).getString("emotion", null);
    }

    public void setEmotion(Context context, String emotion) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("emotion", emotion).apply();
    }

    public String getHeight(Context context) {
        return getSharedPreferences(context).getString("height", null);
    }

    public void setHeight(Context context, String h) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("height", h).apply();
    }

    public String getWeight(Context context) {
        return getSharedPreferences(context).getString("weight", null);
    }

    public void setWeight(Context context, String w) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("weight", w).apply();
    }

    public String getTarget(Context context) {
        return getSharedPreferences(context).getString("target", "60");
    }

    public void setTarget(Context context, String target) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString("target", target).apply();
    }

    public TodayRecord getTodayRecord(Context context) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        TodayRecord data = UserController.getInstance().get(context, "todayRecord", TodayRecord.class);
        if (data == null || !data.date.equals(sf.format(new Date()))) {
            data = new TodayRecord();
        }
        return data;
    }

    public boolean setTodayRecord(Context context, TodayRecord todayRecord) {
        return save(context, "todayRecord", todayRecord);
    }

    public <T> T get(Context context, String key, Class<T> c) {
        ObjectInputStream in = null;
        try {
            File file = context.getFileStreamPath(key);
            in = new ObjectInputStream(new FileInputStream(file));
            Object o = in.readObject();
            if (o != null && c.isInstance(o)) {
                return (T) o;
            }
        } catch (Exception e) {
            Log.w(TAG, "", e);
        } finally {
            Closeables.close(in);
        }
        return null;
    }


    public boolean save(Context context, String key, Serializable obj) {
        ObjectOutputStream out = null;
        try {
            File file = context.getFileStreamPath(key);
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(obj);
            out.flush();
            return true;
        } catch (Exception e) {
            Log.w(TAG, "", e);
            return false;
        } finally {
            Closeables.close(out);
        }
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
            HttpInterface.Result result = HttpInterface.Factory.create().execute(builder.build());
            HttpResult r = result.parse(HttpResult.class);
            uiResult.success = r.isSuccessful();
            uiResult.message = r.retMsg;
        } catch (Exception e) {
            Lg.w("user", "failed to modify user info", e);
            uiResult.message = e.getMessage();
        }
        return uiResult;
    }
}
