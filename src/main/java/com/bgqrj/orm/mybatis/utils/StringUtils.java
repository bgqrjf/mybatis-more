package com.bgqrj.orm.mybatis.utils;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author yangxin
 */
public class StringUtils {

    public static final String REG_EN_COMMA = ",";
    public static final String REG_UNDERLINE = "_";
    public static final String EMPTY_STR = "";
    public static final char REG_EN_COMMA_CR = ',';

    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() < 1;
    }

    public static boolean isBlank(String str) {
        return isEmpty(str) || str.trim().length() < 1;
    }


    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 驼峰法转下划线
     *
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        String pattern = "[A-Z]";
        Pattern humpPattern = Pattern.compile(pattern);
        Matcher matcher = humpPattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, REG_UNDERLINE + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    public static String join(String[] array) {
        return join(Arrays.asList(array));
    }

    public static void join(String[] array, char separator, StringBuilder sb) {
        join(Arrays.asList(array), separator, sb);
    }

    public static <T> String join(Collection<T> collection) {
        return join(collection, REG_EN_COMMA_CR);
    }

    public static <T> String join(Collection<T> collection, char separator) {
        if (collection.isEmpty()) {
            return EMPTY_STR;
        } else {
            StringBuilder result = new StringBuilder();
            join(collection, separator, result);
            return result.toString();
        }
    }

    public static <T> String join(Iterable<T> iterable) {
        if (!iterable.iterator().hasNext()) {
            return EMPTY_STR;
        }
        StringBuilder result = new StringBuilder();
        join(iterable, REG_EN_COMMA_CR, result);
        return result.toString();
    }

    public static <T> void join(Iterable<T> iterable, char separator, StringBuilder sb) {
        join(iterable, separator, String::valueOf, sb);
    }

    public static <T> void join(T[] array, char separator, Function<T> function, StringBuilder sb) {
        join(Arrays.asList(array), separator, function, sb);
    }

    public static <T> void join(Iterable<T> iterable, char separator, Function<T> function, StringBuilder sb) {
        boolean first = true;
        T value;
        for (Iterator<T> var5 = iterable.iterator(); var5.hasNext(); sb.append(function.apply(value))) {
            value = var5.next();
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
        }
    }


    private interface Function<T> {
        /**
         * Function
         *
         * @param var1 var1
         * @return string
         */
        String apply(T var1);
    }


}
