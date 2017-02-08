package cn.yhq.preferences;

import io.realm.RealmObject;

/**
 * Created by Yanghuiqiang on 2017/1/23.
 */

public class Preferences extends RealmObject {
    private String key;
    private byte[] value;
    private long time;
    private int type;

    public Preferences() {
        time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public Preferences setTime(long time) {
        this.time = time;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Preferences setKey(String key) {
        this.key = key;
        return this;
    }

    public byte[] getValue() {
        return value;
    }

    public Preferences setValue(byte[] value) {
        this.value = value;
        return this;
    }

    public int getType() {
        return type;
    }

    public Preferences setType(int type) {
        this.type = type;
        return this;
    }
}
