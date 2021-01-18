package com.github.bgqrjf.mybatis.query.group;

import tk.mybatis.mapper.additional.aggregation.AggregateType;

import java.util.Objects;

/**
 * 分组函数实体属性设置
 *
 * @author yangxin
 */
public class AggregateInfo {
    /**
     * @see AggregateType 支持的函数方法
     */
    private AggregateType aggregateType;
    /**
     * 函数统计的属性。用法:如 SUM(t1_filed) 中的 代表t1_filed字段的实体字段t1Filed
     */
    private String aggregateProperty;
    /**
     * 字段的别名。用法: 如 SUM(t1_filed) as Field2,代表Field2。默认值为实体的属性字段
     */
    private String aggregateAliasName;

    public AggregateInfo(AggregateType aggType, String aggProperty, String aggAliasName) {
        this.aggregateType = aggType;
        this.aggregateProperty = aggProperty;
        this.aggregateAliasName = aggAliasName;
    }

    public AggregateType getAggregateType() {
        return aggregateType;
    }

    public String getAggregateProperty() {
        return aggregateProperty;
    }

    public String getAggregateAliasName() {
        return aggregateAliasName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AggregateInfo that = (AggregateInfo) o;
        return aggregateType.ordinal() == that.aggregateType.ordinal() &&
                Objects.equals(aggregateProperty, that.aggregateProperty) &&
                Objects.equals(aggregateAliasName, that.aggregateAliasName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateType.ordinal(), aggregateProperty, aggregateAliasName);
    }
}