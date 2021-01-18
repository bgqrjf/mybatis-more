package com.bgqrj.orm.mybatis.utils;


import java.util.*;

/**
 * 集合工具类
 *
 * @author yangxin
 */
public class CollectionUtils {
    private CollectionUtils() {

    }

    public static boolean isEmpty(Collection collection) {
        return Objects.isNull(collection) || collection.size() == 0;
    }

    public static boolean isEmpty(Iterable iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }


    /**
     * 按指定数量，将集合分割为n个部分
     *
     * @param list List
     * @param len  数量
     * @return List<List < T>>
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        if (isEmpty(list)) {
            return new ArrayList<>(2);
        }
        List<List<T>> result = new ArrayList<>();

        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, (Math.min((i + 1) * len, size)));
            result.add(subList);
        }
        return result;
    }

    public static <T> T getFirstOrNull(List<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection.get(0);
    }

    public static <T> T getLastOrNull(List<T> collection) {
        if (Objects.isNull(collection) || collection.size() == 0) return null;
        return collection.get(collection.size() - 1);
    }

    public static <T> void shuffle(List<T> list) {
        int size = list.size();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int randomPos = random.nextInt(size);
            Collections.swap(list, i, randomPos);
        }
    }


}
