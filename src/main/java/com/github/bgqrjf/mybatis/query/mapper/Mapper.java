package com.github.bgqrjf.mybatis.query.mapper;


import com.github.bgqrjf.mybatis.query.group.GroupByMapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * 通用mapper
 * @author yangxin
 */
public interface Mapper<T> extends BaseMapper<T>, ConditionMapper<T>, IdsMapper<T>, InsertListMapper<T>, GroupByMapper<T> {
}
