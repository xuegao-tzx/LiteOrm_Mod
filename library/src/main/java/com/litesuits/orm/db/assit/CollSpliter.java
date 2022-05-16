package com.litesuits.orm.db.assit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 辅助事务
 *
 * @author mty
 * @date 2013 -6-15下午11:09:15
 */
public class CollSpliter {

    /**
     * 将 collection 拆分成 N 组ArrayList，每组 perSize 个元素，最后一组元素数量未知。
     * <p>
     * {@link Spliter#oneSplit(ArrayList)}将被调用N次，N >= 1.
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @param perSize    the per size
     * @param spliter    the spliter
     * @return sum of {@link Spliter#oneSplit(ArrayList)}
     * @throws Exception the exception
     */
    public static <T> int split(Collection<T> collection, int perSize, Spliter<T> spliter) throws Exception {
        ArrayList<T> list = new ArrayList<T>();
        int count = 0;
        if (collection.size() <= perSize) {
            list.addAll(collection);
            count += spliter.oneSplit(list);
        } else {
            int i = 0, j = 1;
            for (T data : collection) {
                if (i < j * perSize) list.add(data);
                else {
                    count += spliter.oneSplit(list);
                    j++;
                    list.clear();
                    list.add(data);
                }
                i++;
            }
            if (list.size() > 0) count += spliter.oneSplit(list);
        }
        return count;
    }

    /**
     * The interface Spliter.
     *
     * @param <T> the type parameter
     */
    public interface Spliter<T> {
        /**
         * One split int.
         *
         * @param list the list
         * @return the int
         * @throws Exception the exception
         */
        int oneSplit(ArrayList<T> list) throws Exception;
    }
}