/*
 * Copyright (C) 2013 litesuits.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.litesuits.orm;

import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.*;
import com.litesuits.orm.db.impl.CascadeSQLiteImpl;
import com.litesuits.orm.db.impl.SingleSQLiteImpl;
import com.litesuits.orm.db.model.*;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.log.Log;
import com.litesuits.orm.log.OrmLog;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.rdb.RdbStore;
import ohos.data.resultset.ResultSet;
import ohos.data.resultset.ResultSetHook;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * 数据SQLite操作实现
 * 可查阅 <a href="http://www.sqlite.org/lang.html">SQLite操作指南</a>
 *
 * @author mty
 * @date 2013 -6-2下午2:32:56
 */
public abstract class LiteOrm /*extends SQLiteClosable*/ implements DataBase {

    /**
     * The constant TAG.
     */
    private static final String TAG = LiteOrm.class.getSimpleName();
    /**
     * The M config.
     */
    private final DataBaseConfig mConfig;
    /**
     * The M helper.
     */
    protected SQLiteHelper mHelper;
    /**
     * The M table manager.
     */
    protected TableManager mTableManager;
    /**
     * The Other database.
     */
    protected LiteOrm otherDatabase;

    /**
     * Instantiates a new Lite orm.
     *
     * @param dataBase the data base
     */
    protected LiteOrm(LiteOrm dataBase) {
        this.mHelper = dataBase.mHelper;
        this.mConfig = dataBase.mConfig;
        this.mTableManager = dataBase.mTableManager;
        this.otherDatabase = dataBase;
    }

    /**
     * Instantiates a new Lite orm.
     *
     * @param config the config
     */
    protected LiteOrm(DataBaseConfig config) {
        config.context = config.context.getApplicationContext();
        if (config.dbName == null) config.dbName = DataBaseConfig.DEFAULT_DB_NAME;
        if (config.dbVersion <= 0) config.dbVersion = DataBaseConfig.DEFAULT_DB_VERSION;
        this.mConfig = config;
        this.setDebugged(config.debugged);
        this.openOrCreateDatabase();
    }

    /**
     * get and new a single model operator based on SQLite
     *
     * @param context app context
     * @param dbName  database name
     * @return {@link SingleSQLiteImpl}
     */
    public static LiteOrm newSingleInstance(Context context, String dbName) {
        return newSingleInstance(new DataBaseConfig(context, dbName));
    }

    /**
     * get and new a single model operator based on SQLite
     *
     * @param config lite-orm config
     * @return {@link CascadeSQLiteImpl}
     */
    private synchronized static LiteOrm newSingleInstance(DataBaseConfig config) {
        return SingleSQLiteImpl.newInstance(config);
    }

    /**
     * get and new a cascade model operator based on SQLite
     *
     * @param context app context
     * @param dbName  database name
     * @return {@link SingleSQLiteImpl}
     */
    public static LiteOrm newCascadeInstance(Context context, String dbName) {
        return newCascadeInstance(new DataBaseConfig(context, dbName));
    }

    /**
     * get and new a cascade model operator based on SQLite
     *
     * @param config lite-orm config
     * @return {@link CascadeSQLiteImpl}
     */
    private synchronized static LiteOrm newCascadeInstance(DataBaseConfig config) {
        return CascadeSQLiteImpl.newInstance(config);
    }

    /**
     * Attempts to release memory that SQLite holds but does not require to
     * operate properly. Typically this memory will come from the page cache.
     *
     * @return the number of bytes actually released
     */
    public static int releaseMemory() {
        return DatabaseHelper.releaseRdbMemory();//SQLiteDatabase.releaseMemory();
    }

    /**
     * Open or create database rdb store.
     *
     * @param path    the path
     * @param factory the factory
     * @return the rdb store
     */
    @Override
    public RdbStore openOrCreateDatabase(String path, ResultSetHook factory) {
        Log.w("LiteOrm", "openOrCreateDatabase  path:" + path);
        return null;
    }

    /**
     * Open or create database rdb store.
     *
     * @return the rdb store
     */
    @Override
    public RdbStore openOrCreateDatabase() {
        this.initDatabasePath(this.mConfig.dbName);
        if (this.mHelper != null) this.justRelease();
        this.mHelper = new SQLiteHelper(this.mConfig.context, this.mConfig.dbName, this.mConfig.dbVersion, this.mConfig.onUpdateListener);
        RdbStore rdbStore = this.mHelper.getRdbStore();
        this.mTableManager = new TableManager(this.mConfig.dbName, rdbStore);
        return rdbStore;
    }

    /**
     * Init database path.
     *
     * @param path the path
     */
    private void initDatabasePath(String path) {
        OrmLog.i(TAG, "create  database path: " + path);
        /*
        鸿蒙获取的是dir 需要验证dir下是否有db文件
         path = mConfig.context.getDataDir().getDatabasePath(mConfig.dbName).getPath();
         */
        File dataDir = this.mConfig.context.getDatabaseDir();
        path = new File(dataDir, this.mConfig.dbName).getPath();
        OrmLog.i(TAG, "context database path: " + path);
        File dbp = new File(path).getParentFile();
        if (dbp != null && !dbp.exists()) {
            boolean mks = dbp.mkdirs();
            OrmLog.i(TAG, "create database, parent file mkdirs: " + mks + "  path:" + dbp.getAbsolutePath());
        }
    }

    /**
     * get a single data operator based on SQLite
     *
     * @return {@link CascadeSQLiteImpl}
     */
    public abstract LiteOrm single();

    /**
     * get a cascade data operator based on SQLite
     *
     * @return {@link CascadeSQLiteImpl}
     */
    public abstract LiteOrm cascade();

    /**
     * when debugged is true, the {@link OrmLog} is opened.
     *
     * @param debugged true if debugged
     */
    public void setDebugged(boolean debugged) {
        this.mConfig.debugged = debugged;
        OrmLog.isPrint = debugged;//田梓萱22.02.07修改增加日志可控
    }

    /**
     * Query relation array list.
     *
     * @param class1   the class 1
     * @param class2   the class 2
     * @param key1List the key 1 list
     * @return the array list
     */
    @Override
    public ArrayList<RelationKey> queryRelation(Class class1, Class class2, List<String> key1List) {
        ArrayList<RelationKey> rList = new ArrayList<RelationKey>();
        try {
            EntityTable table1 = TableManager.getTable(class1);
            EntityTable table2 = TableManager.getTable(class2);
            if (this.mTableManager.isSQLMapTableCreated(table1.name, table2.name))
                CollSpliter.split(key1List, SQLStatement.IN_TOP_LIMIT, new CollSpliter.Spliter<String>() {

                    @Override
                    public int oneSplit(ArrayList<String> list) throws Exception {
                        SQLStatement stmt = SQLBuilder.buildQueryRelationSql(class1, class2, key1List);
                        Querier.doQuery(LiteOrm.this.mHelper.getRdbStore()/*.getReadableDatabase()*/, stmt, new Querier.CursorParser() {

                            @Override
                            public void parseEachCursor(RdbStore db, ResultSet c) throws Exception {
                                RelationKey relation = new RelationKey();
                                relation.key1 = c.getString(c.getColumnIndexForName(table1.name));
                                relation.key2 = c.getString(c.getColumnIndexForName(table2.name));
                                rList.add(relation);
                            }

                        });
                        return 0;

                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            releaseReference();
        }
        return rList;
    }

    /**
     * Mapping boolean.
     *
     * @param <E>  the type parameter
     * @param <T>  the type parameter
     * @param col1 the col 1
     * @param col2 the col 2
     * @return the boolean
     */
    @Override
    public <E, T> boolean mapping(Collection<E> col1, Collection<T> col2) {
        if (Checker.isEmpty(col1) || Checker.isEmpty(col2)) return false;
//        acquireReference();
        try {
            return this.keepMapping(col1, col2) | this.keepMapping(col2, col1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            releaseReference();
        }
        return false;
    }

    /**
     * Create sql statement sql statement.
     *
     * @param sql      the sql
     * @param bindArgs the bind args
     * @return the sql statement
     */
    @Override
    public SQLStatement createSQLStatement(String sql, Object[] bindArgs) {
        return new SQLStatement(sql, bindArgs);
    }

    /**
     * Execute boolean.
     *
     * @param db        the db
     * @param statement the statement
     * @return the boolean
     */
    @Override
    public boolean execute(RdbStore db, SQLStatement statement) {
//        acquireReference();
        try {
            if (statement != null) return statement.execute(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            releaseReference();
        }
        return false;

    }

    /**
     * Drop table boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    @Override
    @Deprecated
    public boolean dropTable(Object entity) {
        return this.dropTable(entity.getClass());
    }

    /**
     * Drop table boolean.
     *
     * @param claxx the claxx
     * @return the boolean
     */
    @Override
    public boolean dropTable(Class<?> claxx) {
        return this.dropTable(TableManager.getTable(claxx, false).name);
    }

    /**
     * Drop table boolean.
     *
     * @param tableName the table name
     * @return the boolean
     */
    @Override
    public boolean dropTable(String tableName) {
//        acquireReference();
        try {
            return SQLBuilder.buildDropTable(tableName).execute(this.mHelper.getRdbStore());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            releaseReference();
        }
        return false;
    }

    /**
     * Query count long.
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the long
     */
    @Override
    public <T> long queryCount(Class<T> claxx) {
        return this.queryCount(new QueryBuilder<T>(claxx));
    }

    /**
     * Query count long.
     *
     * @param qb the qb
     * @return the long
     */
    @Override
    public long queryCount(QueryBuilder qb) {
//        acquireReference();
        try {
            if (this.mTableManager.isSQLTableCreated(qb.getTableName())) {
                RdbStore db = this.mHelper.getRdbStore();
                SQLStatement stmt = qb.createStatementForCount();
                return stmt.queryForLong(db);
            } else return 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Update int.
     *
     * @param where             the where
     * @param cvs               the cvs
     * @param conflictAlgorithm the conflict algorithm
     * @return the int
     */
    @Override
    public int update(WhereBuilder where, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
//        acquireReference();
        try {
            RdbStore db = this.mHelper.getRdbStore();//.getWritableDatabase();
            SQLStatement stmt = SQLBuilder.buildUpdateSql(where, cvs, conflictAlgorithm);
            return stmt.execUpdate(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Gets readable database.
     *
     * @return the readable database
     */
    @Override
    public synchronized RdbStore getReadableDatabase() {
        return this.mHelper.getRdbStore();//.getReadableDatabase();
    }

    /**
     * Gets writable database.
     *
     * @return the writable database
     */
    @Override
    public synchronized RdbStore getWritableDatabase() {
        return this.mHelper.getRdbStore();//.getWritableDatabase();
    }

    /**
     * Gets table manager.
     *
     * @return the table manager
     */
    @Override
    public TableManager getTableManager() {
        return this.mTableManager;
    }

    /**
     * Gets sq lite helper.
     *
     * @return the sq lite helper
     */
    @Override
    public SQLiteHelper getSQLiteHelper() {
        return this.mHelper;
    }

//    @Override
//    public RdbStore openOrCreateDatabase(String path, SQLiteDatabase.CursorFactory factory) {
////        path = mConfig.context.getDatabasePath(mConfig.dbName).getPath();
//        File databaseDir = mConfig.context.getDatabaseDir();
//        path = new File(databaseDir,mConfig.dbName).getPath();
//        return SQLiteDatabase.openOrCreateDatabase(path, factory);
//
//    }

    /**
     * Gets data base config.
     *
     * @return the data base config
     */
    @Override
    public DataBaseConfig getDataBaseConfig() {
        return this.mConfig;
    }

    /**
     * Delete database boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean deleteDatabase() {
        String path = this.mHelper.getRdbStore().getPath();//.getWritableDatabase().getPath();
        this.justRelease();
        OrmLog.i(TAG, "data has cleared. delete Database path: " + path);
        return this.deleteDatabase(new File(path));
    }

    /**
     * Delete database boolean.
     *
     * @param file the file
     * @return the boolean
     */
    @Override
    public boolean deleteDatabase(File file) {
//        acquireReference();
        try {
            if (file == null) throw new IllegalArgumentException("file must not be null");
            boolean deleted = file.delete();
            deleted |= new File(file.getPath() + "-journal").delete();
            deleted |= new File(file.getPath() + "-shm").delete();
            deleted |= new File(file.getPath() + "-wal").delete();

            File dir = file.getParentFile();
            if (dir != null) {
                String prefix = file.getName() + "-mj";
                FileFilter filter = new FileFilter() {
                    @Override
                    public boolean accept(File candidate) {
                        return candidate.getName().startsWith(prefix);
                    }
                };
                for (File masterJournal : dir.listFiles(filter)) deleted |= masterJournal.delete();
            }
            return deleted;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            releaseReference();
        }
        return false;
    }

    /**
     * Close.
     */
    @Override
    public synchronized void close() {
//        releaseReference();
    }

    /**
     * refCountIsZero 降到0时自动触发释放各种资源
     */
//    @Override
    protected void onAllReferencesReleased() {
        this.justRelease();
    }

    /**
     * Just release.
     */
    private void justRelease() {
        if (this.mHelper != null) {
            this.mHelper.getRdbStore().close();
//            mHelper.close();
            this.mHelper = null;
        }
        if (this.mTableManager != null) {
            this.mTableManager.release();
            this.mTableManager = null;
        }
    }

    /**
     * Keep mapping boolean.
     *
     * @param <E>  the type parameter
     * @param <T>  the type parameter
     * @param col1 the col 1
     * @param col2 the col 2
     * @return the boolean
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     */
    /* --------------------------------  私有方法 -------------------------------- */
    private <E, T> boolean keepMapping(Collection<E> col1,
                                       Collection<T> col2) throws IllegalAccessException, InstantiationException {
        Class claxx1 = col1.iterator().next().getClass();
        Class claxx2 = col2.iterator().next().getClass();
        EntityTable table1 = TableManager.getTable(claxx1);
        EntityTable table2 = TableManager.getTable(claxx2);
        if (table1.mappingList != null) for (MapProperty mp : table1.mappingList) {
            Class itemClass;
            Class fieldClass = mp.field.getType();
            // N对多关系
            if (mp.isToMany())
                if (ClassUtil.isCollection(fieldClass)) itemClass = FieldUtil.getGenericType(mp.field);
                else if (fieldClass.isArray()) itemClass = FieldUtil.getComponentType(mp.field);
                else
                    throw new RuntimeException(
                            "OneToMany and ManyToMany Relation, Must use collection or array object");
            else
                itemClass = fieldClass;
            if (itemClass == claxx2) {
                ArrayList<String> key1List = new ArrayList<String>();
                HashMap<String, Object> map1 = new HashMap<String, Object>();
                // 构建第1个集合对象的key集合以及value映射
                for (Object o1 : col1)
                    if (o1 != null) {
                        Object key1 = FieldUtil.get(table1.key.field, o1);
                        if (key1 != null) {
                            key1List.add(key1.toString());
                            map1.put(key1.toString(), o1);
                        }
                    }
                ArrayList<RelationKey> relationKeys = this.queryRelation(claxx1, claxx2, key1List);
                if (!Checker.isEmpty(relationKeys)) {
                    HashMap<String, Object> map2 = new HashMap<String, Object>();
                    // 构建第2个对象的value映射
                    for (Object o2 : col2)
                        if (o2 != null) {
                            Object key2 = FieldUtil.get(table2.key.field, o2);
                            if (key2 != null) map2.put(key2.toString(), o2);
                        }
                    HashMap<Object, ArrayList> collMap = new HashMap<Object, ArrayList>();
                    for (RelationKey m : relationKeys) {
                        Object obj1 = map1.get(m.key1);
                        Object obj2 = map2.get(m.key2);
                        if (obj1 != null && obj2 != null) if (mp.isToMany()) {
                            // N对多关系
                            ArrayList col = collMap.get(obj1);
                            if (col == null) {
                                col = new ArrayList();
                                collMap.put(obj1, col);
                            }
                            col.add(obj2);
                        } else FieldUtil.set(mp.field, obj1, obj2);
                    }
                    // N对多关系,查出来的数组
                    if (!Checker.isEmpty(collMap)) for (Map.Entry<Object, ArrayList> entry : collMap.entrySet()) {
                        Object obj1 = entry.getKey();
                        Collection tempColl = entry.getValue();
                        if (ClassUtil.isCollection(itemClass)) {
                            Collection col = (Collection) FieldUtil.get(mp.field, obj1);
                            if (col == null) FieldUtil.set(mp.field, obj1, tempColl);
                            else col.addAll(tempColl);
                        } else if (ClassUtil.isArray(itemClass)) {
                            Object[] tempArray = (Object[]) ClassUtil.newArray(itemClass, tempColl.size());
                            tempColl.toArray(tempArray);
                            Object[] array = (Object[]) FieldUtil.get(mp.field, obj1);
                            if (array == null) FieldUtil.set(mp.field, obj1, tempArray);
                            else {
                                Object[] newArray = DataUtil.concat(array, tempArray);
                                FieldUtil.set(mp.field, obj1, newArray);
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
