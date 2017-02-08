package cn.yhq.preferences;

import android.content.SharedPreferences;

import cn.teamtalking.pms.utils.JsonUtils;

public class PreferencesUtil {

    public static boolean savePreferences(String key, Object value) {
        boolean success = false;
        if (key == null) {
            return success;
        }
        SharedPreferences preferences = DBSharedPreferences.getDefaultInstance();
        SharedPreferences.Editor editor = preferences.edit();
        if (value == null) {
            editor.remove(key);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else {
            editor.putString(key, JsonUtils.toJson(value));
        }
        success = editor.commit();
        return success;
    }

    public static boolean getBooleanValue(String key, boolean defaultValue) {
        SharedPreferences preferences = DBSharedPreferences.getDefaultInstance();
        boolean value = preferences.getBoolean(key, defaultValue);
        return value;
    }

    public static int getIntValue(String key, int defaultValue) {
        SharedPreferences preferences = DBSharedPreferences.getDefaultInstance();
        int value = preferences.getInt(key, defaultValue);
        return value;
    }

    public static float getFloatValue(String key, float defaultValue) {
        SharedPreferences preferences = DBSharedPreferences.getDefaultInstance();
        float value = preferences.getFloat(key, defaultValue);
        return value;
    }

    public static String getStringValue(String key, String defaultValue) {
        SharedPreferences preferences = DBSharedPreferences.getDefaultInstance();
        String value = preferences.getString(key, defaultValue);
        return value;
    }

    public static long getLongValue(String key, long defaultValue) {
        SharedPreferences preferences = DBSharedPreferences.getDefaultInstance();
        long value = preferences.getLong(key, defaultValue);
        return value;
    }

    public static <T> T getObjectValue(String key, Class<T> clazz) {
        String value = getStringValue(key, null);
        if (value == null) {
            return null;
        } else {
            return JsonUtils.toObject(value, clazz);
        }
    }
}
