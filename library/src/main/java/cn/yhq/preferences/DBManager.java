package cn.yhq.preferences;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by Yanghuiqiang on 2017/1/23.
 */

public class DBManager {
    private static final Map<String, Realm> realms = new HashMap<>();
    private static Context context;

    public static void init(Context context) {
        DBManager.context = context;
        Realm.init(context);
    }

    public static Realm getRealm(String dbPath, String databaseName) {
        Realm realm = realms.get(databaseName);
        if (realm == null || realm.isClosed()) {
            RealmConfiguration configuration = new RealmConfiguration.Builder()
                    .directory(new File(dbPath))
                    .name(databaseName)
                    //.inMemory()
                    .schemaVersion(1)
                    .build();
            realm = Realm.getInstance(configuration);
        }
        return realm;
    }

    public static <E extends RealmModel> List<E> getLimitList(String dbPath,
                                                              String databaseName,
                                                              RealmResults<E> data, int offset, int limit) {
        List<E> obtainList = new ArrayList();
        Realm realm = getRealm(dbPath, databaseName);
        if (data.size() == 0) {
            return obtainList;
        }
        for (int i = offset; i < offset + limit; i++) {
            if (i >= data.size()) {
                break;
            }
            E temp = realm.copyFromRealm(data.get(i));
            obtainList.add(temp);
        }
        realm.close();
        return obtainList;
    }
}
