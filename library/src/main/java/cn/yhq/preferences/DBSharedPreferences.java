package cn.yhq.preferences;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;

import static android.R.attr.value;

/**
 * Created by Yanghuiqiang on 2017/1/23.
 */

public class DBSharedPreferences implements SharedPreferences {
    public static final String DB_NAME = "preferences.realm";

    private static DBSharedPreferences instance = new DBSharedPreferences();

    public static DBSharedPreferences getDefaultInstance() {
        return instance;
    }

    private static String DEFAULT_DB_PATH;

    private String dbPath;

    public DBSharedPreferences() {
        this(DEFAULT_DB_PATH);
    }

    public DBSharedPreferences(String dbPath) {
        this.dbPath = dbPath;
    }

    public static void setDefaultDBPath(String dbPath) {
        DEFAULT_DB_PATH = dbPath;
    }

    @Override
    public Map<String, ?> getAll() {
        Map<String, Object> map = new HashMap<>();
        Iterator<Preferences> iterator = getAllPreferences();
        while (iterator.hasNext()) {
            Preferences preferences = iterator.next();
            map.put(preferences.getKey(), preferences.getValue());
        }
        return map;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        return (String) convert(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return (Set<String>) convert(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return (int) convert(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return (long) convert(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return (float) convert(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return (boolean) convert(key, defValue);
    }

    private Object convert(String key, Object defValue) {
        Preferences preferences = getPreferences(key);
        if (preferences == null) {
            return defValue;
        }
        return Types.convertTo(preferences.getType(), preferences.getValue());
    }

    @Override
    public boolean contains(String key) {
        return convert(key, null) != null;
    }

    @Override
    public SharedPreferences.Editor edit() {
        return new Editor(this);
    }

    public Preferences getPreferences(String key) {
        return getRealm().where(Preferences.class).equalTo("key", key).findFirst();
    }

    public Iterator<Preferences> getAllPreferences() {
        Iterator<Preferences> iterator = getRealm().where(Preferences.class).findAll().iterator();
        return iterator;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    Realm getRealm() {
        return DBManager.getRealm(dbPath, DB_NAME);
    }

    public static class Editor implements SharedPreferences.Editor {
        private DBSharedPreferences sharedPreferences;
        private List<Preferences> addList = new ArrayList<>();
        private List<String> removeList = new ArrayList<>();

        Editor(DBSharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
        }

        private Preferences createPreferences(String key, Object value, int type) {
            Preferences preferences = new Preferences();
            preferences.setKey(key);
            preferences.setType(type);
            preferences.setValue(Types.convertTo(type, value));
            return preferences;
        }

        @Override
        public SharedPreferences.Editor putString(String key, String value) {
            addList.add(createPreferences(key, value, Types.STRING));
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            addList.add(createPreferences(key, value, Types.SET));
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            addList.add(createPreferences(key, value, Types.INT));
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            addList.add(createPreferences(key, value, Types.LONG));
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            addList.add(createPreferences(key, value, Types.FLOAT));
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            addList.add(createPreferences(key, value, Types.BOOL));
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            removeList.add(key);
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            addList.clear();
            return this;
        }

        @Override
        public boolean commit() {
            try {
                sharedPreferences.getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (Preferences preferences : addList) {
                            remove(preferences.getKey());
                        }
                        for (String key : removeList) {
                            Preferences preferences = sharedPreferences.getPreferences(key);
                            if (preferences != null) {
                                preferences.deleteFromRealm();
                            }
                        }
                        for (Preferences preferences : addList) {
                            realm.copyToRealm(preferences);
                        }
                    }
                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sharedPreferences.getRealm().close();
            }
            return false;
        }

        @Override
        public void apply() {
            commit();
        }
    }
}
