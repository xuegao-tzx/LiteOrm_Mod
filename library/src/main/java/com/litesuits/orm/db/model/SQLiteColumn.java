package com.litesuits.orm.db.model;

import com.litesuits.orm.db.annotation.Column;

import java.io.Serializable;


/**
 * 列结构
 *
 * @author mty
 * @date 2013 -6-7上午1:17:49
 */
public class SQLiteColumn implements Serializable {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 8822000632819424751L;
    /**
     * The Name.
     */
    @Column("name")
    public String name;
    /**
     * The Type.
     */
    @Column("type")
    public String type;
    /**
     * The Cid.
     */
    @Column("cid")
    private long cid;
    /**
     * The Notnull.
     */
    @Column("notnull")
    private short notnull;
    /**
     * The Dflt value.
     */
    @Column("dflt_value")
    private String dflt_value;
    /**
     * The Pk.
     */
    @Column("pk")
    private short pk;

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Column [cid=" + this.cid + ", name=" + this.name + ", type=" + this.type + ", notnull=" + this.notnull + ", dflt_value="
                + this.dflt_value + ", pk=" + this.pk + "]";
    }

}
