package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;

/**
 * The type Where builder.
 *
 * @author MaTianyu
 * @date 2015 -03-18
 */
public class WhereBuilder {
    /**
     * The constant NOTHING.
     */
    private static final String NOTHING = "";
    /**
     * The constant WHERE.
     */
    private static final String WHERE = " WHERE ";
    /**
     * The constant EQUAL_HOLDER.
     */
    private static final String EQUAL_HOLDER = "=?";
    /**
     * The constant NOT_EQUAL_HOLDER.
     */
    private static final String NOT_EQUAL_HOLDER = "!=?";
    /**
     * The constant GREATER_THAN_HOLDER.
     */
    private static final String GREATER_THAN_HOLDER = ">?";
    /**
     * The constant LESS_THAN_HOLDER.
     */
    private static final String LESS_THAN_HOLDER = "<?";
    /**
     * The constant COMMA_HOLDER.
     */
    private static final String COMMA_HOLDER = ",?";
    /**
     * The constant HOLDER.
     */
    private static final String HOLDER = "?";
    /**
     * The constant AND.
     */
    private static final String AND = " AND ";
    /**
     * The constant OR.
     */
    private static final String OR = " OR ";
    /**
     * The constant NOT.
     */
    private static final String NOT = " NOT ";
    /**
     * The constant DELETE.
     */
    private static final String DELETE = "DELETE FROM ";
    /**
     * The constant PARENTHESES_LEFT.
     */
    private static final String PARENTHESES_LEFT = "(";
    /**
     * The constant PARENTHESES_RIGHT.
     */
    private static final String PARENTHESES_RIGHT = ")";
    /**
     * The constant IN.
     */
    private static final String IN = " IN ";
    /**
     * The Table class.
     */
    private final Class tableClass;
    /**
     * The Where.
     */
    private String where;
    /**
     * The Where args.
     */
    private Object[] whereArgs;

    /**
     * Instantiates a new Where builder.
     *
     * @param tableClass the table class
     */
    public WhereBuilder(Class tableClass) {
        this.tableClass = tableClass;
    }

    /**
     * Instantiates a new Where builder.
     *
     * @param tableClass the table class
     * @param where      the where
     * @param whereArgs  the where args
     */
    public WhereBuilder(Class tableClass, String where, Object[] whereArgs) {
        this.where = where;
        this.whereArgs = whereArgs;
        this.tableClass = tableClass;
    }

    /**
     * Create where builder.
     *
     * @param tableClass the table class
     * @return the where builder
     */
    public static WhereBuilder create(Class tableClass) {
        return new WhereBuilder(tableClass);
    }

    /**
     * Create where builder.
     *
     * @param tableClass the table class
     * @param where      the where
     * @param whereArgs  the where args
     * @return the where builder
     */
    public static WhereBuilder create(Class tableClass, String where, Object[] whereArgs) {
        return new WhereBuilder(tableClass, where, whereArgs);
    }

    /**
     * Build where in string.
     *
     * @param column the column
     * @param num    the num
     * @return the string
     */
    private static String buildWhereIn(String column, int num) {
        StringBuilder sb = new StringBuilder(column).append(IN).append(PARENTHESES_LEFT).append(HOLDER);
        for (int i = 1; i < num; i++) sb.append(COMMA_HOLDER);
        return sb.append(PARENTHESES_RIGHT).toString();
    }

    /**
     * Gets table class.
     *
     * @return the table class
     */
    public Class getTableClass() {
        return this.tableClass;
    }

    /**
     * Where where builder.
     *
     * @param where     "id = ?";                  "id in(?,?,?)";                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};                  new Integer[]{1,2}
     * @return the where builder
     */
    WhereBuilder where(String where, Object... whereArgs) {
        this.where = where;
        this.whereArgs = whereArgs;
        return this;
    }

    /**
     * And where builder.
     *
     * @param where     "id = ?";                  "id in(?,?,?)";                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};                  new Integer[]{1,2}
     * @return the where builder
     */
    public WhereBuilder and(String where, Object... whereArgs) {
        return this.append(AND, where, whereArgs);
    }

    /**
     * Or where builder.
     *
     * @param where     "id = ?";                  "id in(?,?,?)";                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};                  new Integer[]{1,2}
     * @return the where builder
     */
    public WhereBuilder or(String where, Object... whereArgs) {
        return this.append(OR, where, whereArgs);
    }

    /**
     * build as " AND "
     *
     * @return the where builder
     */
    public WhereBuilder and() {
        if (this.where != null) this.where += AND;
        return this;
    }

    /**
     * build as " OR "
     *
     * @return the where builder
     */
    public WhereBuilder or() {
        if (this.where != null) this.where += OR;
        return this;
    }

    /**
     * build as " NOT "
     *
     * @return the where builder
     */
    public WhereBuilder not() {
        if (this.where != null) this.where += NOT;
        return this;
    }

    /**
     * build as " column != ? "
     *
     * @param column the column
     * @param value  the value
     * @return the where builder
     */
    WhereBuilder noEquals(String column, Object value) {
        return this.append(null, column + NOT_EQUAL_HOLDER, value);
    }

    /**
     * build as " column > ? "
     *
     * @param column the column
     * @param value  the value
     * @return the where builder
     */
    public WhereBuilder greaterThan(String column, Object value) {
        return this.append(null, column + GREATER_THAN_HOLDER, value);
    }

    /**
     * build as " column < ? "
     *
     * @param column the column
     * @param value  the value
     * @return the where builder
     */
    public WhereBuilder lessThan(String column, Object value) {
        return this.append(null, column + LESS_THAN_HOLDER, value);
    }

    /**
     * build as " column = ? "
     *
     * @param column the column
     * @param value  the value
     * @return the where builder
     */
    public WhereBuilder equals(String column, Object value) {
        return this.append(null, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " or column = ? "
     *
     * @param column the column
     * @param value  the value
     * @return the where builder
     */
    public WhereBuilder orEquals(String column, Object value) {
        return this.append(OR, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " and column = ? "
     *
     * @param column the column
     * @param value  the value
     * @return the where builder
     */
    public WhereBuilder andEquals(String column, Object value) {
        return this.append(AND, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " column in(?,?...) "
     *
     * @param column the column
     * @param values the values
     * @return the where builder
     */
    public WhereBuilder in(String column, Object... values) {
        return this.append(null, WhereBuilder.buildWhereIn(column, values.length), values);
    }

    /**
     * build as " or column in(?,?...) "
     *
     * @param column the column
     * @param values the values
     * @return the where builder
     */
    public WhereBuilder orIn(String column, Object... values) {
        return this.append(OR, WhereBuilder.buildWhereIn(column, values.length), values);
    }

    /**
     * build as " and column in(?,?...) "
     *
     * @param column the column
     * @param values the values
     * @return the where builder
     */
    public WhereBuilder andIn(String column, Object... values) {
        return this.append(AND, WhereBuilder.buildWhereIn(column, values.length), values);
    }

    /**
     * Append where builder.
     *
     * @param connect     NULL or " AND " or " OR " or " NOT "
     * @param whereString "id = ?";                    or "id in(?,?,?)";                    or "id LIKE %?";                    ...
     * @param value       new String[]{"",""};                    or new Integer[]{1,2};                    ...
     * @return this where builder
     */
    public WhereBuilder append(String connect, String whereString, Object... value) {
        if (this.where == null) {
            this.where = whereString;
            this.whereArgs = value;
        } else {
            if (connect != null) this.where += connect;
            this.where += whereString;
            if (this.whereArgs == null) this.whereArgs = value;
            else {
                Object[] newWhere = new Object[this.whereArgs.length + value.length];
                System.arraycopy(this.whereArgs, 0, newWhere, 0, this.whereArgs.length);
                System.arraycopy(value, 0, newWhere, this.whereArgs.length, value.length);
                this.whereArgs = newWhere;
            }
        }
        return this;
    }

    /**
     * Trans to string array string [ ].
     *
     * @return the string [ ]
     */
    String[] transToStringArray() {
        if (this.whereArgs != null && this.whereArgs.length > 0) {
            if (this.whereArgs instanceof String[]) return (String[]) this.whereArgs;
            String[] arr = new String[this.whereArgs.length];
            for (int i = 0; i < arr.length; i++) arr[i] = String.valueOf(this.whereArgs[i]);
            return arr;
        }
        return null;
    }

    /**
     * Create where string string.
     *
     * @return the string
     */
    String createWhereString() {
        if (this.where != null) return WHERE + this.where;
        else return NOTHING;
    }

    /**
     * Create statement delete sql statement.
     *
     * @return the sql statement
     */
    public SQLStatement createStatementDelete() {
        SQLStatement stmt = new SQLStatement();
        stmt.sql = DELETE + TableManager.getTableName(this.tableClass) + this.createWhereString();
        stmt.bindArgs = this.transToStringArray();
        return stmt;
    }

    /**
     * Gets where.
     *
     * @return the where
     */
    public String getWhere() {
        return this.where;
    }

    /**
     * Sets where.
     *
     * @param where the where
     */
    public void setWhere(String where) {
        this.where = where;
    }

    /**
     * Get where args object [ ].
     *
     * @return the object [ ]
     */
    Object[] getWhereArgs() {
        return this.whereArgs;
    }

    /**
     * Sets where args.
     *
     * @param whereArgs the where args
     */
    public void setWhereArgs(Object[] whereArgs) {
        this.whereArgs = whereArgs;
    }
}
