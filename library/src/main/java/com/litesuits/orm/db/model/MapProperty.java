package com.litesuits.orm.db.model;

import com.litesuits.orm.db.enums.Relation;

import java.lang.reflect.Field;

/**
 * 映射关系
 *
 * @author MaTianyu 2014-3-7下午11:16:19
 */
public class MapProperty extends Property {
    /**
     * The constant PRIMARYKEY.
     */
    public static final String PRIMARYKEY = " PRIMARY KEY ";
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 1641409866866426637L;
    /**
     * The Relation.
     */
    private final Relation relation;

    /**
     * Instantiates a new Map property.
     *
     * @param p        the p
     * @param relation the relation
     */
    public MapProperty(Property p, Relation relation) {
        this(p.column, p.field, relation);
    }

    /**
     * Instantiates a new Map property.
     *
     * @param column   the column
     * @param field    the field
     * @param relation the relation
     */
    private MapProperty(String column, Field field, Relation relation) {
        super(column, field);
        this.relation = relation;
    }

    /**
     * Is to many boolean.
     *
     * @return the boolean
     */
    public boolean isToMany() {
        return this.relation == Relation.ManyToMany || this.relation == Relation.OneToMany;
    }

    /**
     * Is to one boolean.
     *
     * @return the boolean
     */
    public boolean isToOne() {
        return this.relation == Relation.ManyToOne || this.relation == Relation.OneToOne;
    }

}
