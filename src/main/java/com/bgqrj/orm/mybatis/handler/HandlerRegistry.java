package com.bgqrj.orm.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;


/**
 * 描述: type handler处理类
 *
 * @author yangxin
 * 日期: 2020/9/21
 */
public class HandlerRegistry {

    private HandlerRegistry() {

    }

    private static final org.apache.ibatis.session.Configuration MYBATIS_CONFIGURATION;
    private static final TypeHandlerRegistry TYPE_HANDLER_REGISTRY;

    static {
        MYBATIS_CONFIGURATION = new org.apache.ibatis.session.Configuration();
        TYPE_HANDLER_REGISTRY = MYBATIS_CONFIGURATION.getTypeHandlerRegistry();
    }

    /**
     * 注册单个对象的type handler映射
     *
     * @param clazz 对象泛型class
     * @param <T>   泛型
     * @return Configuration
     */
    public static <T> TypeHandlerRegistry registryObject(Class<T> clazz) {
        //注册类对应的TypeHandler
        TYPE_HANDLER_REGISTRY.register(clazz,
                JdbcType.LONGNVARCHAR,
                new JsonObjectTypeHandler<>(clazz));
        return TYPE_HANDLER_REGISTRY;
    }


    /**
     * 返回mybatis配置对象
     *
     * @return Configuration
     */
    public static org.apache.ibatis.session.Configuration getConfiguration() {
        return MYBATIS_CONFIGURATION;
    }


}
