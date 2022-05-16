package com.litesuits.orm.db.assit;

import com.litesuits.orm.log.OrmLog;
import ohos.data.rdb.RdbStore;

/**
 * 辅助事务
 *
 * @author mty
 * @date 2013 -6-15下午11:09:15
 */
public class Transaction {
    /**
     * The constant TAG.
     */
    private static final String TAG = Transaction.class.getSimpleName();

    /**
     * 因为每个具体事物都不一样，但又有相同的结构，既要维持代码的统一性，也要可以个性化解析。
     *
     * @param <T>    the type parameter
     * @param db     the db
     * @param worker the worker
     * @return the t
     */
    public static <T> T execute(RdbStore db, Worker<T> worker) {
        db.beginTransaction();
        OrmLog.i(TAG, "----> BeginTransaction");
        T data = null;
        try {
            data = worker.doTransaction(db);
            db.markAsCommit();//.setTransactionSuccessful();
            if (OrmLog.isPrint) OrmLog.i(TAG, "----> Transaction Successful");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return data;
    }

    /**
     * The interface Worker.
     *
     * @param <T> the type parameter
     */
    public interface Worker<T> {
        /**
         * Do transaction t.
         *
         * @param db the db
         * @return the t
         * @throws Exception the exception
         */
        T doTransaction(RdbStore db) throws Exception;
    }

}
