package com.litesuits.orm.db.enums;

/**
 * The enum Strategy.
 */
public enum Strategy {
    /**
     * Rollback strategy.
     */
    ROLLBACK(" ROLLBACK "),
    /**
     * Abort strategy.
     */
    ABORT(" ABORT "),
    /**
     * Fail strategy.
     */
    FAIL(" FAIL "),
    /**
     * Ignore strategy.
     */
    IGNORE(" IGNORE "),
    /**
     * Replace strategy.
     */
    REPLACE(" REPLACE ");

    /**
     * The Sql.
     */
    public String sql;

    /**
     * Instantiates a new Strategy.
     *
     * @param sql the sql
     */
    Strategy(String sql) {
        this.sql = sql;
    }

    /**
     * Gets sql.
     *
     * @return the sql
     */
    public String getSql() {
        return this.sql;
    }
}