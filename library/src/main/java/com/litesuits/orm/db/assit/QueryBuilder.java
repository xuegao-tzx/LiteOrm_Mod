package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;

import java.util.regex.Pattern;

/**
 * 查询构建
 *
 * @param <T> the type parameter
 * @author mty
 * @date 2013 -6-14下午3:47:16
 */
public class QueryBuilder<T> {
    /**
     * The constant AND.
     */
    public static final String AND = " AND ";
    /**
     * The constant OR.
     */
    public static final String OR = " OR ";
    /**
     * The constant EQUAL_HOLDER.
     */
    public static final String EQUAL_HOLDER = "=?";
    /**
     * The constant ASC.
     */
    private static final String ASC = " ASC";
    /**
     * The constant DESC.
     */
    private static final String DESC = " DESC";
    /**
     * The constant GROUP_BY.
     */
    private static final String GROUP_BY = " GROUP BY ";
    /**
     * The constant HAVING.
     */
    private static final String HAVING = " HAVING ";
    /**
     * The constant ORDER_BY.
     */
    private static final String ORDER_BY = " ORDER BY ";
    /**
     * The constant LIMIT.
     */
    private static final String LIMIT = " LIMIT ";
    /**
     * The constant SELECT_COUNT.
     */
    private static final String SELECT_COUNT = "SELECT COUNT(*) FROM ";
    /**
     * The constant SELECT.
     */
    private static final String SELECT = "SELECT ";
    /**
     * The constant DISTINCT.
     */
    private static final String DISTINCT = " DISTINCT ";
    /**
     * The constant ASTERISK.
     */
    private static final String ASTERISK = "*";
    /**
     * The constant FROM.
     */
    private static final String FROM = " FROM ";
    /**
     * The constant COMMA_HOLDER.
     */
    private static final String COMMA_HOLDER = ",?";
    /**
     * The constant COMMA.
     */
    private static final String COMMA = ",";
    /**
     * The constant limitPattern.
     */
    private static final Pattern limitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");
    /**
     * The Clazz.
     */
    private final Class<T> clazz;
    /**
     * The Columns.
     */
    protected String[] columns;
    /**
     * The Where builder.
     */
    protected WhereBuilder whereBuilder;
    /**
     * The Clazz mapping.
     */
    private Class clazzMapping;
    /**
     * The Distinct.
     */
    private boolean distinct;
    /**
     * The Group.
     */
    private String group;
    /**
     * The Having.
     */
    private String having;
    /**
     * The Order.
     */
    private String order;
    /**
     * The Limit.
     */
    private String limit;

    /**
     * Instantiates a new Query builder.
     *
     * @param claxx the claxx
     */
    public QueryBuilder(Class<T> claxx) {
        this.clazz = claxx;
        this.whereBuilder = new WhereBuilder(claxx);
    }

    /**
     * Create query builder.
     *
     * @param <T>   the type parameter
     * @param claxx the claxx
     * @return the query builder
     */
    public static <T> QueryBuilder<T> create(Class<T> claxx) {
        return new QueryBuilder<T>(claxx);
    }

    /**
     * 添加条件
     *
     * @param s      the s
     * @param name   the name
     * @param clause the clause
     */
    private static void appendClause(StringBuilder s, String name, String clause) {
        if (!Checker.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    /**
     * 添加列，逗号分隔
     *
     * @param s       the s
     * @param columns the columns
     */
    private static void appendColumns(StringBuilder s, String[] columns) {
        int n = columns.length;

        for (int i = 0; i < n; i++) {
            String column = columns[i];

            if (column != null) {
                if (i > 0) s.append(",");
                s.append(column);
            }
        }
        s.append(" ");
    }

    /**
     * Build where in string.
     *
     * @param column the column
     * @param num    the num
     * @return the string
     */
    private static String buildWhereIn(String column, int num) {
        StringBuilder sb = new StringBuilder(column).append(" IN (?");
        for (int i = 1; i < num; i++) sb.append(COMMA_HOLDER);
        return sb.append(")").toString();
    }

    /**
     * Gets query class.
     *
     * @return the query class
     */
    public Class<T> getQueryClass() {
        return this.clazz;
    }

    /**
     * Where query builder.
     *
     * @param builder the builder
     * @return the query builder
     */
    public QueryBuilder<T> where(WhereBuilder builder) {
        this.whereBuilder = builder;
        return this;
    }

    /**
     * Gets builder.
     *
     * @return the builder
     */
    public WhereBuilder getwhereBuilder() {
        return this.whereBuilder;
    }

    /**
     * Where query builder.
     *
     * @param where     "id = ?";                  "id in(?,?,?)";                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};                  new Integer[]{1,2}
     * @return the query builder
     */
    public QueryBuilder<T> where(String where, Object... whereArgs) {
        this.whereBuilder.where(where, whereArgs);
        return this;
    }

    /**
     * Where append query builder.
     *
     * @param where     "id = ?";                  "id in(?,?,?)";                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};                  new Integer[]{1,2}
     * @return the query builder
     */
    public QueryBuilder<T> whereAppend(String where, Object... whereArgs) {
        this.whereBuilder.append(null, where, whereArgs);
        return this;
    }

    /**
     * build as " AND " + where
     *
     * @param where     "id = ?";                  "id in(?,?,?)";                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};                  new Integer[]{1,2}
     * @return the query builder
     */
    public QueryBuilder<T> whereAnd(String where, Object... whereArgs) {
        this.whereBuilder.and(where, whereArgs);
        return this;
    }

    /**
     * build as " OR " + where
     *
     * @param where     "id = ?";                  "id in(?,?,?)";                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};                  new Integer[]{1,2}
     * @return the query builder
     */
    public QueryBuilder<T> whereOr(String where, Object... whereArgs) {
        this.whereBuilder.or(where, whereArgs);
        return this;
    }

    /**
     * build as where+" AND "
     *
     * @return the query builder
     */
    public QueryBuilder<T> whereAppendAnd() {
        this.whereBuilder.and();
        return this;
    }

    /**
     * build as where+" OR "
     *
     * @return the query builder
     */
    public QueryBuilder<T> whereAppendOr() {
        this.whereBuilder.or();
        return this;
    }

    /**
     * build as where+" NOT "
     *
     * @return the query builder
     */
    public QueryBuilder<T> whereAppendNot() {
        this.whereBuilder.not();
        return this;
    }

    /**
     * build as where+" column != ? "
     *
     * @param column the column
     * @param value  the value
     * @return the query builder
     */
    public QueryBuilder<T> whereNoEquals(String column, Object value) {
        this.whereBuilder.noEquals(column, value);
        return this;
    }

    /**
     * build as where+" column > ? "
     *
     * @param column the column
     * @param value  the value
     * @return the query builder
     */
    public QueryBuilder<T> whereGreaterThan(String column, Object value) {
        this.whereBuilder.greaterThan(column, value);
        return this;
    }

    /**
     * build as where+" column < ? "
     *
     * @param column the column
     * @param value  the value
     * @return the query builder
     */
    public QueryBuilder<T> whereLessThan(String column, Object value) {
        this.whereBuilder.lessThan(column, value);
        return this;
    }

    /**
     * build as where+" column = ? "
     *
     * @param column the column
     * @param value  the value
     * @return the query builder
     */
    public QueryBuilder<T> whereEquals(String column, Object value) {
        this.whereBuilder.equals(column, value);
        return this;
    }

    /**
     * build as where+" column IN(?, ?, ?...)"
     *
     * @param column the column
     * @param values the values
     * @return the query builder
     */
    public QueryBuilder<T> whereIn(String column, Object... values) {
        this.whereBuilder.in(column, values);
        return this;
    }

    /**
     * 需要返回的列，不填写默认全部，即select * 。
     *
     * @param columns 列名,注意不是对象的属性名。
     * @return the query builder
     */
    public QueryBuilder<T> columns(String[] columns) {
        this.columns = columns;
        return this;
    }

    /**
     * 累积需要返回的列，不填写默认全部，即select * 。
     *
     * @param columns 列名,注意不是对象的属性名。
     * @return the query builder
     */
    public QueryBuilder<T> appendColumns(String[] columns) {
        if (this.columns != null) {
            String[] newCols = new String[this.columns.length + columns.length];
            System.arraycopy(this.columns, 0, newCols, 0, this.columns.length);
            System.arraycopy(columns, 0, newCols, this.columns.length, columns.length);
            this.columns = newCols;
        } else this.columns = columns;
        return this;
    }

    /**
     * 唯一性保证
     *
     * @param distinct the distinct
     * @return the query builder
     */
    public QueryBuilder<T> distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    /**
     * GROUP BY 语句用于结合合计函数，根据一个或多个列对结果集进行分组。
     *
     * @param group the group
     * @return the query builder
     */
    public QueryBuilder<T> groupBy(String group) {
        this.group = group;
        return this;
    }

    /**
     * 在 SQL 中增加 HAVING 子句原因是，WHERE 关键字无法与合计函数一起使用。
     *
     * @param having the having
     * @return the query builder
     */
    public QueryBuilder<T> having(String having) {
        this.having = having;
        return this;
    }

    /**
     * Order by query builder.
     *
     * @param order the order
     * @return the query builder
     */
    public QueryBuilder<T> orderBy(String order) {
        this.order = order;
        return this;
    }

    /**
     * Append order asc by query builder.
     *
     * @param column the column
     * @return the query builder
     */
    public QueryBuilder<T> appendOrderAscBy(String column) {
        if (this.order == null) this.order = column + ASC;
        else this.order += ", " + column + ASC;
        return this;
    }

    /**
     * Append order desc by query builder.
     *
     * @param column the column
     * @return the query builder
     */
    public QueryBuilder<T> appendOrderDescBy(String column) {
        if (this.order == null) this.order = column + DESC;
        else this.order += ", " + column + DESC;
        return this;
    }

    /**
     * Limit query builder.
     *
     * @param limit the limit
     * @return the query builder
     */
    public QueryBuilder<T> limit(String limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Limit query builder.
     *
     * @param start  the start
     * @param length the length
     * @return the query builder
     */
    public QueryBuilder<T> limit(int start, int length) {
        this.limit = start + COMMA + length;
        return this;
    }

    /**
     * Query mapping info query builder.
     *
     * @param clazzMapping the clazz mapping
     * @return the query builder
     */
    QueryBuilder<T> queryMappingInfo(Class clazzMapping) {
        this.clazzMapping = clazzMapping;
        return this;
    }

    /**
     * 构建查询语句
     *
     * @return the sql statement
     */
    public SQLStatement createStatement() {
        if (this.clazz == null)
            throw new IllegalArgumentException("U Must Set A Query Entity Class By queryWho(Class) or " +
                    "QueryBuilder(Class)");
        if (Checker.isEmpty(this.group) && !Checker.isEmpty(this.having)) throw new IllegalArgumentException(
                "HAVING仅允许在有GroupBy的时候使用(HAVING clauses are only permitted when using a groupBy clause)");
        if (!Checker.isEmpty(this.limit) && !limitPattern.matcher(this.limit).matches())
            throw new IllegalArgumentException(
                    "invalid LIMIT clauses:" + this.limit);

        StringBuilder query = new StringBuilder(120);

        query.append(SELECT);
        if (this.distinct) query.append(DISTINCT);
        if (!Checker.isEmpty(this.columns)) appendColumns(query, this.columns);
        else query.append(ASTERISK);
        query.append(FROM).append(this.getTableName());

        query.append(this.whereBuilder.createWhereString());

        appendClause(query, GROUP_BY, this.group);
        appendClause(query, HAVING, this.having);
        appendClause(query, ORDER_BY, this.order);
        appendClause(query, LIMIT, this.limit);

        SQLStatement stmt = new SQLStatement();
        stmt.sql = query.toString();
        stmt.bindArgs = this.whereBuilder.transToStringArray();
        return stmt;
    }

    /**
     * Build a statement that returns a 1 by 1 table with a numeric value.
     * SELECT COUNT(*) FROM table;
     *
     * @return the sql statement
     */
    public SQLStatement createStatementForCount() {
        StringBuilder query = new StringBuilder(120);
        query.append(SELECT_COUNT).append(this.getTableName());
        SQLStatement stmt = new SQLStatement();
        if (this.whereBuilder != null) {
            query.append(this.whereBuilder.createWhereString());
            stmt.bindArgs = this.whereBuilder.transToStringArray();
        }
        stmt.sql = query.toString();
        return stmt;
    }

    /**
     * Gets table name.
     *
     * @return the table name
     */
    public String getTableName() {
        if (this.clazzMapping == null) return TableManager.getTableName(this.clazz);
        else
            return TableManager.getMapTableName(this.clazz, this.clazzMapping);
    }

}
