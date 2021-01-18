package com.bgqrj.orm.mybatis.query;

import com.bgqrj.orm.mybatis.query.group.AggregateInfo;
import com.bgqrj.orm.mybatis.query.mapper.Mapper;
import com.bgqrj.orm.mybatis.utils.CollectionUtils;
import com.bgqrj.orm.mybatis.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 通用Query对象
 *
 * @author yangxin
 */
public class Query<T> {
    private static final Logger log = LoggerFactory.getLogger(Query.class);
    /**
     * 升序
     */
    public static final int ORDER_ASC = 1;
    /**
     * 降序
     */
    public static final int ORDER_DESC = 2;
    private final QueryExample<T> condition;
    private Mapper<T> mapper;
    private Example.Criteria criteria;
    private Class<T> modelClass;
    private SqlSessionTemplate sqlSessionTemplate;
    private String[] sonModelNames;
    private boolean haveNull = false;
    private boolean useGroup = false;

    public QueryExample<T> getCondition() {
        return condition;
    }

    public Query(Mapper<T> mapper, Class<T> modelClass) {
        this.mapper = mapper;
        this.modelClass = modelClass;
        this.condition = new QueryExample<T>(modelClass);
        this.criteria = condition.createCriteria();
    }

    public Query(Mapper<T> mapper, Class<T> modelClass, SqlSessionTemplate sqlSessionTemplate) {
        this.mapper = mapper;
        this.modelClass = modelClass;
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.condition = new QueryExample<>(modelClass);
        this.criteria = condition.createCriteria();
    }

    /**
     * 创建查询对象
     *
     * @param mapperClass        要查询的实体对应的Mapper.class
     * @param sqlSessionTemplate 支持指定数据源的sqlSessionTemplate进行查询
     * @param <T>                实体类
     * @return Query查询对象
     */
    public static <T> Query<T> create(Class<? extends Mapper<T>> mapperClass, SqlSessionTemplate sqlSessionTemplate) {
        ParameterizedType pt = (ParameterizedType) mapperClass.getGenericInterfaces()[0];
        Class<T> modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        Mapper<T> mapper = sqlSessionTemplate.getMapper(mapperClass);
        return new Query<>(mapper, modelClass, sqlSessionTemplate);
    }


    public Example.OrderBy orderBy(String order) {
        return condition.orderBy(order);
    }

    /**
     * orderBy排序
     *
     * @param field 排序依据的字段(对应实体类的字段名，而不是表字段名)
     * @param order 1 升序; 2降序
     * @return Query
     */
    public Query<T> orderBy(String field, int order) {
        if (order == ORDER_ASC) {
            orderBy(field).asc();
        } else {
            orderBy(field).desc();
        }
        return this;
    }

    /**
     * 设置查询的字段
     *
     * @param properties 实体类的字段名
     * @return Query
     */
    public Query<T> selectProperties(String... properties) {
        condition.selectProperties(properties);
        return this;
    }

    /**
     * 不查询的字段(对应实体类的字段名，而不是表字段名)
     *
     * @param properties 实体类的字段名
     * @return Query
     */
    public Query<T> excludeProperties(String... properties) {
        condition.excludeProperties(properties);
        return this;
    }


    /**
     * 去重(sql查询的关键字distinct)
     *
     * @return Query
     */
    public Query<T> distinct() {
        condition.setDistinct(true);
        return this;
    }

    /**
     * 添加'等于'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> andEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andEqualTo(fieldName, value);
        return this;
    }

    /**
     * 添加 or filed=value 的条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> orEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orEqualTo(fieldName, value);
        return this;
    }

    /**
     * 添加  ’and 不等于‘查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> andNotEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andNotEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andNotEqualTo(fieldName, value);
        return this;
    }

    /**
     * 添加  ’or 不等于‘查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> orNotEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orNotEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orNotEqualTo(fieldName, value);
        return this;
    }


    /**
     * 添加 ' and is null'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @return Query
     */
    public Query<T> andIsNull(String fieldName) {
        criteria.andIsNull(fieldName);
        return this;
    }

    /**
     * 添加' or is null'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @return Query
     */
    public Query<T> orIsNull(String fieldName) {
        criteria.orIsNull(fieldName);
        return this;
    }

    /**
     * 添加'and is not null'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @return Query
     */
    public Query<T> andIsNotNull(String fieldName) {
        criteria.andIsNotNull(fieldName);
        return this;
    }


    /**
     * 添加'or is not null'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @return Query
     */
    public Query<T> orIsNotNull(String fieldName) {
        criteria.orIsNotNull(fieldName);
        return this;
    }

    /**
     * 添加'between A and B'条件
     *
     * @param fieldName 对应实体类的字段名
     * @param left      左边的值
     * @param right     右边的值
     * @return Query
     */
    public Query<T> andBetween(String fieldName, Object left, Object right) {
        if (Objects.isNull(left) || Objects.isNull(right)) {
            log.warn("Method andBetween param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andBetween(fieldName, left, right);
        return this;
    }

    /**
     * 添加 or between条件
     *
     * @param fieldName 对应实体类的字段名
     * @param left      左区间
     * @param right     右区间
     * @return Query
     */
    public Query<T> orBetween(String fieldName, Object left, Object right) {
        if (StringUtils.isEmpty(fieldName) || Objects.isNull(left) || Objects.isNull(right)) {
            log.warn("Method orBetween param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orBetween(fieldName, left, right);
        return this;
    }

    /**
     * 正则表达式
     *
     * @param fieldName 字段名
     * @param regexp    正则规则
     * @return Query
     */
    public Query<T> andRegexp(String fieldName, String regexp) {
        if (StringUtils.isEmpty(fieldName) || StringUtils.isEmpty(regexp)) {
            log.warn("Method andRegexp param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        String column = condition.column(fieldName);
        criteria.andCondition(column + " REGEXP " + regexp);
        return this;
    }


    /**
     * 添加'and like'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> andLike(String fieldName, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method andLike param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andLike(fieldName, value);
        return this;
    }


    /**
     * 添加'or like'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> orLike(String fieldName, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method orLike param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orLike(fieldName, value);
        return this;
    }

    /**
     * 添加'and in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的集合
     * @return Query
     */
    public Query<T> andIn(String fieldName, Iterable<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            log.warn("Method andIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andIn(fieldName, value);
        return this;
    }

    /**
     * 添加'and in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的字符串
     * @return Query
     */
    public Query<T> andIn(String fieldName, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method andIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.andIn(fieldName, Arrays.asList(data));
        return this;
    }

    /**
     * 添加'and not in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的字符串
     * @return Query
     */
    public Query<T> andNotIn(String fieldName, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method andNotIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.andNotIn(fieldName, Arrays.asList(data));
        return this;
    }

    /**
     * 添加' and not in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的集合
     * @return Query
     */
    public Query<T> andNotIn(String fieldName, Iterable<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            log.warn("Method andNotIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andNotIn(fieldName, value);
        return this;
    }


    /**
     * 添加'or in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的集合
     * @return Query
     */
    public Query<T> orIn(String fieldName, Iterable<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            log.warn("Method orIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orIn(fieldName, value);
        return this;
    }

    /**
     * 添加'or in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的字符串
     * @return
     */
    public Query<T> orIn(String fieldName, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method orIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.orIn(fieldName, Arrays.asList(data));
        return this;
    }

    /**
     * 添加'or not in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的字符串
     * @return Query
     */
    public Query<T> orNotIn(String fieldName, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method orNotIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.orNotIn(fieldName, Arrays.asList(data));
        return this;
    }

    /**
     * 添加'or not in'查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值的集合
     * @return Query
     */
    public Query<T> orNotIn(String fieldName, Iterable<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            log.warn("Method orNotIn param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orNotIn(fieldName, value);
        return this;
    }

    /**
     * 添加' and > '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> andGreaterThan(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andGreaterThan param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andGreaterThan(fieldName, value);
        return this;
    }


    /**
     * 添加' or > '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> orGreaterThan(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orGreaterThan param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orGreaterThan(fieldName, value);
        return this;
    }

    /**
     * 添加'and >= '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> andGreaterThanOrEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andGreaterThanOrEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andGreaterThanOrEqualTo(fieldName, value);
        return this;
    }

    /**
     * 添加'or >= '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> orGreaterThanOrEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orGreaterThanOrEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orGreaterThanOrEqualTo(fieldName, value);
        return this;
    }

    /**
     * 添加'and <= '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> andLessThanOrEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andLessThanOrEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andLessThanOrEqualTo(fieldName, value);
        return this;
    }


    /**
     * 添加' and < '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> andLessThan(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andLessThan param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.andLessThan(fieldName, value);
        return this;
    }


    /**
     * 添加' or < '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> orLessThan(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orLessThan param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orLessThan(fieldName, value);
        return this;
    }

    /**
     * 添加'or <= '查询条件
     *
     * @param fieldName 对应实体类的字段名
     * @param value     值
     * @return Query
     */
    public Query<T> orLessThanOrEqualTo(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orLessThanOrEqualTo param {} is null! Sql not exec!",fieldName);
            haveNull = true;
        }
        criteria.orLessThanOrEqualTo(fieldName, value);
        return this;
    }

    /**
     * 添加'and ()'查询条件
     *
     * @param query 查询条件
     * @return Query
     */
    public Query<T> and(Query<T> query) {
        if (Objects.isNull(query)) {
            log.warn("Method and param is null! Sql not exec!");
            haveNull = true;
        } else {
            condition.and(query.criteria);
        }
        return this;
    }

    /**
     * 添加'and ()'查询条件
     *
     * @param query 查询条件
     * @return Query
     */
    public Query<T> or(Query<T> query) {
        if (Objects.isNull(query)) {
            log.warn("Method or param is null! Sql not exec!");
            haveNull = true;
        } else {
            condition.or(query.criteria);
        }
        return this;
    }

    /**
     * 分组查询时,设置组函数列表
     *
     * @param aggregateInfos 组函数列表
     * @return Query
     */
    public Query<T> groupByAggregateFunc(List<AggregateInfo> aggregateInfos) {
        condition.getGroupByCondition().aggregateBy(aggregateInfos);
        this.useGroup = true;
        return this;
    }

    /**
     * 分组查询时,设置要 group by的字段列表
     *
     * @param groupByProperties group by的字段列表
     * @return Query
     */
    public Query<T> groupByProperties(String... groupByProperties) {
        this.useGroup = true;
        condition.getGroupByCondition().groupBy(groupByProperties);
        return this;
    }


    /**
     * 子模块 oneToMany、ToOne
     * 支持下级关联格式
     *
     * @param names 子模块名称
     * @return Query
     */
    public Query<T> sonModel(String... names) {
        this.sonModelNames = names;
        return this;
    }


    /**
     * 执行查询
     *
     * @return 结果列表
     */
    public List<T> execute() {
        List<T> list;
        if (useGroup) {
            if (!allowGroupQuery()) {
                return new ArrayList<>(2);
            }
            list = mapper.selectAggregationByExample(condition, condition.getGroupByCondition());
        } else {
            if (haveNull) {
                return new ArrayList<>(2);
            }
            if (Objects.isNull(sonModelNames) || sonModelNames.length < 1) {
                list = mapper.selectByCondition(condition);
            } else {
                SonModelUtils<T> sonModelUtils = new SonModelUtils<>(sonModelNames, modelClass, sqlSessionTemplate);
                list = sonModelUtils.warpSonModel(mapper.selectByCondition(condition));
            }
        }
        return list;
    }

    /**
     * 查询一个结果
     *
     * @return model
     */
    public T selectOne() {
        List<T> data;
        if (useGroup) {
            if (allowGroupQuery()) {
                PageHelper.offsetPage(0, 1, false);
                data = mapper.selectAggregationByExample(condition, condition.getGroupByCondition());
            } else {
                return null;
            }
        } else {
            if (haveNull) {
                return null;
            }
            PageHelper.offsetPage(0, 1, false);
            data = mapper.selectByCondition(condition);
        }
        PageHelper.clearPage();
        SonModelUtils<T> sonModelUtils = new SonModelUtils<>(sonModelNames, modelClass, sqlSessionTemplate);
        return CollectionUtils.getFirstOrNull(sonModelUtils.warpSonModel(data));
    }

    /**
     * 执行’count()‘函数
     *
     * @return cnt
     */
    public int count() {
        if (haveNull) {
            return 0;
        }
        return mapper.selectCountByCondition(condition);
    }

    private boolean allowGroupQuery() {
        return !CollectionUtils.isEmpty(condition.getGroupByCondition().getAggregateInfoList()) || !CollectionUtils.isEmpty(condition.getGroupByCondition().getGroupByProperties());
    }

}
