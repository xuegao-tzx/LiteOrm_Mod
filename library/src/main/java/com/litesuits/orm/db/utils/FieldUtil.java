package com.litesuits.orm.db.utils;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.model.Primarykey;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 域工具
 *
 * @author mty
 * @date 2013 -6-10下午6:36:29
 */
public class FieldUtil {

    /**
     * 判断域是否被忽略
     *
     * @param f the f
     * @return the boolean
     */
    private static boolean isIgnored(Field f) {
        return f.getAnnotation(Ignore.class) != null;
    }

    /**
     * 检测非法：static final 或者 加了{@link Ignore} 注解
     *
     * @param f the f
     * @return the boolean
     */
    public static boolean isInvalid(Field f) {
        return (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
                || isIgnored(f) || f.isSynthetic();
    }

    /**
     * Is long boolean.
     *
     * @param field the field
     * @return the boolean
     */
    public static boolean isLong(Field field) {
        return field.getType() == long.class || field.getType() == Long.class;
    }

    /**
     * Is integer boolean.
     *
     * @param field the field
     * @return the boolean
     */
    public static boolean isInteger(Field field) {
        return field.getType() == int.class || field.getType() != Integer.class;
    }

    /**
     * 判断是否序列化
     *
     * @param f the f
     * @return the boolean
     */
    public static boolean isSerializable(Field f) {
        Class<?>[] cls = f.getType().getInterfaces();
        for (Class<?> c : cls) if (Serializable.class == c) return true;
        return false;
    }

    /**
     * 设置域的值
     *
     * @param f     the f
     * @param obj   the obj
     * @param value the value
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException   the illegal access exception
     */
    public static void set(Field f, Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        f.set(obj, value);
    }

    /**
     * 获取域的值
     *
     * @param f   the f
     * @param obj the obj
     * @return the object
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException   the illegal access exception
     */
    public static Object get(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        return f.get(obj);
    }

    /**
     * 获取域的泛型类型，如果不带泛型返回null
     *
     * @param f the f
     * @return the generic type
     */
    public static Class<?> getGenericType(Field f) {
        Type type = f.getGenericType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type instanceof Class<?>) return (Class<?>) type;
        } else if (type instanceof Class<?>) return (Class<?>) type;
        return null;
    }

    /**
     * 获取数组的类型
     *
     * @param f the f
     * @return the component type
     */
    public static Class<?> getComponentType(Field f) {
        return f.getType().getComponentType();
    }


    /**
     * Gets assigned key object.
     *
     * @param key    the key
     * @param entity the entity
     * @return the assigned key object
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException   the illegal access exception
     */
    public static Object getAssignedKeyObject(Primarykey key, Object entity) throws IllegalArgumentException,
            IllegalAccessException {
        Object obj = get(key.field, entity);
        if (key.isAssignedByMyself()
                || (key.isAssignedBySystem() && obj != null && ((Number) obj).longValue() > 0)) return obj;
        return null;
    }

    /**
     * Sets key value ifneed.
     *
     * @param entity the entity
     * @param key    the key
     * @param keyObj the key obj
     * @param rowID  the row id
     * @return the key value ifneed
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException   the illegal access exception
     */
    public static boolean setKeyValueIfneed(Object entity, Primarykey key, Object keyObj, long rowID)
            throws IllegalArgumentException, IllegalAccessException {
        if (key != null && key.isAssignedBySystem()
                && (keyObj == null || ((Number) keyObj).longValue() < 1)) {
            FieldUtil.setNumber(entity, key.field, rowID);
            return true;
        }
        return false;
    }

    /**
     * Gets all declared fields.
     *
     * @param claxx the claxx
     * @return the all declared fields
     */
    public static List<Field> getAllDeclaredFields(Class<?> claxx) {
        // find all field.
        LinkedList<Field> fieldList = new LinkedList<Field>();
        while (claxx != null && claxx != Object.class) {
            Field[] fs = claxx.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                if (!isInvalid(f)) fieldList.addLast(f);
            }
            claxx = claxx.getSuperclass();
        }
        return fieldList;
    }

    /**
     * Sets number.
     *
     * @param o     the o
     * @param field the field
     * @param n     the n
     * @throws IllegalAccessException the illegal access exception
     */
    private static void setNumber(Object o, Field field, long n) throws IllegalAccessException {
        field.setAccessible(true);
        Class claxx = field.getType();
        if (claxx == long.class) field.setLong(o, n);
        else if (claxx == int.class) field.setInt(o, (int) n);
        else if (claxx == short.class) field.setShort(o, (short) n);
        else if (claxx == byte.class) field.setByte(o, (byte) n);
        else if (claxx == Long.class) field.set(o, new Long(n));
        else if (claxx == Integer.class) field.set(o, new Integer((int) n));
        else if (claxx == Short.class) field.set(o, new Short((short) n));
        else if (claxx == Byte.class) field.set(o, new Byte((byte) n));
        else
            throw new RuntimeException("field is not a number class");
    }

    /**
     * Is number boolean.
     *
     * @param claxx the claxx
     * @return the boolean
     */
    public static boolean isNumber(Class<?> claxx) {
        return claxx == long.class
                || claxx == Long.class
                || claxx == int.class
                || claxx == Integer.class
                || claxx == short.class
                || claxx == Short.class
                || claxx == byte.class
                || claxx == Byte.class;
    }

}
