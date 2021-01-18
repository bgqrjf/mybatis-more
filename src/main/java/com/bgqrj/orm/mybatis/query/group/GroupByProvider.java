package com.bgqrj.orm.mybatis.query.group;

import com.bgqrj.orm.mybatis.utils.CollectionUtils;
import com.bgqrj.orm.mybatis.utils.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.SqlReservedWords;
import tk.mybatis.mapper.util.StringUtil;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author liuchan
 * @author liuzh
 * @author yangxin
 */
public class GroupByProvider extends MapperTemplate {

    public GroupByProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public static String aggregationSelectClause(Class<?> entityClass, String wrapKeyword, GroupByCondition condition) {
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        Map<String, EntityColumn> propertyMap = entityTable.getPropertyMap();
        StringBuilder selectBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(condition.getAggregateInfoList())) {
            Set<AggregateInfo> aggregateInfoSet = new HashSet<>(condition.getAggregateInfoList());
            List<String> aggFunList = new ArrayList<>();
            for (AggregateInfo aggregateInfo : aggregateInfoSet) {
                StringBuilder aggColumnBuilder = new StringBuilder(aggregateInfo.getAggregateType().name());
                if (StringUtils.isNotEmpty(aggregateInfo.getAggregateProperty())) {
                    String columnName = propertyMap.get(aggregateInfo.getAggregateProperty()).getColumn();
                    aggColumnBuilder.append("(").append(columnName).append(")");
                    aggColumnBuilder.append(" AS ");
                }
                if (StringUtil.isNotEmpty(aggregateInfo.getAggregateAliasName())) {
                    aggColumnBuilder.append(aggregateInfo.getAggregateAliasName());
                } else {
                    aggColumnBuilder.append(wrapKeyword(wrapKeyword, aggregateInfo.getAggregateProperty()));
                }
                aggFunList.add(aggColumnBuilder.toString());
            }
            selectBuilder.append(StringUtils.join(aggFunList));

        }
        if (CollectionUtils.isNotEmpty(condition.getGroupByProperties())) {
            for (String property : condition.getGroupByProperties()) {
                selectBuilder.append(", ");
                String columnName = propertyMap.get(property).getColumn();
                selectBuilder.append(columnName).append(" AS ").append(wrapKeyword(wrapKeyword, property));
            }
        }
        return selectBuilder.toString();
    }

    private static String wrapKeyword(String wrapKeyword, String columnName) {
        if (StringUtil.isNotEmpty(wrapKeyword) && SqlReservedWords.containsWord(columnName)) {
            return MessageFormat.format(wrapKeyword, columnName);
        }
        return columnName;
    }

    public static String aggregationGroupBy(Class<?> entityClass, String wrapKeyword, GroupByCondition condition) {
        if (condition.getGroupByProperties() != null && condition.getGroupByProperties().size() > 0) {
            EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            Map<String, EntityColumn> propertyMap = entityTable.getPropertyMap();
            StringBuilder groupByBuilder = new StringBuilder();
            for (String property : condition.getGroupByProperties()) {
                if (groupByBuilder.length() == 0) {
                    groupByBuilder.append(" GROUP BY ");
                } else {
                    groupByBuilder.append(", ");
                }
                groupByBuilder.append(propertyMap.get(property).getColumn());
            }
            return groupByBuilder.toString();
        }
        return "";
    }

    /**
     * 根据Example查询总数
     *
     * @param ms
     * @return sql
     */
    public String selectAggregationByExample(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if (isCheckExampleEntityClass()) {
            sql.append(SqlHelper.exampleCheck(entityClass));
        }
        sql.append("SELECT ${@com.sftcwl.orm.mybatis.query.group.GroupByProvider@aggregationSelectClause(");
        sql.append("@").append(entityClass.getCanonicalName()).append("@class");
        sql.append(", '").append(getConfig().getWrapKeyword()).append("'");
        sql.append(", aggregateCondition");
        sql.append(")} ");
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateByExampleWhereClause());
        sql.append(" ${@com.sftcwl.orm.mybatis.query.group.GroupByProvider@aggregationGroupBy(");
        sql.append("@").append(entityClass.getCanonicalName()).append("@class");
        sql.append(", '").append(getConfig().getWrapKeyword()).append("'");
        sql.append(", aggregateCondition");
        sql.append(")} ");
        sql.append(SqlHelper.exampleOrderBy("example", entityClass));
        sql.append(SqlHelper.exampleForUpdate());
        return sql.toString();
    }

}
