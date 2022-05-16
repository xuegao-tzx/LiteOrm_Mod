package com.litesuits.orm.db.model;


import com.litesuits.orm.db.enums.AssignType;

import java.lang.reflect.Field;

/**
 * 主键
 *
 * @author mty
 * @date 2013 -6-9上午1:09:33
 */
public class Primarykey extends Property {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 2304252505493855513L;

    /**
     * The Assign.
     */
    public AssignType assign;

    /**
     * Instantiates a new Primarykey.
     *
     * @param p      the p
     * @param assign the assign
     */
    public Primarykey(Property p, AssignType assign) {
        this(p.column, p.field, p.classType, assign);
    }

    /**
     * Instantiates a new Primarykey.
     *
     * @param column    the column
     * @param field     the field
     * @param classType the class type
     * @param assign    the assign
     */
    public Primarykey(String column, Field field, int classType, AssignType assign) {
        super(column, field, classType);
        this.assign = assign;
    }

    /**
     * Is assigned by system boolean.
     *
     * @return the boolean
     */
    public boolean isAssignedBySystem() {
        return this.assign == AssignType.AUTO_INCREMENT;
    }

    /**
     * Is assigned by myself boolean.
     *
     * @return the boolean
     */
    public boolean isAssignedByMyself() {
        return this.assign == AssignType.BY_MYSELF;
    }
}
