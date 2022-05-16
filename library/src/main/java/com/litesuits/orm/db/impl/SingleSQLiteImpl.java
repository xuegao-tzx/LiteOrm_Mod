package com.litesuits.orm.db.impl;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.*;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.EntityTable;
import ohos.data.rdb.RdbStore;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 数据SQLite操作实现
 * 可查阅 <a href="http://www.sqlite.org/lang.html">SQLite操作指南</a>
 *
 * @author mty
 * @date 2013 -6-2下午2:32:56
 */
public final class SingleSQLiteImpl extends LiteOrm {

    /**
     * The constant TAG.
     */
    public static final String TAG = SingleSQLiteImpl.class.getSimpleName();

    /**
     * Instantiates a new Single sq lite.
     *
     * @param dataBase the data base
     */
    SingleSQLiteImpl(LiteOrm dataBase) {
        super(dataBase);
    }

    /**
     * Instantiates a new Single sq lite.
     *
     * @param config the config
     */
    private SingleSQLiteImpl(DataBaseConfig config) {
        super(config);
    }


    /**
     * New instance lite orm.
     *
     * @param config the config
     * @return the lite orm
     */
    public synchronized static LiteOrm newInstance(DataBaseConfig config) {
        return new SingleSQLiteImpl(config);
    }

    /**
     * Single lite orm.
     *
     * @return the lite orm
     */
    @Override
    public LiteOrm single() {
        return this;
    }

    /**
     * Cascade lite orm.
     *
     * @return the lite orm
     */
    @Override
    public LiteOrm cascade() {
        if (this.otherDatabase == null) this.otherDatabase = new CascadeSQLiteImpl(this);
        return this.otherDatabase;
    }

    /**
     * Save long.
     *
     * @param entity the entity
     * @return the long
     */
    @Override
    public long save(Object entity) {
        //acquireReference();
        try {
            RdbStore db = this.mHelper.getRdbStore();
            this.mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildReplaceSql(entity).execInsert(db, entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Save int.
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the int
     */
    @Override
    public <T> int save(Collection<T> collection) {
        //acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                RdbStore db = this.mHelper.getRdbStore();
                Object entity = collection.iterator().next();
                this.mTableManager.checkOrCreateTable(db, entity);
                SQLStatement stmt = SQLBuilder.buildReplaceAllSql(entity);
                return stmt.execInsertCollection(db, collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Insert long.
     *
     * @param entity the entity
     * @return the long
     */
    @Override
    public long insert(Object entity) {
        return this.insert(entity, null);
    }

    /**
     * Insert long.
     *
     * @param entity            the entity
     * @param conflictAlgorithm the conflict algorithm
     * @return the long
     */
    @Override
    public long insert(Object entity, ConflictAlgorithm conflictAlgorithm) {
        //acquireReference();
        try {
            RdbStore db = this.mHelper.getRdbStore();
            this.mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildInsertSql(entity, conflictAlgorithm).execInsert(db, entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Insert int.
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the int
     */
    @Override
    public <T> int insert(Collection<T> collection) {
        return this.insert(collection, null);
    }

    /**
     * Insert int.
     *
     * @param <T>               the type parameter
     * @param collection        the collection
     * @param conflictAlgorithm the conflict algorithm
     * @return the int
     */
    @Override
    public <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        //acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                RdbStore db = this.mHelper.getRdbStore();
                Object entity = collection.iterator().next();
                SQLStatement stmt = SQLBuilder.buildInsertAllSql(entity, conflictAlgorithm);
                this.mTableManager.checkOrCreateTable(db, entity);
                return stmt.execInsertCollection(db, collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Update int.
     *
     * @param entity the entity
     * @return the int
     */
    @Override
    public int update(Object entity) {
        return this.update(entity, null, null);
    }

    /**
     * Update int.
     *
     * @param entity            the entity
     * @param conflictAlgorithm the conflict algorithm
     * @return the int
     */
    @Override
    public int update(Object entity, ConflictAlgorithm conflictAlgorithm) {
        return this.update(entity, null, conflictAlgorithm);
    }

    /**
     * Update int.
     *
     * @param entity            the entity
     * @param cvs               the cvs
     * @param conflictAlgorithm the conflict algorithm
     * @return the int
     */
    @Override
    public int update(Object entity, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        //acquireReference();
        try {
            RdbStore db = this.mHelper.getRdbStore();
            this.mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm).execUpdate(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Update int.
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the int
     */
    @Override
    public <T> int update(Collection<T> collection) {
        return this.update(collection, null, null);
    }

    /**
     * Update int.
     *
     * @param <T>               the type parameter
     * @param collection        the collection
     * @param conflictAlgorithm the conflict algorithm
     * @return the int
     */
    @Override
    public <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        return this.update(collection, null, conflictAlgorithm);
    }

    /**
     * Update int.
     *
     * @param <T>               the type parameter
     * @param collection        the collection
     * @param cvs               the cvs
     * @param conflictAlgorithm the conflict algorithm
     * @return the int
     */
    @Override
    public <T> int update(Collection<T> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        //acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                RdbStore db = this.mHelper.getRdbStore();
                Object entity = collection.iterator().next();
                this.mTableManager.checkOrCreateTable(db, entity);
                SQLStatement stmt = SQLBuilder.buildUpdateAllSql(entity, cvs, conflictAlgorithm);
                return stmt.execUpdateCollection(db, collection, cvs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Delete int.
     *
     * @param entity the entity
     * @return the int
     */
    @Override
    public int delete(Object entity) {
        EntityTable table = TableManager.getTable(entity);
        //acquireReference();
        if (this.mTableManager.isSQLTableCreated(table.name)) try {
            RdbStore db = this.mHelper.getRdbStore();
            return SQLBuilder.buildDeleteSql(entity).execDelete(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Delete int.
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the int
     */
    @Override
    public <T> int delete(Class<T> claxx) {
        return this.deleteAll(claxx);
    }

    /**
     * Delete int.
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the int
     */
    @Override
    public <T> int delete(Collection<T> collection) {
        //acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                EntityTable table = TableManager.getTable(collection.iterator().next());
                if (this.mTableManager.isSQLTableCreated(table.name)) {
                    int rows;
                    RdbStore db = this.mHelper.getRdbStore();
                    db.beginTransaction();
                    try {
                        rows = CollSpliter.split(collection, SQLStatement.IN_TOP_LIMIT, new CollSpliter.Spliter<T>() {
                            @Override
                            public int oneSplit(ArrayList<T> list) throws Exception {
                                return SQLBuilder.buildDeleteSql(list).execDeleteCollection(db, list);
                            }
                        });
                        db.markAsCommit();//.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    return rows;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Delete int.
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @param where the where
     * @return the int
     */
    @Override
    @Deprecated
    public <T> int delete(Class<T> claxx, WhereBuilder where) {
        return this.delete(where);
    }

    /**
     * Delete int.
     *
     * @param where the where
     * @return the int
     */
    @Override
    public int delete(WhereBuilder where) {
        EntityTable table = TableManager.getTable(where.getTableClass(), false);
        //acquireReference();
        if (this.mTableManager.isSQLTableCreated(table.name)) try {
            RdbStore db = this.mHelper.getRdbStore();
            return where.createStatementDelete().execDelete(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Delete all int.
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the int
     */
    @Override
    public <T> int deleteAll(Class<T> claxx) {
        EntityTable table = TableManager.getTable(claxx, false);
        //acquireReference();
        if (this.mTableManager.isSQLTableCreated(table.name)) try {
            RdbStore db = this.mHelper.getRdbStore();
            SQLStatement stmt = SQLBuilder.buildDeleteAllSql(claxx);
            return stmt.execDelete(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * 删除从[start,end]的数据
     *
     * @param <T>            the type parameter
     * @param claxx          the claxx
     * @param start          the start
     * @param end            the end
     * @param orderAscColumn the order asc column
     * @return the int
     */
    @Override
    public <T> int delete(Class<T> claxx, long start, long end, String orderAscColumn) {
        EntityTable table = TableManager.getTable(claxx, false);
        //acquireReference();
        if (this.mTableManager.isSQLTableCreated(table.name)) try {
            if (start < 0 || end < start) throw new RuntimeException("start must >=0 and smaller than end");
            if (start != 0) start -= 1;
            end = end == Integer.MAX_VALUE ? -1 : end - start;
            SQLStatement stmt = SQLBuilder.buildDeleteSql(claxx, start, end, orderAscColumn);
            RdbStore db = this.mHelper.getRdbStore();
            return stmt.execDelete(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * Query array list.
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the array list
     */
    @Override
    public <T> ArrayList<T> query(Class<T> claxx) {
        return this.query(new QueryBuilder<T>(claxx));
    }

    /**
     * Query array list.
     *
     * @param <T> the type parameter
     * @param qb  the qb
     * @return the array list
     */
    @Override
    public <T> ArrayList<T> query(QueryBuilder<T> qb) {
        EntityTable table = TableManager.getTable(qb.getQueryClass(), false);
        //acquireReference();
        if (this.mTableManager.isSQLTableCreated(table.name)) try {
            return qb.createStatement().query(this.mHelper.getRdbStore(), qb.getQueryClass());
        } finally {
            //releaseReference();
        }
        else return new ArrayList<T>();
    }

    /**
     * Query by id t.
     *
     * @param <T>   the type parameter
     * @param id    the id
     * @param claxx the claxx
     * @return the t
     */
    @Override
    public <T> T queryById(long id, Class<T> claxx) {
        return this.queryById(String.valueOf(id), claxx);
    }

    /**
     * Query by id t.
     *
     * @param <T>   the type parameter
     * @param id    the id
     * @param claxx the claxx
     * @return the t
     */
    @Override
    public <T> T queryById(String id, Class<T> claxx) {
        EntityTable table = TableManager.getTable(claxx, false);
        //acquireReference();
        if (this.mTableManager.isSQLTableCreated(table.name)) try {
            SQLStatement stmt = new QueryBuilder<T>(claxx)
                    .where(table.key.column + "=?", id)
                    .createStatement();
            ArrayList<T> list = stmt.query(this.mHelper.getRdbStore(), claxx);
            if (!Checker.isEmpty(list)) return list.get(0);
        } finally {
            //releaseReference();
        }
        return null;
    }

}
