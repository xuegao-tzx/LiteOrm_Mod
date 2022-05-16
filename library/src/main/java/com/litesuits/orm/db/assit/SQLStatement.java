package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Querier.CursorParser;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapInfo;
import com.litesuits.orm.db.model.MapInfo.MapTable;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.log.OrmLog;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.Statement;
import ohos.data.resultset.ResultSet;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * sql语句构造与执行
 *
 * @author mty
 * @date 2013 -6-14下午7:48:34
 */
public class SQLStatement implements Serializable {
    /**
     * The constant NONE.
     */
    public static final short NONE = -1;
    /**
     * The constant NORMAL.
     */
    public static final short NORMAL = 0;
    /**
     * The constant IN_TOP_LIMIT.
     */
    public static final int IN_TOP_LIMIT = 999;
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = -3790876762607683712L;
    /**
     * The constant TAG.
     */
    private static final String TAG = SQLStatement.class.getSimpleName();
    /**
     * sql语句
     */
    public String sql;
    /**
     * sql语句中占位符对应的参数
     */
    public Object[] bindArgs;
    /**
     * sql语句执行者，私有(private)。
     */
    private Statement mStatement;

    /**
     * Instantiates a new Sql statement.
     */
    public SQLStatement() {
    }

    /**
     * Instantiates a new Sql statement.
     *
     * @param sql  the sql
     * @param args the args
     */
    public SQLStatement(String sql, Object[] args) {
        this.sql = sql;
        this.bindArgs = args;
    }

    /**
     * 重新映射关系到数据库
     *
     * @param entity       the entity
     * @param insertNew    仅在执行删除该实体时，此值为false
     * @param tableCheck   the table check
     * @param db           the db
     * @param tableManager the table manager
     */
    private static void mapRelationToDb(Object entity, boolean insertNew,
                                        boolean tableCheck, RdbStore db,
                                        TableManager tableManager) {
        // 插入关系映射
        MapInfo mapTable = SQLBuilder.buildMappingInfo(entity, insertNew, tableManager);
        if (mapTable != null && !mapTable.isEmpty()) Transaction.execute(db, new Transaction.Worker<Boolean>() {
            @Override
            public Boolean doTransaction(RdbStore db) throws Exception {
                if (insertNew && tableCheck) for (MapTable table : mapTable.tableList)
                    tableManager.checkOrCreateMappingTable(db, table.name, table.column1, table.column2);
                if (mapTable.delOldRelationSQL != null) for (SQLStatement st : mapTable.delOldRelationSQL) {
                    long rowId = st.execDelete(db);
                    if (OrmLog.isPrint)
                        OrmLog.v(SQLStatement.TAG, "Exec delete mapping success, nums: " + rowId);
                }
                if (insertNew && mapTable.mapNewRelationSQL != null)
                    for (SQLStatement st : mapTable.mapNewRelationSQL) {
                        long rowId = st.execInsert(db);
                        if (OrmLog.isPrint) OrmLog.v(SQLStatement.TAG, "Exec save mapping success, nums: " + rowId);
                    }
                return true;
            }
        });
    }

    /**
     * 给sql语句的占位符(?)按序绑定值
     *
     * @param i The 1-based index to the parameter to bind null to
     * @param o the o
     * @throws IOException the io exception
     */
    private void bind(int i, Object o) throws IOException {
        if (o == null) this.mStatement.setNull(i);//.bindNull(i);
        else if (o instanceof CharSequence || o instanceof Boolean || o instanceof Character)
            this.mStatement.setString(i, String.valueOf(o));
        else if (o instanceof Float || o instanceof Double) this.mStatement.setDouble(i, ((Number) o).doubleValue());
        else if (o instanceof Number) this.mStatement.setLong(i, ((Number) o).longValue());
        else if (o instanceof Date) this.mStatement.setLong(i, ((Date) o).getTime());
        else if (o instanceof byte[]) this.mStatement.setBlob(i, (byte[]) o);
        else if (o instanceof Serializable) this.mStatement.setBlob(i, DataUtil.objectToByte(o));
        else
            this.mStatement.setNull(i);
    }

    /**
     * 插入数据，未传入实体所以不可以为之注入ID。
     *
     * @param db the db
     * @return the long
     * @throws IOException            the io exception
     * @throws IllegalAccessException the illegal access exception
     */
    public long execInsert(RdbStore db) throws IOException, IllegalAccessException {
        return this.execInsertWithMapping(db, null, null);
    }

    /**
     * 插入数据，并为实体对象为之注入ID（如果需要）。
     *
     * @param db     the db
     * @param entity the entity
     * @return the long
     * @throws IOException            the io exception
     * @throws IllegalAccessException the illegal access exception
     */
    public long execInsert(RdbStore db, Object entity) throws IOException, IllegalAccessException {
        return this.execInsertWithMapping(db, entity, null);
    }

    /**
     * 插入数据，为其注入ID（如果需要），关系表也一并处理。
     *
     * @param db           the db
     * @param entity       the entity
     * @param tableManager the table manager
     * @return the long
     * @throws IllegalAccessException the illegal access exception
     * @throws IOException            the io exception
     */
    private long execInsertWithMapping(RdbStore db, Object entity, TableManager tableManager)
            throws IllegalAccessException, IOException {
        this.printSQL();
        this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
        Object keyObj = null;
        if (!Checker.isEmpty(this.bindArgs)) {
            keyObj = this.bindArgs[0];
            for (int i = 0; i < this.bindArgs.length; i++) this.bind(i + 1, this.bindArgs[i]);
        }
        //OrmLog.i(TAG, "SQL Execute bind over ");
        long rowID = NONE;
        try {
            rowID = this.mStatement.executeAndGetLastInsertRowId();//.executeInsert();
        } finally {
            this.realease();
        }
        //OrmLog.i(TAG, "SQL Execute insert over ");
        if (OrmLog.isPrint) OrmLog.i(TAG, "SQL Execute Insert RowID --> " + rowID + "    sql: " + this.sql);
        if (entity != null) FieldUtil.setKeyValueIfneed(entity, TableManager.getTable(entity).key, keyObj, rowID);
        if (tableManager != null) SQLStatement.mapRelationToDb(entity, true, true, db, tableManager);
        return rowID;
    }

    /**
     * 执行批量插入
     *
     * @param db   the db
     * @param list the list
     * @return the int
     */
    public int execInsertCollection(RdbStore db, Collection<?> list) {
        return this.execInsertCollectionWithMapping(db, list, null);
    }

    /**
     * Exec insert collection with mapping int.
     *
     * @param db           the db
     * @param list         the list
     * @param tableManager the table manager
     * @return the int
     */
    private int execInsertCollectionWithMapping(RdbStore db, Collection<?> list, TableManager tableManager) {
        this.printSQL();
        db.beginTransaction();
        if (OrmLog.isPrint) OrmLog.i(TAG, "----> BeginTransaction[insert col]");
        EntityTable table = null;
        try {
            this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
            Iterator<?> it = list.iterator();
            boolean mapTableCheck = true;
            while (it.hasNext()) {
                this.mStatement.clearValues();//.clearBindings();
                Object obj = it.next();

                if (table == null) table = TableManager.getTable(obj);

                int j = 1;
                Object keyObj = null;
                if (table.key != null) {
                    keyObj = FieldUtil.getAssignedKeyObject(table.key, obj);
                    this.bind(j++, keyObj);
                }
                // 第一个是主键。其他属性从2开始。
                if (!Checker.isEmpty(table.pmap))
                    for (Property p : table.pmap.values()) this.bind(j++, FieldUtil.get(p.field, obj));
                long rowID = this.mStatement.executeAndGetLastInsertRowId();//.executeInsert();
                FieldUtil.setKeyValueIfneed(obj, table.key, keyObj, rowID);
                if (tableManager != null) {
                    SQLStatement.mapRelationToDb(obj, true, mapTableCheck, db, tableManager);
                    mapTableCheck = false;
                }
            }
            if (OrmLog.isPrint) OrmLog.i(TAG, "Exec insert [" + list.size() + "] rows , SQL: " + this.sql);
            db.markAsCommit();//.setTransactionSuccessful();
            if (OrmLog.isPrint) OrmLog.i(TAG, "----> BeginTransaction[insert col] Successful");
            return list.size();
        } catch (Exception e) {
            if (OrmLog.isPrint) OrmLog.e(TAG, "----> BeginTransaction[insert col] Failling");
            e.printStackTrace();
        } finally {
            this.realease();
            db.endTransaction();
        }
        return NONE;
    }

    /**
     * 执行更新单个数据，返回受影响的行数
     *
     * @param db the db
     * @return the int
     * @throws IOException the io exception
     */
    public int execUpdate(RdbStore db) throws IOException {
        return this.execUpdateWithMapping(db, null, null);
    }

    /**
     * 执行更新单个数据，返回受影响的行数
     *
     * @param db           the db
     * @param entity       the entity
     * @param tableManager the table manager
     * @return the int
     * @throws IOException the io exception
     */
    private int execUpdateWithMapping(RdbStore db, Object entity, TableManager tableManager) throws IOException {
        this.printSQL();
        this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
        if (!Checker.isEmpty(this.bindArgs))
            for (int i = 0; i < this.bindArgs.length; i++) this.bind(i + 1, this.bindArgs[i]);
        int rows = NONE;
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mStatement.execute();
            rows = NORMAL;
        } else */
        {
            rows = this.mStatement.executeAndGetChanges();//.executeUpdateDelete();
        }
        this.realease();
        if (OrmLog.isPrint) OrmLog.i(TAG, "SQL Execute update, changed rows --> " + rows);
        if (tableManager != null && entity != null) SQLStatement.mapRelationToDb(entity, true, true, db, tableManager);
        return rows;
    }

    /**
     * 执行批量更新
     *
     * @param db   the db
     * @param list the list
     * @param cvs  the cvs
     * @return the int
     */
    public int execUpdateCollection(RdbStore db, Collection<?> list, ColumnsValue cvs) {
        return this.execUpdateCollectionWithMapping(db, list, cvs, null);
    }

    /**
     * 执行批量更新
     *
     * @param db           the db
     * @param list         the list
     * @param cvs          the cvs
     * @param tableManager the table manager
     * @return the int
     */
    private int execUpdateCollectionWithMapping(RdbStore db, Collection<?> list,
                                                ColumnsValue cvs, TableManager tableManager) {
        this.printSQL();
        db.beginTransaction();
        if (OrmLog.isPrint) OrmLog.d(TAG, "----> BeginTransaction[update col]");
        try {
            this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
            Iterator<?> it = list.iterator();
            boolean mapTableCheck = true;
            EntityTable table = null;
            while (it.hasNext()) {
                this.mStatement.clearValues();//.clearBindings();
                Object obj = it.next();
                if (table == null) table = TableManager.getTable(obj);
                this.bindArgs = SQLBuilder.buildUpdateSqlArgsOnly(obj, cvs);
                if (!Checker.isEmpty(this.bindArgs))
                    for (int i = 0; i < this.bindArgs.length; i++) this.bind(i + 1, this.bindArgs[i]);
                this.mStatement.execute();
                if (tableManager != null) {
                    SQLStatement.mapRelationToDb(obj, true, mapTableCheck, db, tableManager);
                    mapTableCheck = false;
                }
            }
            if (OrmLog.isPrint) OrmLog.i(TAG, "Exec update [" + list.size() + "] rows , SQL: " + this.sql);
            db.markAsCommit();//.setTransactionSuccessful();
            if (OrmLog.isPrint) OrmLog.d(TAG, "----> BeginTransaction[update col] Successful");
            return list.size();
        } catch (Exception e) {
            if (OrmLog.isPrint) OrmLog.e(TAG, "----> BeginTransaction[update col] Failling");
            e.printStackTrace();
        } finally {
            this.realease();
            db.endTransaction();
        }
        return NONE;
    }

    /**
     * 删除语句执行，返回受影响的行数
     *
     * @param db the db
     * @return the int
     * @throws IOException the io exception
     */
    public int execDelete(RdbStore db) throws IOException {
        return this.execDeleteWithMapping(db, null, null);
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     *
     * @param db           the db
     * @param entity       the entity
     * @param tableManager the table manager
     * @return the int
     * @throws IOException the io exception
     */
    private int execDeleteWithMapping(RdbStore db, Object entity, TableManager tableManager)
            throws IOException {
        this.printSQL();
        this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
        if (this.bindArgs != null) for (int i = 0; i < this.bindArgs.length; i++) this.bind(i + 1, this.bindArgs[i]);
        int nums = NONE;
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mStatement.execute();
            nums = NORMAL;
        } else */
        {
            nums = this.mStatement.executeAndGetChanges();//.executeUpdateDelete();
        }
        if (OrmLog.isPrint) OrmLog.v(TAG, "SQL execute delete, changed rows--> " + nums);
        this.realease();
        // 删除关系映射
        if (tableManager != null && entity != null)
            SQLStatement.mapRelationToDb(entity, false, false, db, tableManager);
        return nums;
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     *
     * @param db         the db
     * @param collection the collection
     * @return the int
     * @throws IOException the io exception
     */
    public int execDeleteCollection(RdbStore db, Collection<?> collection) throws IOException {
        return this.execDeleteCollectionWithMapping(db, collection, null);
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     *
     * @param db           the db
     * @param collection   the collection
     * @param tableManager the table manager
     * @return the int
     * @throws IOException the io exception
     */
    private int execDeleteCollectionWithMapping(RdbStore db, Collection<?> collection,
                                                TableManager tableManager) throws IOException {
        this.printSQL();
        // 删除全部数据
        this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
        if (this.bindArgs != null) for (int i = 0; i < this.bindArgs.length; i++) this.bind(i + 1, this.bindArgs[i]);
        int nums;
       /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mStatement.execute();
            nums = collection.size();
        } else */
        {
            nums = this.mStatement.executeAndGetChanges();//.executeUpdateDelete();
        }
        if (OrmLog.isPrint) OrmLog.v(TAG, "SQL execute delete, changed rows --> " + nums);
        this.realease();
        if (tableManager != null) {
            // 删除关系映射
            Boolean suc = Transaction.execute(db, new Transaction.Worker<Boolean>() {
                @Override
                public Boolean doTransaction(RdbStore db) throws Exception {
                    boolean mapTableCheck = true;
                    for (Object o : collection) {
                        // 删除关系映射
                        SQLStatement.mapRelationToDb(o, false, mapTableCheck, db, tableManager);
                        mapTableCheck = false;
                    }
                    return true;
                }
            });
            if (OrmLog.isPrint)
                OrmLog.i(TAG, "Exec delete collection mapping: " + ((suc != null && suc) ? "成功" : "失败"));
        }
        return nums;
    }

    /**
     * 执行create,drop table 等
     *
     * @param db the db
     * @return the boolean
     */
    public boolean execute(RdbStore db) {
        this.printSQL();
        try {
            this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
            if (this.bindArgs != null)
                for (int i = 0; i < this.bindArgs.length; i++) this.bind(i + 1, this.bindArgs[i]);
            this.mStatement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.realease();
        }
        return false;
    }

    /**
     * Execute a statement that returns a 1 by 1 table with a numeric value.
     * For example, SELECT COUNT(*) FROM table;
     *
     * @param db the db
     * @return the long
     */
    public long queryForLong(RdbStore db) {
        this.printSQL();
        long count = 0;
        try {
            this.mStatement = db.buildStatement(this.sql);//.compileStatement(sql);
            if (this.bindArgs != null)
                for (int i = 0; i < this.bindArgs.length; i++) this.bind(i + 1, this.bindArgs[i]);
            count = this.mStatement.executeAndGetLong();//.simpleQueryForLong();
            if (OrmLog.isPrint) OrmLog.i(TAG, "SQL execute query for count --> " + count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.realease();
        }
        return count;
    }

    /**
     * 执行查询
     * 根据类信息读取数据库，取出全部本类的对象。
     *
     * @param <T>   the type parameter
     * @param db    the db
     * @param claxx the claxx
     * @return the array list
     */
    public <T> ArrayList<T> query(RdbStore db, Class<T> claxx) {
        this.printSQL();
        ArrayList<T> list = new ArrayList<T>();
        try {
            EntityTable table = TableManager.getTable(claxx, false);
            Querier.doQuery(db, this, new CursorParser() {
                @Override
                public void parseEachCursor(RdbStore db, ResultSet c) throws Exception {
                    //long start = System.nanoTime();
                    T t = ClassUtil.newInstance(claxx);
                    //Log.i(TAG, "parse new after  " + ((System.nanoTime() - start)/1000));
                    //start = System.nanoTime();
                    DataUtil.injectDataToObject(c, t, table);
                    //Log.i(TAG, "parse inject after  " +  ((System.nanoTime() - start)/1000));
                    list.add(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 执行查询
     * 根据类信息读取数据库，取出本类的对象。
     *
     * @param <T>   the type parameter
     * @param db    the db
     * @param claxx the claxx
     * @return the t
     */
    public <T> T queryOneEntity(RdbStore db, Class<T> claxx) {
        this.printSQL();
        EntityTable table = TableManager.getTable(claxx, false);
        T t = Querier.doQuery(db, this, new CursorParser<T>() {
            T t;

            @Override
            public void parseEachCursor(RdbStore db, ResultSet c) throws Exception {
                this.t = ClassUtil.newInstance(claxx);
                DataUtil.injectDataToObject(c, this.t, table);
                this.stopParse();
            }

            @Override
            public T returnResult() {
                return this.t;
            }
        });
        return t;
    }
    /*------------------------------ 私有方法 ------------------------------*/

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "SQLStatement [sql=" + this.sql + ", bindArgs=" + Arrays.toString(this.bindArgs) + ", mStatement=" + this.mStatement
                + "]";
    }

    /**
     * Print sql.
     */
    private void printSQL() {
        if (OrmLog.isPrint) OrmLog.d(TAG, "SQL Execute: [" + this.sql + "] ARGS--> " + Arrays.toString(this.bindArgs));
    }

    /**
     * Realease.
     */
    private void realease() {
        if (this.mStatement != null) this.mStatement.close();
        //sql = null;
        this.bindArgs = null;
        this.mStatement = null;
    }


}
