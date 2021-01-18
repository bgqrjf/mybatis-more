package com.github.bgqrjf.mybatis.annotations;


import com.github.bgqrjf.mybatis.query.mapper.Mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一对多注解
 *
 * @author yangxin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
    /**
     * 子model 名称
     */
    Class<? extends Mapper> sonMapper();

    /**
     * 子model下，当前关联当前model的key
     */
    String masterKey();

}
