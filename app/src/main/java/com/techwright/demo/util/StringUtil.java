package com.techwright.demo.util;

import android.text.TextUtils;

public class StringUtil {

    public static boolean checkIfNull(String value) {
        return value != null && !TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null");
    }

}
