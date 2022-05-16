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

import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.SQLiteHelper.OnUpdateListener;
import ohos.app.Context;

/**
 * 数据操作配置
 *
 * @author MaTianyu 2013-6-2下午4:36:16
 */
public class DataBaseConfig {
    /**
     * The constant DEFAULT_DB_NAME.
     */
    public static final String DEFAULT_DB_NAME = "liteorm.db";
    /**
     * The constant DEFAULT_DB_VERSION.
     */
    public static final int DEFAULT_DB_VERSION = 1;

    /**
     * The Context.
     */
    public Context context;
    /**
     * The Debugged.
     */
    public boolean debugged = false;
    /**
     * The Db name.
     */
    public String dbName = DEFAULT_DB_NAME;
    /**
     * The Db version.
     */
    public int dbVersion = DEFAULT_DB_VERSION;
    /**
     * The On update listener.
     */
    public OnUpdateListener onUpdateListener;

    /**
     * Instantiates a new Data base config.
     *
     * @param context the context
     */
    public DataBaseConfig(Context context) {
        this(context, DEFAULT_DB_NAME);
    }

    /**
     * Instantiates a new Data base config.
     *
     * @param context the context
     * @param dbName  the db name
     */
    public DataBaseConfig(Context context, String dbName) {
        this(context, dbName, false, DEFAULT_DB_VERSION, null);
    }

    /**
     * Instantiates a new Data base config.
     *
     * @param context          the context
     * @param dbName           the db name
     * @param debugged         the debugged
     * @param dbVersion        the db version
     * @param onUpdateListener the on update listener
     */
    private DataBaseConfig(Context context, String dbName, boolean debugged,
                           int dbVersion, OnUpdateListener onUpdateListener) {
        this.context = context.getApplicationContext();
        if (!Checker.isEmpty(dbName)) this.dbName = dbName;
        if (dbVersion > 1) this.dbVersion = dbVersion;
        this.debugged = debugged;
        this.onUpdateListener = onUpdateListener;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "DataBaseConfig [mContext=" + this.context + ", mDbName=" + this.dbName + ", mDbVersion="
                + this.dbVersion + ", mOnUpdateListener=" + this.onUpdateListener + "]";
    }

}
