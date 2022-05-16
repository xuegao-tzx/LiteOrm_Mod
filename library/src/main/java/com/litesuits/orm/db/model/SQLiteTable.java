package com.litesuits.orm.db.model;

import com.litesuits.orm.db.annotation.Column;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 表结构，SQLite中的每一张表都有这样的属性。
 * CREATE TABLE sqlite_master (
 * 　type TEXT,
 * 　name TEXT,
 * 　tbl_name TEXT,
 * 　rootpage INTEGER,
 * 　sql TEXT
 * );
 *
 * @author mty
 * @date 2013 -6-2下午11:17:40
 */
public class SQLiteTable implements Serializable {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 6706520684759700566L;

    /**
     * The Type.
     */
    @Column("type")
    public String type;

    /**
     * The Name.
     */
    @Column("name")
    public String name;
    /**
     * The Sql.
     */
    @Column("sql")
    public String sql = "CREATE TABLE sqlite_master (" +
            "  　type TEXT," +
            "  　name TEXT," +
            "  　tbl_name TEXT," +
            "  　rootpage INTEGER," +
            "  　sql TEXT" +
            "  );";
    /**
     * The Is table checked.
     */
    public boolean isTableChecked;
    /**
     * The Columns.
     */
    public HashMap<String, Integer> columns;
    /**
     * The Tbl name.
     */
    @Column("tbl_name")
    private String tbl_name;
    /**
     * The Rootpage.
     */
    @Column("rootpage")
    private long rootpage;

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "SQLiteTable{" +
                "type='" + this.type + '\'' +
                ", name='" + this.name + '\'' +
                ", tbl_name='" + this.tbl_name + '\'' +
                ", rootpage=" + this.rootpage +
                ", sql='" + this.sql + '\'' +
                ", isTableChecked=" + this.isTableChecked +
                ", columns=" + this.columns +
                '}';
    }

}
