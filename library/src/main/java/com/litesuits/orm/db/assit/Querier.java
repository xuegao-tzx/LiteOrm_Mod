package com.litesuits.orm.db.assit;

import com.litesuits.orm.log.OrmLog;
import ohos.data.rdb.RdbException;
import ohos.data.rdb.RdbStore;
import ohos.data.resultset.ResultSet;

/**
 * 辅助查询
 *
 * @author MaTianyu
 * @date 2013 -6-15下午11:11:02
 */
public class Querier {
    /**
     * The constant TAG.
     */
    private static final String TAG = Querier.class.getSimpleName();

    /**
     * 因为每个查询都不一样，但又有相同的结构，这种形式维持代码的统一性，也可以个性化解析。
     *
     * @param <T>    the type parameter
     * @param db     the db
     * @param st     the st
     * @param parser the parser
     * @return the t
     */
    public static <T> T doQuery(RdbStore db, SQLStatement st, CursorParser<T> parser) {
        if (OrmLog.isPrint) OrmLog.d(TAG, "----> Query Start: " + st.toString());
//        Cursor cursor = db.rawQuery(st.sql, (String[]) st.bindArgs);
        ResultSet cursor = null;
        try {
            cursor = db.querySql(st.sql, (String[]) st.bindArgs);
        } catch (RdbException e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            parser.process(db, cursor);
            if (OrmLog.isPrint) OrmLog.d(TAG, "<---- Query End , cursor size : " + cursor.getRowCount());
        } else if (OrmLog.isPrint) OrmLog.e(TAG, "<---- Query End : cursor is null");
        return parser.returnResult();
    }

    /**
     * A simple cursor parser
     *
     * @param <T> the type parameter
     * @author MaTianyu
     */
    public static abstract class CursorParser<T> {
        /**
         * The Parse.
         */
        private boolean parse = true;

        /**
         * Process.
         *
         * @param db     the db
         * @param cursor the cursor
         */
        final void process(RdbStore db, ResultSet cursor) {
            try {

                boolean gotoSuccess = cursor.goToFirstRow();//.moveToFirst();
                while (this.parse && gotoSuccess && !cursor.isEnded()) {
                    this.parseEachCursor(db, cursor);
                    gotoSuccess = cursor.goToNextRow();//.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        /**
         * Stop parse.
         */
        protected final void stopParse() {
            this.parse = false;
        }

        /**
         * Return result t.
         *
         * @return the t
         */
        public T returnResult() {
            return null;
        }

        /**
         * Parse each cursor.
         *
         * @param db the db
         * @param c  the c
         * @throws Exception the exception
         */
        public abstract void parseEachCursor(RdbStore db, ResultSet c) throws Exception;
    }
}
