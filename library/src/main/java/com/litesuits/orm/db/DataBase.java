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

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.RelationKey;
import ohos.data.rdb.RdbStore;
import ohos.data.resultset.ResultSetHook;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * data base operation interface
 *
 * @author mty
 * @date 2013 -6-2上午2:37:56
 */
public interface DataBase {

    /**
     * Open or create database rdb store.
     *
     * @return true if create successfully.
     */
    RdbStore openOrCreateDatabase();

    /**
     * save: insert or update a single entity
     *
     * @param entity the entity
     * @return the number of rows affected by this SQL statement execution.
     */
    long save(Object entity);

    /**
     * save: insert or update a collection
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the number of affected rows
     */
    <T> int save(Collection<T> collection);

    /**
     * insert a single entity
     *
     * @param entity the entity
     * @return the number of rows affected by this SQL statement execution.
     */
    long insert(Object entity);


    /**
     * insert a single entity with conflict algorithm
     *
     * @param entity            the entity
     * @param conflictAlgorithm the conflict algorithm
     * @return the number of rows affected by this SQL statement execution.
     */
    long insert(Object entity, ConflictAlgorithm conflictAlgorithm);

    /**
     * insert a collection
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the number of affected rows
     */
    <T> int insert(Collection<T> collection);

    /**
     * insert a collection with conflict algorithm
     *
     * @param <T>               the type parameter
     * @param collection        the collection
     * @param conflictAlgorithm the conflict algorithm
     * @return the number of affected rows
     */
    <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a single entity
     *
     * @param entity the entity
     * @return number of affected rows
     */
    int update(Object entity);

    /**
     * update a single entity with conflict algorithm
     *
     * @param entity            the entity
     * @param conflictAlgorithm the conflict algorithm
     * @return number of affected rows
     */
    int update(Object entity, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a single entity with conflict algorithm, and only update columns in {@link ColumnsValue}
     * if param {@link ColumnsValue} is null, update all columns.
     *
     * @param entity            the entity
     * @param cvs               the cvs
     * @param conflictAlgorithm the conflict algorithm
     * @return the number of affected rows
     */
    int update(Object entity, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a collection
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the number of affected rows
     */
    <T> int update(Collection<T> collection);

    /**
     * update a collection with conflict algorithm
     *
     * @param <T>               the type parameter
     * @param collection        the collection
     * @param conflictAlgorithm the conflict algorithm
     * @return number of affected rows
     */
    <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a collection with conflict algorithm, and only update columns in {@link ColumnsValue}
     * if param {@link ColumnsValue} is null, update all columns.
     *
     * @param <T>               the type parameter
     * @param collection        the collection
     * @param cvs               the cvs
     * @param conflictAlgorithm the conflict algorithm
     * @return number of affected rows
     */
    <T> int update(Collection<T> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * update model use custom where clause.
     *
     * @param builder           the builder
     * @param cvs               the cvs
     * @param conflictAlgorithm the conflict algorithm
     * @return number of affected rows
     */
    int update(WhereBuilder builder, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * delete a single entity
     *
     * @param entity the entity
     * @return the number of affected rows
     */
    int delete(Object entity);

    /**
     * delete all rows
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the number of affected rows
     */
    <T> int delete(Class<T> claxx);

    /**
     * delete all rows
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the number of affected rows
     */
    <T> int deleteAll(Class<T> claxx);

    /**
     * <b>start must >=0 and smaller than end</b>
     * <p>delete from start to the end, <b>[start,end].</b>
     * <p>set end={@link Integer#MAX_VALUE} will delete all rows from the start
     *
     * @param <T>          the type parameter
     * @param claxx        the claxx
     * @param start        the start
     * @param end          the end
     * @param orderAscColu the order asc colu
     * @return the number of affected rows
     */
    <T> int delete(Class<T> claxx, long start, long end, String orderAscColu);

    /**
     * delete a collection
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the number of affected rows
     */
    <T> int delete(Collection<T> collection);

    /**
     * delete by custem where syntax
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @param where the where
     * @return the number of affected rows
     * @deprecated use {@link #delete(WhereBuilder)} instead.
     */
    <T> int delete(Class<T> claxx, WhereBuilder where);

    /**
     * delete by custem where syntax
     *
     * @param where the where
     * @return the number of affected rows
     */
    int delete(WhereBuilder where);

    /**
     * query all data of this type
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the query result list
     */
    <T> ArrayList<T> query(Class<T> claxx);

    /**
     * custom query
     *
     * @param <T> the type parameter
     * @param qb  the qb
     * @return the query result list
     */
    <T> ArrayList<T> query(QueryBuilder<T> qb);

    /**
     * query entity by long id
     *
     * @param <T>   the type parameter
     * @param id    the id
     * @param clazz the clazz
     * @return the query result
     */
    <T> T queryById(long id, Class<T> clazz);

    /**
     * query entity by string id
     *
     * @param <T>   the type parameter
     * @param id    the id
     * @param clazz the clazz
     * @return the query result
     */
    <T> T queryById(String id, Class<T> clazz);

    /**
     * query count of table rows and return
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the count of query result
     */
    <T> long queryCount(Class<T> claxx);

    /**
     * query count of your sql query result rows and return
     *
     * @param qb the qb
     * @return the count of query result
     */
    long queryCount(QueryBuilder qb);

    /**
     * build a sql statement with sql and args.
     *
     * @param sql      the sql
     * @param bindArgs the bind args
     * @return the sql statement
     */
    SQLStatement createSQLStatement(String sql, Object[] bindArgs);

    /**
     * Execute this SQL statement, if it is not a SELECT / INSERT / DELETE / UPDATE, for example
     * CREATE / DROP table, view, trigger, index etc.
     *
     * @param db        the db
     * @param statement the statement
     * @return the boolean
     */
    boolean execute(RdbStore db, SQLStatement statement);

    /**
     * drop a table
     *
     * @param entity the entity
     * @return true if droped successfully.
     * @deprecated
     */
    @Deprecated
    boolean dropTable(Object entity);

    /**
     * drop a table
     *
     * @param claxx the claxx
     * @return true if droped successfully.
     */
    boolean dropTable(Class<?> claxx);

    /**
     * drop a table
     *
     * @param tableName the table name
     * @return true if droped successfully.
     */
    boolean dropTable(String tableName);

    /**
     * find and return relation between two diffirent collection.
     *
     * @param class1   the class 1
     * @param class2   the class 2
     * @param key1List the key 1 list
     * @return the relation list of class1 and class2;
     */
    ArrayList<RelationKey> queryRelation(Class class1, Class class2, List<String> key1List);

    /**
     * auto entity relation mapping
     *
     * @param <E>  the type parameter
     * @param <T>  the type parameter
     * @param col1 the col 1
     * @param col2 the col 2
     * @return the boolean
     */
    <E, T> boolean mapping(Collection<E> col1, Collection<T> col2);

    /**
     * get readable database
     *
     * @return the readable database
     */
    RdbStore getReadableDatabase();

    /**
     * get writable database
     *
     * @return the writable database
     */
    RdbStore getWritableDatabase();

    /**
     * get {@link TableManager}
     *
     * @return the table manager
     */
    TableManager getTableManager();

    /**
     * get {@link SQLiteHelper}
     *
     * @return the sq lite helper
     */
    SQLiteHelper getSQLiteHelper();

    /**
     * get {@link DataBaseConfig}
     *
     * @return the data base config
     */
    DataBaseConfig getDataBaseConfig();

    /**
     * Open or create database rdb store.
     *
     * @param path    the path
     * @param factory the factory
     * @return the rdb store
     */
    RdbStore openOrCreateDatabase(String path, ResultSetHook factory);

    /**
     * Delete database boolean.
     *
     * @return the boolean
     */
    boolean deleteDatabase();

    /**
     * Delete database boolean.
     *
     * @param file the file
     * @return true if delete successfully.
     */
    boolean deleteDatabase(File file);

    /**
     * 关闭数据库，清空缓存。
     */
    void close();
}
