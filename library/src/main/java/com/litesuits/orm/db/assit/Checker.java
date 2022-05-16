package com.litesuits.orm.db.assit;

import java.util.Collection;
import java.util.Map;

/**
 * 辅助判断
 *
 * @author mty
 * @date 2013 -6-10下午5:50:57
 */
public class Checker {

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(CharSequence str) {
        return isNull(str) || str.length() == 0;
    }

    /**
     * Is empty boolean.
     *
     * @param os the os
     * @return the boolean
     */
    public static boolean isEmpty(Object[] os) {
        return isNull(os) || os.length == 0;
    }

    /**
     * Is empty boolean.
     *
     * @param l the l
     * @return the boolean
     */
    public static boolean isEmpty(Collection<?> l) {
        return isNull(l) || l.isEmpty();
    }

    /**
     * Is empty boolean.
     *
     * @param m the m
     * @return the boolean
     */
    public static boolean isEmpty(Map<?, ?> m) {
        return isNull(m) || m.isEmpty();
    }

    /**
     * Is null boolean.
     *
     * @param o the o
     * @return the boolean
     */
    private static boolean isNull(Object o) {
        return o == null;
    }
}
