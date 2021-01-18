package com.github.bgqrjf.mybatis.codegen;

/**
 * MBG生成可配置的参数及包名配置
 *
 * @author yangxin
 */
public class ConfProperties {

    private ConfProperties() {
    }

    /*
     * 项目基本包默认生成路径及名称
     */
    /**
     * 生成实体默认的 包名及路径: ${bq-mybatis.conf.root-package}.entity
     */
    public static final String PACKAGE_MODEL = ".entity";
    /**
     * 生成mapper接口默认的 包名及路径:${bq-mybatis.conf.root-package}.dao.mapper
     */
    public static final String PACKAGE_MAPPER_CLASS = ".dao.mapper";
    /**
     * 生成service接口默认的 包名及路径:${bq-mybatis.conf.root-package}.service
     */
    public static final String PACKAGE_SERVICE = ".service";
    /**
     * 生成ServiceImpl接口默认的 包名及路径:${bq-mybatis.conf.root-package}.service.impl
     */
    public static final String PACKAGE_SERVICE_IMPL = ".impl";
    /**
     * 生成controller接口默认的 包名及路径:${bq-mybatis.conf.root-package}.controller
     */
    public static final String PACKAGE_CONTROLLER = ".controller";
    /**
     * 生成表字段常量类默认的 包名及路径:${bq-mybatis.conf.root-package}.constant.table
     */
    public static final String PACKAGE_TABLE_CONSTANT = ".constant.table";

    /*
     * 可配置项
     */
    /**
     * 待生成的文件所在的包基础结构，如com.xx.a
     */
    public static final String CONF_ROOT_PACKAGE = "bq.mybatis.conf.root-package";
    /**
     * 生生成的实体存放文件夹 默认位置为${bq-mybatis.conf.root-package}.entity,有值则在entity文件夹内增加层级
     */
    public static final String CONF_ENTITY_PUT_FILE = "bq.mybatis.conf.entity.put-file-name";
    /**
     * 生成的xml文件放在 resource文件夹下mapper文件夹下的${bq-mybatis.conf.mapper.put-file-name}文件夹内.默认放在 resource/mapper/下;
     * 有值则resource/mapper/文件夹内创建对应文件夹
     */
    public static final String CONF_MAPPER_XML_PUT_FILE = "bq.mybatis.conf.mapper.put-file-name";
    /**
     * 生成实体集成的基类class路径
     */
    public static final String CONF_ENTITY_CLASS = "bq.mybatis.conf.root-class";
    /**
     * 生成代码所在的项目模块名(module)
     */
    public static final String CONF_MODULE_NAME = "bq.mybatis.conf.module-name";
    /**
     * 重新生成该表相关文件,是否覆盖mapper.java 。默认不覆盖
     */
    public static final String CON_COVER_MAPPER_JAVA = "bq.mybatis.conf.cover-mapper-java";

    /**
     * jdbc url
     */
    public static final String CONF_DB_JDBC_URL = "bq.mybatis.db.url";
    /**
     * jdbc 用户名
     */
    public static final String CONF_DB_JDBC_USER_NAME = "bq.mybatis.db.username";
    /**
     * jdbc 用户密码
     */
    public static final String CONF_DB_JDBC_PASSWORD = "bq.mybatis.db.password";
    /**
     * jdbc driver-class-name
     */
    public static final String CONF_DB_JDBC_DRIVER_CLASS_NAME = "bq.mybatis.db.driver-class-name";

}
