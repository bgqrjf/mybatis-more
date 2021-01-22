package com.bgqrj.mybatis.demo.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * 描述: 字符串工具类
 *
 * @author yangxin
 * 日期: 2020/4/26
 */
public final class StringUtils {

    public static final String REG_EN_COMMA = ",";
    public static final String REG_UNDERLINE = "_";
    public static final String REG_EN_SEMICOLON = ";";
    public static final String EMPTY_STR = "";
    public static final String BLANK_STR = " ";
    public static final String REG_MINUS_SIGN = "-";

    private static final char CHAR_EN_COMMA = ',';

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00%");

    private StringUtils() {

    }

    public static boolean isEmpty(String str) {
        return Objects.isNull(str) || str.length() < 1;
    }

    public static boolean isBlank(String str) {
        return isEmpty(str) || str.trim().length() < 1;
    }


    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    public static String join(String[] array) {
        return join(Arrays.asList(array));
    }

    public static void join(String[] array, char separator, StringBuilder sb) {
        join(Arrays.asList(array), separator, sb);
    }

    public static <T> String join(Collection<T> collection) {
        return join(collection, CHAR_EN_COMMA);
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
        if (!iterable.iterator().hasNext())
            return EMPTY_STR;
        StringBuilder result = new StringBuilder();
        join(iterable, CHAR_EN_COMMA, result);
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
        String apply(T var1);
    }

    /**
     * BigDecimal转换成百分数字符串
     *
     * @param bigDecimal BigDecimal
     * @return 两位有效数字百分比字符串
     */
    public static String converseToPercentage(BigDecimal bigDecimal) {
        if (Objects.isNull(bigDecimal)) {
            bigDecimal = BigDecimal.ZERO;
        }
        return DECIMAL_FORMAT.format(bigDecimal);
    }


}
