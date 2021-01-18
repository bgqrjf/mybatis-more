package com.github.bgqrjf.mybatis.query.group;

import com.github.bgqrjf.mybatis.utils.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 分组查询条件实体
 *
 * @author yangxin
 * 日期: 2020/9/8
 */
public class GroupByCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> groupByProperties;
    private List<AggregateInfo> aggregateInfoList;

    public List<String> getGroupByProperties() {
        return groupByProperties;
    }

    public void setGroupByProperties(List<String> groupByProperties) {
        this.groupByProperties = groupByProperties;
    }

    public List<AggregateInfo> getAggregateInfoList() {
        return aggregateInfoList;
    }

    public void setAggregateInfoList(List<AggregateInfo> aggregateInfoList) {
        this.aggregateInfoList = aggregateInfoList;
    }

    public GroupByCondition() {
        this.groupByProperties = new ArrayList<>();
        this.aggregateInfoList = new ArrayList<>();
    }

    public GroupByCondition(List<AggregateInfo> aggInfoList, String[] groupByProperties) {
        this.groupByProperties = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(aggInfoList)) {
            this.aggregateBy(aggInfoList);
        }
        this.groupBy(groupByProperties);
    }


    public GroupByCondition groupBy(String... groupByProperties) {
        this.groupByProperties.addAll(Arrays.asList(groupByProperties));
        return this;
    }

    public GroupByCondition aggregateBy(List<AggregateInfo> aggregateInfos) {
        this.aggregateInfoList = aggregateInfos;
        return this;
    }

    public static GroupByCondition init() {
        return new GroupByCondition();
    }


}
