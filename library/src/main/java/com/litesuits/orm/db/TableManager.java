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
package com.litesuits.orm.db;


import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.*;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.model.*;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.log.OrmLog;
import ohos.data.rdb.RdbStore;
import ohos.data.resultset.ResultSet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 表管理
 *
 * @author MaTianyu
 * @date 2013 -6-16上午12:27:32
 */
public final class TableManager {
    /**
     * The constant TAG.
     */
    private static final String TAG = TableManager.class.getSimpleName();
    /**
     * The constant ID.
     */
    private static final String[] ID = new String[]{"id", "_id"};
    /**
     * 这里放的是类的实体信息表（主键、属性、关系映射...）
     * 全局单例
     * key : Class Name
     * value: {@link EntityTable}
     */
    private final static HashMap<String, EntityTable> mEntityTableMap = new HashMap<String, EntityTable>();
    /**
     * 这里放的是数据库表信息（表名、字段、建表语句...）
     * 每个数据库对应一个
     * key : Class Name
     * value: {@link EntityTable}
     */
    private final HashMap<String, SQLiteTable> mSqlTableMap = new HashMap<String, SQLiteTable>();
    /**
     * 数据库表信息
     */
    private String dbName = "";

    /**
     * Instantiates a new Table manager.
     *
     * @param dbName the db name
     * @param db     the db
     */
    public TableManager(String dbName, RdbStore db) {
        this.dbName = dbName;
        this.initSqlTable(db);
    }

    /**
     * 获取缓存实体表信息
     *
     * @param name the name
     * @return the entity table
     */
    private static EntityTable getEntityTable(String name) {
        return mEntityTableMap.get(name);
    }

    /**
     * 缓存的实体表信息
     *
     * @param tableName the table name
     * @param entity    the entity
     * @return 返回前一个和此Key相同的Value ，没有则返回null。
     */
    private static EntityTable putEntityTable(String tableName, EntityTable entity) {
        return mEntityTableMap.put(tableName, entity);
    }

    /**
     * 根据实体生成表信息,一定需要PrimaryKey
     *
     * @param entity the entity
     * @return the table
     */
    public static EntityTable getTable(Object entity) {
        return getTable(entity.getClass(), true);
    }

    /**
     * 根据类生成表信息,一定需要PrimaryKey
     *
     * @param claxx the claxx
     * @return the table
     */
    public static EntityTable getTable(Class<?> claxx) {
        return getTable(claxx, true);
    }

    /**
     * 获取实体表信息(Entity Table)
     * 注意映射表存储在MAP中，key 为 class name， value 为 entity table。
     *
     * @param claxx  the claxx
     * @param needPK the need pk
     * @return {@link EntityTable}
     */
    public static synchronized EntityTable getTable(Class<?> claxx, boolean needPK) {
        EntityTable table = getEntityTable(claxx.getName());
        //if(OrmLog.isPrint)OrmLog.i(TAG, "table : " + table + "  , claxx: " + claxx);
        if (table == null) {
            table = new EntityTable();
            table.claxx = claxx;
            table.name = getTableName(claxx);
            table.pmap = new LinkedHashMap<String, Property>();
            List<Field> fields = FieldUtil.getAllDeclaredFields(claxx);
            for (Field f : fields) {
                if (FieldUtil.isInvalid(f)) continue;

                // 获取列名,每个属性都有，没有注解默认取属性名
                Column col = f.getAnnotation(Column.class);
                String column = col != null ? col.value() : f.getName();
                Property p = new Property(column, f);


                // 主键判断
                PrimaryKey key = f.getAnnotation(PrimaryKey.class);
                if (key != null) {
                    // 主键不加入属性Map
                    table.key = new Primarykey(p, key.value());
                    // 主键为系统分配,对类型有要求
                    checkPrimaryKey(table.key);
                } else {
                    //ORM handle
                    Mapping mapping = f.getAnnotation(Mapping.class);
                    if (mapping != null) table.addMapping(new MapProperty(p, mapping.value()));
                    else
                        table.pmap.put(p.column, p);
                }
            }
            if (table.key == null) for (String col : table.pmap.keySet()) {
                for (String id : ID)
                    if (id.equalsIgnoreCase(col)) {
                        Property p = table.pmap.get(col);
                        if (p.field.getType() == String.class) {
                            // 主键移除属性Map
                            table.pmap.remove(col);
                            table.key = new Primarykey(p, AssignType.BY_MYSELF);
                            break;
                        } else if (FieldUtil.isNumber(p.field.getType())) {
                            // 主键移除属性Map
                            table.pmap.remove(col);
                            table.key = new Primarykey(p, AssignType.AUTO_INCREMENT);
                            break;
                        }

                    }
                if (table.key != null) break;
            }
            if (needPK && table.key == null) throw new RuntimeException(
                    "你必须为[" + table.claxx.getSimpleName() + "]设置主键(you must set the primary key...)" +
                            "\n 提示：在对象的属性上加PrimaryKey注解来设置主键。");
            putEntityTable(claxx.getName(), table);
        }
        return table;
    }

    /**
     * Check primary key.
     *
     * @param key the key
     */
    private static void checkPrimaryKey(Primarykey key) {
        if (key.isAssignedBySystem()) {
            if (!FieldUtil.isNumber(key.field.getType())) throw new RuntimeException(
                    AssignType.AUTO_INCREMENT
                            + " Auto increment primary key must be a number ...\n " +
                            "错误提示：自增主键必须设置为数字类型");
        } else if (key.isAssignedByMyself()) {
            if (String.class != key.field.getType() && !FieldUtil.isNumber(key.field.getType()))
                throw new RuntimeException(
                        AssignType.BY_MYSELF
                                + " Custom primary key must be string or number ...\n " +
                                "错误提示：自定义主键值必须为String或者Number类型");
        } else throw new RuntimeException(
                " Primary key without Assign Type ...\n " +
                        "错误提示：主键无类型");
    }

    /**
     * 根据类自动生成表名字
     *
     * @param claxx the claxx
     * @return the table name
     */
    public static String getTableName(Class<?> claxx) {
        Table anno = claxx.getAnnotation(Table.class);
        if (anno != null) return anno.value();
        else return claxx.getName().replaceAll("\\.", "_");
    }

    /**
     * Gets map table name.
     *
     * @param c1 the c 1
     * @param c2 the c 2
     * @return the map table name
     */
    public static String getMapTableName(Class c1, Class c2) {
        return getMapTableName(getTableName(c1), getTableName(c2));
    }

    /**
     * Gets map table name.
     *
     * @param t1 the t 1
     * @param t2 the t 2
     * @return the map table name
     */
    public static String getMapTableName(EntityTable t1, EntityTable t2) {
        return getMapTableName(t1.name, t2.name);
    }

    /**
     * Gets map table name.
     *
     * @param tableName1 the table name 1
     * @param tableName2 the table name 2
     * @return the map table name
     */
    private static String getMapTableName(String tableName1, String tableName2) {
        if (tableName1.compareTo(tableName2) < 0) return tableName1 + "_" + tableName2;
        else
            return tableName2 + "_" + tableName1;
    }

    /**
     * 插入新列
     *
     * @param db        the db
     * @param tableName the table name
     * @param columns   the columns
     * @return the int
     */
    private static int insertNewColunms(RdbStore db, String tableName, List<String> columns) {
        Integer size = null;
        if (!Checker.isEmpty(columns)) size = Transaction.execute(db, new Transaction.Worker<Integer>() {
            @Override
            public Integer doTransaction(RdbStore db) {
                for (String c : columns) {
                    SQLStatement stmt = SQLBuilder.buildAddColumnSql(tableName, c);
                    stmt.execute(db);
                }
                return columns.size();
            }
        });
        return size == null ? 0 : size;
    }

    /**
     * 建立新表
     *
     * @param db    the db
     * @param table the table
     * @return the boolean
     */
    private static boolean createTable(RdbStore db, EntityTable table) {
        return SQLBuilder.buildCreateTable(table).execute(db);
    }

    /**
     * 数据库分析
     * 通过读数据库得到一张表的全部列名
     *
     * @param db        the db
     * @param tableName the table name
     * @return the all columns from sq lite
     */
    private static ArrayList<String> getAllColumnsFromSQLite(RdbStore db, String tableName) {
        EntityTable table = getTable(SQLiteColumn.class, false);
        ArrayList<String> list = new ArrayList<String>();

        SQLStatement st = SQLBuilder.buildColumnsObtainAll(tableName);
        Querier.doQuery(db, st, new Querier.CursorParser() {
            @Override
            public void parseEachCursor(RdbStore db, ResultSet c) throws Exception {
                SQLiteColumn col = new SQLiteColumn();
                DataUtil.injectDataToObject(c, col, table);
                list.add(col.name);
            }
        });

        return list;
    }

    /**
     * 语义分析
     * 依据表的sql“CREATE TABLE”建表语句得到一张表的全部列名。
     *
     * @param sql the sql
     * @return the array list
     */
    private static ArrayList<String> transformSqlToColumns(String sql) {
        if (sql != null) {
            int start = sql.indexOf("(");
            int end = sql.lastIndexOf(")");
            if (start > 0 && end > 0) {
                sql = sql.substring(start + 1, end);
                String[] cloumns = sql.split(",");
                ArrayList<String> colList = new ArrayList<String>();
                for (String col : cloumns) {
                    col = col.trim();
                    int endS = col.indexOf(" ");
                    if (endS > 0) col = col.substring(0, endS);
                    colList.add(col);
                }
                OrmLog.e(TAG, "降级：语义分析表结构（" + colList + " , Origin SQL is: " + sql);
                return colList;
            }
        }
        return null;
    }

    /**
     * Init sql table.
     *
     * @param db the db
     */
    private void initSqlTable(RdbStore db) {
        // 关键点：初始化全部数据库表
        this.initAllTablesFromSQLite(db);
    }

    /* —————————————————————————— 静态私有方法 ———————————————————————— */

    /**
     * Clear sql table.
     */
    private void clearSqlTable() {
        synchronized (this.mSqlTableMap) {
            this.mSqlTableMap.clear();
        }
    }

    /**
     * 清空数据
     */
    public void release() {
        this.clearSqlTable();
        mEntityTableMap.clear();
    }

    /**
     * 检测表是否建立，没有则建一张新表。
     *
     * @param db     the db
     * @param entity the entity
     * @return the entity table
     */
    public EntityTable checkOrCreateTable(RdbStore db, Object entity) {
        return this.checkOrCreateTable(db, entity.getClass());
    }
    /* —————————————————————————— 静态公共方法 ———————————————————————— */

    /**
     * 检测[数据库表]是否建立，没有则建一张新表。
     *
     * @param db    the db
     * @param claxx the claxx
     * @return the entity table
     */
    private synchronized EntityTable checkOrCreateTable(RdbStore db, Class claxx) {
        // 关键点1：获取[实体表]
        EntityTable table = getTable(claxx);
        // 关键点2: 判断[数据库表]是否存在，是否需要新加列。
        // 关键点3：新建[数据库表]并加入表队列
        if (!this.checkExistAndColumns(db, table))
            if (TableManager.createTable(db, table)) this.putNewSqlTableIntoMap(table);
        return table;
    }

    /**
     * 检测[映射表]是否建立，没有则建一张新表。
     *
     * @param db        the db
     * @param tableName the table name
     * @param column1   the column 1
     * @param column2   the column 2
     */
    public synchronized void checkOrCreateMappingTable(RdbStore db, String tableName,
                                                       String column1, String column2) {
        // 关键点1：获取[实体表]
        EntityTable table = this.getMappingTable(tableName, column1, column2);
        // 关键点2: 判断[数据库表]是否存在，是否需要新加列。
        // 关键点3：新建[数据库表]并加入表队列
        if (!this.checkExistAndColumns(db, table))
            if (TableManager.createTable(db, table)) this.putNewSqlTableIntoMap(table);
    }

    /**
     * 仅仅检测[数据库表]是否建立
     *
     * @param tableName1 the table name 1
     * @param tableName2 the table name 2
     * @return the boolean
     */
    public boolean isSQLMapTableCreated(String tableName1, String tableName2) {
        return this.mSqlTableMap.get(getMapTableName(tableName1, tableName2)) != null;
    }

    /**
     * 仅仅检测[数据库表]是否建立
     *
     * @param tableName the table name
     * @return the boolean
     */
    public boolean isSQLTableCreated(String tableName) {
        return this.mSqlTableMap.get(tableName) != null;
    }

    /**
     * 检查表是否存在，存在的话检查是否需要改动，添加列字段。
     * 注：sqlite仅仅支持表改名、表添加列两种alter方式。表中修改、刪除列是不被直接支持的。
     * 不能新加主键：The column may not have a PRIMARY KEY or UNIQUE constraint.
     * <p> http://www.sqlite.org/lang_altertable.html
     *
     * @param db          the db
     * @param entityTable the entity table
     * @return the boolean
     */
    private boolean checkExistAndColumns(RdbStore db, EntityTable entityTable) {
        SQLiteTable sqlTable = this.mSqlTableMap.get(entityTable.name);
        if (sqlTable != null) {
            if (OrmLog.isPrint) OrmLog.d(TAG, "Table [" + entityTable.name + "] Exist");
            if (!sqlTable.isTableChecked) {
                // 表仅进行一次检查，检验是否有新字段加入。
                sqlTable.isTableChecked = true;
                if (OrmLog.isPrint) OrmLog.i(TAG, "Table [" + entityTable.name + "] check column now.");
                if (entityTable.key != null) if (sqlTable.columns.get(entityTable.key.column) == null) {
                    SQLStatement stmt = SQLBuilder.buildDropTable(sqlTable.name);
                    stmt.execute(db);
                    if (OrmLog.isPrint) OrmLog.i(TAG, "Table [" + entityTable.name + "] Primary Key has changed, " +
                            "so drop and recreate it later.");
                    return false;
                }
                if (entityTable.pmap != null) {
                    ArrayList<String> newColumns = new ArrayList<String>();
                    for (String col : entityTable.pmap.keySet())
                        if (sqlTable.columns.get(col) == null) newColumns.add(col);
                    if (!Checker.isEmpty(newColumns)) {
                        for (String col : newColumns) sqlTable.columns.put(col, 1);
                        int sum = TableManager.insertNewColunms(db, entityTable.name, newColumns);
                        if (OrmLog.isPrint) if (sum > 0) OrmLog.i(TAG,
                                "Table [" + entityTable.name + "] add " + sum + " new column ： " + newColumns);
                        else
                            OrmLog.e(TAG,
                                    "Table [" + entityTable.name + "] add " + sum + " new column error ： " +
                                            newColumns);
                    }
                }
            }
            return true;
        }
        if (OrmLog.isPrint) OrmLog.d(TAG, "Table [" + entityTable.name + "] Not Exist");
        return false;
    }

    /**
     * 将Sql Table放入存储集合
     *
     * @param table the table
     */
    private void putNewSqlTableIntoMap(EntityTable table) {
        if (OrmLog.isPrint) OrmLog.i(TAG, "Table [" + table.name + "] Create Success");
        SQLiteTable sqlTable = new SQLiteTable();
        sqlTable.name = table.name;
        sqlTable.columns = new HashMap<String, Integer>();
        if (table.key != null) sqlTable.columns.put(table.key.column, 1);
        if (table.pmap != null) for (String col : table.pmap.keySet()) sqlTable.columns.put(col, 1);
        // 第一次建表，不用检查
        sqlTable.isTableChecked = true;
        this.mSqlTableMap.put(sqlTable.name, sqlTable);
    }

    /**
     * 初始化全部表及其列名,初始化失败，则无法进行下去。
     *
     * @param db the db
     */
    private void initAllTablesFromSQLite(RdbStore db) {
        synchronized (this.mSqlTableMap) {
            if (Checker.isEmpty(this.mSqlTableMap)) {
                if (OrmLog.isPrint) OrmLog.i(TAG, "Initialize SQL table start--------------------->");
                SQLStatement st = SQLBuilder.buildTableObtainAll();
                EntityTable table = getTable(SQLiteTable.class, false);
                Querier.doQuery(db, st, new Querier.CursorParser() {
                    @Override
                    public void parseEachCursor(RdbStore db, ResultSet c) throws Exception {
                        SQLiteTable sqlTable = new SQLiteTable();
                        DataUtil.injectDataToObject(c, sqlTable, table);
                        ArrayList<String> colS = TableManager.getAllColumnsFromSQLite(db, sqlTable.name);
                        if (Checker.isEmpty(colS)) {
                            // 如果读数据库失败了，那么解析建表语句
                            OrmLog.e(TableManager.TAG, "读数据库失败了，开始解析建表语句");
                            colS = TableManager.transformSqlToColumns(sqlTable.sql);
                        }
                        sqlTable.columns = new HashMap<String, Integer>();
                        for (String col : colS) sqlTable.columns.put(col, 1);
                        if (OrmLog.isPrint) {
                            OrmLog.i(TableManager.TAG, "Find One SQL Table: " + sqlTable);
                            OrmLog.i(TableManager.TAG, "Table Column: " + colS);
                        }
                        TableManager.this.mSqlTableMap.put(sqlTable.name, sqlTable);
                    }
                });
                if (OrmLog.isPrint)
                    OrmLog.i(TAG, "Initialize SQL table end  ---------------------> " + this.mSqlTableMap.size());
            }
        }
    }

    /**
     * 获取映射表信息(Entity Table)
     * 注意映射表存储在MAP中，key 为 database name + table name， value 为 entity table。
     *
     * @param tableName the table name
     * @param column1   the column 1
     * @param column2   the column 2
     * @return {@link EntityTable}
     */
    private EntityTable getMappingTable(String tableName, String column1, String column2) {
        EntityTable table = getEntityTable(this.dbName + tableName);
        if (table == null) {
            table = new EntityTable();
            table.name = tableName;
            table.pmap = new LinkedHashMap<String, Property>();
            table.pmap.put(column1, null);
            table.pmap.put(column2, null);
            TableManager.putEntityTable(this.dbName + tableName, table);
        }
        return table;
    }

}
