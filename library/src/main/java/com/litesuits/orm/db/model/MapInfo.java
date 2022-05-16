package com.litesuits.orm.db.model;

import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.SQLStatement;

import java.util.ArrayList;

/**
 * 映射表类
 *
 * @author MaTianyu 2014-3-8上午3:20:20
 */
public class MapInfo {

    /**
     * The Table list.
     */
    public ArrayList<MapTable> tableList;
    /**
     * The Map new relation sql.
     */
    public ArrayList<SQLStatement> mapNewRelationSQL;
    /**
     * The Del old relation sql.
     */
    public ArrayList<SQLStatement> delOldRelationSQL;

    /**
     * Add table boolean.
     *
     * @param table the table
     * @return the boolean
     */
    public boolean addTable(MapTable table) {
        if (table.name == null) return false;
        if (this.tableList == null) this.tableList = new ArrayList<MapTable>();
        //for (MapTable mt : tableList) {
        //    if (mt.name.equals(table.name)) return false;
        //}
        return this.tableList.add(table);
    }

    /**
     * Add new relation sql boolean.
     *
     * @param st the st
     * @return the boolean
     */
    public boolean addNewRelationSQL(SQLStatement st) {
        if (this.mapNewRelationSQL == null) this.mapNewRelationSQL = new ArrayList<SQLStatement>();
        return this.mapNewRelationSQL.add(st);
    }

    /**
     * Add new relation sql boolean.
     *
     * @param list the list
     * @return the boolean
     */
    public boolean addNewRelationSQL(ArrayList<SQLStatement> list) {
        if (this.mapNewRelationSQL == null) this.mapNewRelationSQL = new ArrayList<SQLStatement>();
        return this.mapNewRelationSQL.addAll(list);
    }

    /**
     * Add del old relation sql boolean.
     *
     * @param st the st
     * @return the boolean
     */
    public boolean addDelOldRelationSQL(SQLStatement st) {
        if (this.delOldRelationSQL == null) this.delOldRelationSQL = new ArrayList<SQLStatement>();
        return this.delOldRelationSQL.add(st);
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return Checker.isEmpty(this.tableList)
                || Checker.isEmpty(this.mapNewRelationSQL) && Checker.isEmpty(this.delOldRelationSQL);
    }

    /**
     * The type Map table.
     */
    public static class MapTable {
        /**
         * The Name.
         */
        public String name;
        /**
         * The Column 1.
         */
        public String column1;
        /**
         * The Column 2.
         */
        public String column2;

        /**
         * Instantiates a new Map table.
         *
         * @param name the name
         * @param col1 the col 1
         * @param col2 the col 2
         */
        public MapTable(String name, String col1, String col2) {
            this.name = name;
            this.column1 = col1;
            this.column2 = col2;
        }
    }
}