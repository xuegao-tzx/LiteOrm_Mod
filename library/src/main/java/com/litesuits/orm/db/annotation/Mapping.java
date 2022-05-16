package com.litesuits.orm.db.annotation;

import com.litesuits.orm.db.enums.Relation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关系映射
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
    /**
     * Value relation.
     *
     * @return the relation
     */
    Relation value();
}
