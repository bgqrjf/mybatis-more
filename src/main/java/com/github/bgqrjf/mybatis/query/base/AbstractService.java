package com.github.bgqrjf.mybatis.query.base;


import com.github.bgqrjf.mybatis.query.mapper.Mapper;
import com.github.bgqrjf.mybatis.query.weekend.WeekEndQuery;
import com.github.bgqrjf.mybatis.utils.StringUtils;
import com.github.bgqrjf.mybatis.query.Query;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * 基于通用MyBatis Mapper插件的Service接口的实现
 *
 * @author yangxin
 */
public abstract class AbstractService<T> implements BaseService<T> {

    @Autowired
    protected Mapper<T> mapper;

    private Class<T> modelClass;

    public AbstractService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public int save(T model) {
        return mapper.insert(model);
    }

    @Override
    public int save(List<T> models) {
        return mapper.insertList(models);
    }

    @Override
    public int saveSelective(T model) {
        return mapper.insertSelective(model);
    }

    @Override
    public int deleteById(Object pkId) {
        return mapper.deleteByPrimaryKey(pkId);
    }

    @Override
    public int deleteByIds(String pkIds) {
        return mapper.deleteByIds(pkIds);
    }

    @Override
    public  int deleteByIds(Collection<?> pkIds){
        return mapper.deleteByIds(StringUtils.join(pkIds));
    }

    @Override
    public int update(T model) {
        return mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public int updateSelective(T model) {
        return mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public int updateByCondition(T model, Query<T> query) {
        return mapper.updateByCondition(model, query.getCondition());
    }
    @Override
    public int updateByCondition(T model, WeekEndQuery<T> query) {
        return mapper.updateByCondition(model, query.getCondition());
    }

    @Override
    public int updateByConditionSelective(T model, WeekEndQuery<T> query) {
        return mapper.updateByConditionSelective(model, query.getCondition());
    }
    @Override
    public int updateByConditionSelective(T model, Query<T> query) {
        return mapper.updateByConditionSelective(model, query.getCondition());
    }

    @Override
    public T selectOne(Object pkId) {
        return mapper.selectByPrimaryKey(pkId);
    }

    @Override
    public <F> List<T> selectByIds(Collection<F> pkIds) {
        String id = StringUtils.join(pkIds);
        return mapper.selectByIds(id);
    }

    @Override
    public List<T> selectAll() {
        return mapper.selectAll();
    }

    @Override
    public Query<T> createQuery() {
        return new Query<>(mapper, modelClass);
    }

    @Override
    public Query<T> createQuery(SqlSessionTemplate sqlSessionTemplate) {
        return new Query<>(mapper, modelClass, sqlSessionTemplate);
    }

    @Override
    public WeekEndQuery<T> createWeekEndQuery() {
        return new WeekEndQuery<>(mapper, modelClass);
    }

    @Override
    public WeekEndQuery<T> createWeekEndQuery(SqlSessionTemplate sqlSessionTemplate) {
        return new WeekEndQuery<>(mapper, modelClass, sqlSessionTemplate);
    }


}
