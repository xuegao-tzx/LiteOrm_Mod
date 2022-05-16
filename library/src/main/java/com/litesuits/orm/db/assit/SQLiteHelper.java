package com.litesuits.orm.db.assit;

import com.litesuits.orm.log.Log;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.StoreConfig;

/**
 * SQLite辅助类
 *
 * @author mty
 * @date 2013 -6-2下午4:42:47
 */
public class SQLiteHelper extends DatabaseHelper {

    /**
     * The On update listener.
     */
    private final OnUpdateListener onUpdateListener;
    /**
     * The Config.
     */
    private final StoreConfig config;
    /**
     * The Version.
     */
    private final int version;

    /**
     * Instantiates a new Sq lite helper.
     *
     * @param context          the context
     * @param name             the name
     * @param version          the version
     * @param onUpdateListener the on update listener
     */
    public SQLiteHelper(Context context, String name, int version, OnUpdateListener onUpdateListener) {
        super(context);
        Log.println("new SQLiteHelper name:%s", name);
        this.onUpdateListener = onUpdateListener;
        this.version = version;
        this.config = StoreConfig.newDefaultConfig(name);

    }

    /**
     * Gets rdb store.
     *
     * @return the rdb store
     */
    public RdbStore getRdbStore() {
        return this.getRdbStore(this.config, this.version, new RdbOpenCallback() {
            @Override
            public void onCreate(RdbStore rdbStore) {
                Log.println("getRdbStore - onCreate");
                try {
                    rdbStore.executeSql("CREATE TABLE IF NOT EXISTS Categories (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT);");
                } catch (Exception e) {
                    Log.println("getRdbStore - onCreate Exception");
                    e.printStackTrace();
                }
            }

            @Override
            public void onUpgrade(RdbStore db, int oldVersion, int newVersion) {
                if (SQLiteHelper.this.onUpdateListener != null)
                    SQLiteHelper.this.onUpdateListener.onUpdate(db, oldVersion, newVersion);
            }
        });
    }

    /**
     * The interface On update listener.
     */
    public interface OnUpdateListener {
        /**
         * On update.
         *
         * @param db         the db
         * @param oldVersion the old version
         * @param newVersion the new version
         */
        void onUpdate(RdbStore db, int oldVersion, int newVersion);
    }


}