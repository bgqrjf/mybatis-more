package com.bgqrj.orm.mybatis.query;

import com.bgqrj.orm.mybatis.query.group.GroupByCondition;
import tk.mybatis.mapper.weekend.Weekend;



/**
 * 通用example查询模板
 *
 * @author yangxin
 */
public class QueryExample<T> extends Weekend<T> {
    private GroupByCondition groupByCondition;

    public GroupByCondition getGroupByCondition() {
        return groupByCondition;
    }

    public QueryExample(Class<T> entityClass) {
        super(entityClass);
        groupByCondition = GroupByCondition.init();
    }

    public QueryExample(Class<T> entityClass, boolean exists) {
        super(entityClass, exists);
        groupByCondition = GroupByCondition.init();
    }

    public QueryExample(Class<T> entityClass, boolean exists, boolean notNull) {
        super(entityClass, exists, notNull);
        groupByCondition = GroupByCondition.init();
    }

    public String column(String fieldName) {
        if (propertyMap.containsKey(fieldName)) {
            return propertyMap.get(fieldName).getColumn();
        } else {
            return fieldName;
        }
    }
}
