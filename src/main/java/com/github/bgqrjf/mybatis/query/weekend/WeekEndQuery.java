package com.github.bgqrjf.mybatis.query.weekend;

import com.github.bgqrjf.mybatis.query.mapper.Mapper;
import com.github.bgqrjf.mybatis.utils.CollectionUtils;
import com.github.bgqrjf.mybatis.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.bgqrjf.mybatis.query.QueryExample;
import com.github.bgqrjf.mybatis.query.SonModelUtils;
import com.github.bgqrjf.mybatis.query.group.AggregateInfo;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendCriteria;
import tk.mybatis.mapper.weekend.reflection.Reflections;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 通用Query对象
 *
 * @author yangxin
 */
public class WeekEndQuery<T> {
    private static final Logger log = LoggerFactory.getLogger(WeekEndQuery.class);
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
    private WeekendCriteria<T, Object> criteria;
    private Class<T> modelClass;
    private SqlSessionTemplate sqlSessionTemplate;
    private String[] sonModelNames;
    private boolean haveNull = false;
    private boolean useGroup = false;

    public QueryExample<T> getCondition() {
        return condition;
    }

    public WeekEndQuery(Mapper<T> mapper, Class<T> modelClass) {
        this.mapper = mapper;
        this.modelClass = modelClass;
        this.condition = new QueryExample<>(modelClass);
        this.criteria = condition.weekendCriteria();
    }

    public WeekEndQuery(Mapper<T> mapper, Class<T> modelClass, SqlSessionTemplate sqlSessionTemplate) {
        this.mapper = mapper;
        this.modelClass = modelClass;
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.condition = new QueryExample<>(modelClass);
        this.criteria = condition.weekendCriteria();
    }

    /**
     * 创建查询对象
     *
     * @param mapperClass        要查询的实体对应的Mapper.class
     * @param sqlSessionTemplate 支持指定数据源的sqlSessionTemplate进行查询
     * @param <T>                实体类
     * @return Query查询对象
     */
    public static <T> WeekEndQuery<T> create(Class<? extends Mapper<T>> mapperClass, SqlSessionTemplate sqlSessionTemplate) {
        ParameterizedType pt = (ParameterizedType) mapperClass.getGenericInterfaces()[0];
        Class<T> modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        Mapper<T> mapper = sqlSessionTemplate.getMapper(mapperClass);
        return new WeekEndQuery<>(mapper, modelClass, sqlSessionTemplate);
    }


    public Example.OrderBy orderBy(String order) {
        return condition.orderBy(order);
    }

    /**
     * orderBy排序
     *
     * @param fn    排序依据的字段(
     * @param order 1 升序; 2降序
     * @return Query
     */
    public WeekEndQuery<T> orderBy(Func<T> fn, int order) {
        String field = Reflections.fnToFieldName(fn);
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
     * @param properties 实体类的字段
     * @return Query
     */
    @SafeVarargs
    public final WeekEndQuery<T> selectProperties(Func<T>... properties) {
        condition.selectProperties(columnArray(Arrays.asList(properties)));
        return this;
    }

    private String[] columnArray(List<Func<T>> funcList) {
        String[] columnArr = new String[]{};
        if (CollectionUtils.isNotEmpty(funcList)) {
            columnArr = new String[funcList.size()];
            int i = 0;
            for (Func<T> fn : funcList) {
                columnArr[i] = Reflections.fnToFieldName(fn);
                i++;
            }
        }
        return columnArr;
    }

    /**
     * 不查询的字段
     *
     * @param properties 实体类的字段
     * @return Query
     */
    @SafeVarargs
    public final WeekEndQuery<T> excludeProperties(Func<T>... properties) {
        condition.excludeProperties(columnArray(Arrays.asList(properties)));
        return this;
    }


    /**
     * 去重(sql查询的关键字distinct)
     *
     * @return Query
     */
    public WeekEndQuery<T> distinct() {
        condition.setDistinct(true);
        return this;
    }

    /**
     * 添加'等于'查询条件
     *
     * @param func  对应实体类的字段函数
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> andEqualTo(Func<T> func, Object value) {
        if (value == null) {
            log.warn("Method andEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andEqualTo(func, value);
        return this;
    }

    /**
     * 添加 or filed=value 的条件
     *
     * @param func  对应实体类的字段函数
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> orEqualTo(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orEqualTo(func, value);
        return this;
    }

    /**
     * 添加  ’and 不等于‘查询条件
     *
     * @param func  对应实体类的字段函数
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> andNotEqualTo(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andNotEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andNotEqualTo(func, value);
        return this;
    }

    /**
     * 添加  ’or 不等于‘查询条件
     *
     * @param func    对应实体类的字段函数
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> orNotEqualTo(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orNotEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orNotEqualTo(func, value);
        return this;
    }


    /**
     * 添加 ' and is null'查询条件
     *
     * @param fn 对应实体类的字段
     * @return Query
     */
    public WeekEndQuery<T> andIsNull(Func<T> fn) {
        criteria.andIsNull(fn);
        return this;
    }

    /**
     * 添加' or is null'查询条件
     *
     * @param fn 对应实体类的字段
     * @return Query
     */
    public WeekEndQuery<T> orIsNull(Func<T> fn) {
        criteria.orIsNull(fn);
        return this;
    }

    /**
     * 添加'and is not null'查询条件
     *
     * @param fn 对应实体类的字段函数
     * @return Query
     */
    public WeekEndQuery<T> andIsNotNull(Func<T> fn) {
        criteria.andIsNotNull(fn);
        return this;
    }


    /**
     * 添加'or is not null'查询条件
     *
     * @param fn 对应实体类的字段函数
     * @return Query
     */
    public WeekEndQuery<T> orIsNotNull(Func<T> fn) {
        criteria.orIsNotNull(fn);
        return this;
    }

    /**
     * 添加'between A and B'条件
     *
     * @param func    对应实体类的字段函数
     * @param left  左边的值
     * @param right 右边的值
     * @return Query
     */
    public WeekEndQuery<T> andBetween(Func<T> func, Object left, Object right) {
        if (Objects.isNull(left) || Objects.isNull(right)) {
            log.warn("Method andBetween param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andBetween(func, left, right);
        return this;
    }

    /**
     * 添加 or between条件
     *
     * @param func  对应实体类的字段函数
     * @param left  左区间
     * @param right 右区间
     * @return Query
     */
    public WeekEndQuery<T> orBetween(Func<T> func, Object left, Object right) {
        if (Objects.isNull(left) || Objects.isNull(right)) {
            log.warn("Method orBetween param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orBetween(func, left, right);
        return this;
    }

    /**
     * 正则表达式
     *
     * @param func     字段名
     * @param regexp 正则规则
     * @return Query
     */
    public WeekEndQuery<T> andRegexp(Func<T> func, String regexp) {
        if (StringUtils.isEmpty(regexp)) {
            log.warn("Method andRegexp param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        String column = condition.column(Reflections.fnToFieldName(func));
        criteria.andCondition(column + " REGEXP " + regexp);
        return this;
    }


    /**
     * 添加'and like'查询条件
     *
     * @param func    对应实体类的字段函数
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> andLike(Func<T> func, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method andLike param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andLike(func, value);
        return this;
    }


    /**
     * 添加'or like'查询条件
     *
     * @param func    对应实体类的字段函数
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> orLike(Func<T> func, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method orLike param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orLike(func, value);
        return this;
    }

    /**
     * 添加'and in'查询条件
     *
     * @param func    对应实体类的字段函数
     * @param value 值的集合
     * @return Query
     */
    public WeekEndQuery<T> andIn(Func<T> func, Iterable<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            log.warn("Method andIn param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andIn(func, value);
        return this;
    }

    /**
     * 添加'and in'查询条件
     *
     * @param func    对应实体类的字段函数
     * @param value 值的字符串
     * @return Query
     */
    public WeekEndQuery<T> andIn(Func<T> func, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method andIn param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.andIn(func, Collections.singletonList(data));
        return this;
    }

    /**
     * 添加'and not in'查询条件
     *
     * @param func    对应实体类的字段函数
     * @param value 值的字符串
     * @return Query
     */
    public WeekEndQuery<T> andNotIn(Func<T> func, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method andNotIn param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.andNotIn(func, Collections.singletonList(data));
        return this;
    }

    /**
     * 添加' and not in'查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值的集合
     * @return Query
     */
    public WeekEndQuery<T> andNotIn(Func<T> func, Iterable<?> value) {
        criteria.andNotIn(func, value);
        return this;
    }


    /**
     * 添加'or in'查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值的集合
     * @return Query
     */
    public WeekEndQuery<T> orIn(Func<T> func, Iterable<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            log.warn("Method orIn param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orIn(func, value);
        return this;
    }

    /**
     * 添加'or in'查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值的字符串
     * @return WeekEndQuery
     */
    public WeekEndQuery<T> orIn(Func<T> func, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method orIn param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.orIn(func, Collections.singletonList(data));
        return this;
    }

    /**
     * 添加'or not in'查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值的字符串
     * @return Query
     */
    public WeekEndQuery<T> orNotIn(Func<T> func, String value) {
        if (StringUtils.isEmpty(value)) {
            log.warn("Method orNotIn param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        String[] data = value.split(StringUtils.REG_EN_COMMA);
        criteria.orNotIn(func, Collections.singletonList(data));
        return this;
    }

    /**
     * 添加'or not in'查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值的集合
     * @return Query
     */
    public WeekEndQuery<T> orNotIn(Func<T> func, Iterable<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            log.warn("Method orNotIn param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orNotIn(func, value);
        return this;
    }

    /**
     * 添加' and > '查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> andGreaterThan(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andGreaterThan param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andGreaterThan(func, value);
        return this;
    }


    /**
     * 添加' or > '查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> orGreaterThan(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orGreaterThan param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orGreaterThan(func, value);
        return this;
    }

    /**
     * 添加'and >= '查询条件
     *
     * @param func    对应实体类的字段名
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> andGreaterThanOrEqualTo(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andGreaterThanOrEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andGreaterThanOrEqualTo(func, value);
        return this;
    }

    /**
     * 添加'or >= '查询条件
     *
     * @param func    对应实体类的字段名
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> orGreaterThanOrEqualTo(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orGreaterThanOrEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orGreaterThanOrEqualTo(func, value);
        return this;
    }

    /**
     * 添加'and <= '查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> andLessThanOrEqualTo(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andLessThanOrEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andLessThanOrEqualTo(func, value);
        return this;
    }

    /**
     * 添加'or <= '查询条件
     *
     * @param func    对应实体类的字段
     * @param value 值
     * @return Query
     */
    public WeekEndQuery<T> orLessThanOrEqualTo(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orLessThanOrEqualTo param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orLessThanOrEqualTo(func, value);
        return this;
    }

    /**
     * 添加' and < '查询条件
     *
     * @param func 对应实体类的字段
     * @param value     值
     * @return Query
     */
    public WeekEndQuery<T> andLessThan(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method andLessThan param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.andLessThan(func, value);
        return this;
    }


    /**
     * 添加' or < '查询条件
     *
     * @param func 对应实体类的字段
     * @param value     值
     * @return Query
     */
    public WeekEndQuery<T> orLessThan(Func<T> func, Object value) {
        if (Objects.isNull(value)) {
            log.warn("Method orLessThan param {} is null! Sql not exec!",Reflections.fnToFieldName(func));
            haveNull = true;
        }
        criteria.orLessThan(func, value);
        return this;
    }

    /**
     * 添加'and ()'查询条件
     *
     * @param query 查询条件
     * @return Query
     */
    public WeekEndQuery<T> and(WeekEndQuery<T> query) {
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
    public WeekEndQuery<T> or(WeekEndQuery<T> query) {
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
    public WeekEndQuery<T> groupByAggregateFunc(List<AggregateInfo> aggregateInfos) {
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
    @SafeVarargs
    public final WeekEndQuery<T> groupByProperties(Func<T>... groupByProperties) {
        this.useGroup = true;
        condition.getGroupByCondition().groupBy(columnArray(Arrays.asList(groupByProperties)));
        return this;
    }


    /**
     * 子模块 oneToMany、ToOne
     * 支持下级关联格式
     *
     * @param names 子模块名称
     * @return Query
     */
    public WeekEndQuery<T> sonModel(String... names) {
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
