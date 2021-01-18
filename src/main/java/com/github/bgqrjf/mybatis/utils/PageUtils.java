package com.github.bgqrjf.mybatis.utils;

import com.github.pagehelper.PageHelper;

/**
 * 分页工具类
 *
 * @author yangxin
 */
public class PageUtils {
    private PageUtils() {

    }

    public static void startPage(int page, int size) {
        startPage(page, size, null);
    }

    /**
     * 查询方法前调用 ，紧跟在这个方法后的第一个MyBatis 查询方法会被进行分页。
     *
     * @param page    当前页
     * @param size    数据条数
     * @param orderBy 排序字段,如 orderBy="-id" 则根据id递减排序。默认升序
     */
    public static void startPage(int page, int size, String orderBy) {
        if (page != 0 && size != 0) {
            PageHelper.startPage(page, size);
        }
        orderBy(orderBy);
    }

    private static void orderBy(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return;
        }
        String order = parse(orderBy);
        if (StringUtils.isNotBlank(order)) {
            PageHelper.orderBy(order);
        }
    }


    private static String parse(String orderBy) {
        String[] orders = orderBy.split(StringUtils.REG_EN_COMMA);
        StringBuilder sb = new StringBuilder();
        for (String order : orders) {
            if (StringUtils.isEmpty(order)) {
                continue;
            }
            String underLineOrder = StringUtils.camel2Underline(order);
            sb.append(StringUtils.REG_EN_COMMA);
            //orderBy -id则按照id降序
            if (underLineOrder.startsWith("-")) {
                sb.append(underLineOrder.substring(1)).append(" desc");
            } else {
                sb.append(underLineOrder).append(" asc");
            }
        }
        if (sb.length() > 0) {
            return sb.substring(1);
        }
        return null;
    }

    /**
     * 清理 ThreadLocal 存储的分页参数
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }

}
