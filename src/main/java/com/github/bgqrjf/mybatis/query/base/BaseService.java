package com.github.bgqrjf.mybatis.query.base;

import com.github.bgqrjf.mybatis.query.weekend.WeekEndQuery;
import com.github.bgqrjf.mybatis.query.Query;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.Collection;
import java.util.List;

/**
 * BaseService 层 基础接口，其他Service 接口 请继承该接口
 *
 * @author yangxin
 */
public interface BaseService<T> {
    /**
     * 保存一个实体，null的属性也会保存，不会使用数据库默认值
     *
     * @param model 数据实体
     * @return 成功数量
     */
    int save(T model);

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     *
     * @param model 数据实体
     * @return 成功数量
     */
    int saveSelective(T model);


    /**
     * 批量插入实体
     *
     * @param models 实体列表
     * @return 成功数量
     */
    int save(List<T> models);

    /**
     * 通过主鍵刪除一条记录
     *
     * @param pk 主键
     * @return 成功数量
     */
    int deleteById(Object pk);

    /**
     * 根据主键批量删除实体
     *
     * @param pkIds pkIds -> “1,2,3,4”
     * @return 成功数量
     */
    int deleteByIds(String pkIds);

    /**
     * 根据主键批量删除实体
     *
     * @param pkIds pkIds集合
     * @return 成功数量
     */
    int deleteByIds(Collection<?> pkIds);


    /**
     * 根据主键更新实体全部字段，null值会被更新
     *
     * @param model 包含主键的实体对象
     * @return 修改的数量
     */
    int update(T model);

    /**
     * 根据条件更新实体包含的不是null的属性值
     *
     * @param model 包含主键的实体对象
     * @return 修改的数量
     */
    int updateSelective(T model);

    /**
     * 根据条件更新实体包含的全部属性，null值会被更新
     *
     * @param model 设置要更新的值的实体
     * @param query 设置where条件
     * @return 修改的数量
     */
    int updateByCondition(T model, Query<T> query);

    /**
     * 根据条件更新实体包含的全部属性，null值会被更新
     *
     * @param model 设置要更新的值的实体
     * @param query 设置where条件
     * @return 修改的数量
     */
    int updateByCondition(T model, WeekEndQuery<T> query);

    /**
     * 根据条件更新实体包含的非null属性
     *
     * @param model 设置要更新的值的实体
     * @param query 设置where条件
     * @return 修改的数量
     */
    int updateByConditionSelective(T model, Query<T> query);

    /**
     * 根据条件更新实体包含的非null属性
     *
     * @param model 设置要更新的值的实体
     * @param query 设置where条件
     * @return 修改的数量
     */
    int updateByConditionSelective(T model, WeekEndQuery<T> query);


     /**
     * 根据主键查询实体
     *
     * @param pkId 主键
     * @return 实体对象
     */
    T selectOne(Object pkId);

    /**
     * 根据主键集合批量查询实体
     *
     * @param pkIds 主键集合
     * @param <F>   类型
     * @return 实体列表
     */
    <F> List<T> selectByIds(Collection<F> pkIds);

    /**
     * 创建一个查询对象(不支持子查询)
     *
     * @return Query
     */
    Query<T> createQuery();

    /**
     * 创建一个查询对象(支持子查询)
     *
     * @param sqlSessionTemplate 实体所在数据源的sqlSessionTemplate
     * @return Query
     */
    Query<T> createQuery(SqlSessionTemplate sqlSessionTemplate);

    /**
     * 查询全部列表
     *
     * @return 实体列表
     */
    List<T> selectAll();

    /**
     * 创建一个函数查询对象(不支持子查询)
     *
     * @return Query
     */
    WeekEndQuery<T> createWeekEndQuery();

    /**
     * 创建一个函数查询对象(支持子查询)
     *
     * @param sqlSessionTemplate 实体所在数据源的sqlSessionTemplate
     * @return Query
     */
    WeekEndQuery<T> createWeekEndQuery(SqlSessionTemplate sqlSessionTemplate);

}
