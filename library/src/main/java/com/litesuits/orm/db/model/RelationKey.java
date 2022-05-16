package com.litesuits.orm.db.model;

/**
 * The type Relation key.
 *
 * @author MaTianyu
 * @date 14 -7-24
 */
public class RelationKey {
    /**
     * The Key 1.
     */
    public String key1;
    /**
     * The Key 2.
     */
    public String key2;

    /**
     * Is ok boolean.
     *
     * @return the boolean
     */
    public boolean isOK() {
        return this.key1 != null && this.key2 != null;
    }

}
