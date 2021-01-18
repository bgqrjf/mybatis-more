package com.github.bgqrjf.mybatis.annotations;


import com.github.bgqrjf.mybatis.query.mapper.Mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一对一注解
 *
 * @author yangxin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ToOne {
    /**
     * 子model 名称
     */
    Class<? extends Mapper> sonMapper();

    /**
     * 当前model的key值为子model的主键值
     */
    String sonKey() default "";

    /**
     * 子model的key值为当前实体主键id
     */
    String masterKey() default "";
}
