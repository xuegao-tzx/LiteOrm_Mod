package com.litesuits.orm.kvdb;

import java.util.List;

/**
 * 数据操作定义
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author mty
 * @date 2013 -6-2上午2:37:56
 */
public interface DataCache<K, V> {

    /**
     * Save object.
     *
     * @param key  the key
     * @param data the data
     * @return the object
     */
    Object save(K key, V data);

    /**
     * Delete object.
     *
     * @param key the key
     * @return the object
     */
    Object delete(K key);

    /**
     * Update object.
     *
     * @param key  the key
     * @param data the data
     * @return the object
     */
    Object update(K key, V data);

    /**
     * Query list.
     *
     * @param arg the arg
     * @return the list
     */
    List<V> query(String arg);
}
