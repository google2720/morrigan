package com.morrigan.m.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本工具类
 * Created by y on 2016/12/29.
 */
public class AppTextUtils {

    public static boolean isCellPhone(String phone) {
        return phone != null && phone.length() > 0 && Pattern.matches("1[0-9]{10}", phone);
    }
}
