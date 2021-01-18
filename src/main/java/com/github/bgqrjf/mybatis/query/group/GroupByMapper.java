package com.github.bgqrjf.mybatis.query.group;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @author yangxin
 * @date 2020-09-08
 */
@RegisterMapper
public interface GroupByMapper<T> {
    /**
     * 根据example和aggregateCondition进行聚合查询
     * 分组不支持having条件过滤， 如需要建议使用xml文件
     *
     * @param example 查询example
     * @param aggregateCondition 可以设置聚合查询的属性和分组属性
     * @return 返回聚合查询属性和分组属性的值
     */
    @SelectProvider(type = GroupByProvider.class, method = "dynamicSQL")
    List<T> selectAggregationByExample(@Param("example") Object example, @Param("aggregateCondition") GroupByCondition aggregateCondition);


}

