package com.bgqrj.mybatis.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

import com.bgqrj.mybatis.demo.constant.DataBaseConstant;
import com.github.bgqrjf.mybatis.handler.HandlerRegistry;

/**
 * 主库Mybatis配置
 *
 * @author yx
 */
@Configuration
@MapperScan(basePackages = MasterDataBaseMybatisConfig.MAPPER_DAO_LOCATION, sqlSessionFactoryRef = DataBaseConstant.MASTER_SQL_SESSION_FACTORY_NAME)
public class MasterDataBaseMybatisConfig {
    private static final String MODEL_PACKAGE = "com.bgqrj.mybatis.demo.*.entity";
    private static final String MAPPER_XML_LOCATION = "classpath*:mapper/*.xml";
    static final String MAPPER_DAO_LOCATION = "com.bgqrj.mybatis.demo.dao.mapper";

    @ConfigurationProperties(prefix = "master.druid")
    @Bean(name = DataBaseConstant.MASTER_DATA_SOURCE_NAME, destroyMethod = "close", initMethod = "init")
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }


    @Bean(DataBaseConstant.MASTER_SQL_SESSION_FACTORY_NAME)
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier(DataBaseConstant.MASTER_DATA_SOURCE_NAME) DataSource masterDataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(masterDataSource);
        factory.setTypeAliasesPackage(MODEL_PACKAGE);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources(MAPPER_XML_LOCATION));
        factory.setConfiguration(registryTypeHandler());
        return factory.getObject();

    }

    /**
     * 配置需要扫描的接口文件的位置
     *
     * @return mapper
     */
    @Bean(name = "masterMapperScannerConfigurer")
    public MapperScannerConfigurer masterMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(DataBaseConstant.MASTER_SQL_SESSION_FACTORY_NAME);
        mapperScannerConfigurer.setBasePackage(MAPPER_DAO_LOCATION);

        //配置通用Mapper，详情请查阅官方文档
        Properties properties = new Properties();
        //Mapper插件基础接口的完全限定名
        properties.setProperty("mappers", "com.github.bgqrjf.mybatis.query.mapper.Mapper");
        //insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
        properties.setProperty("notEmpty", "true");
        properties.setProperty("IDENTITY", "MYSQL");
        mapperScannerConfigurer.setProperties(properties);

        return mapperScannerConfigurer;
    }

    /**
     * 实现接口方法，返回事务数据库管理器
     *
     * @return transactionManager
     */
    @Bean(name = DataBaseConstant.MASTER_TRANSACTION_MANAGER)
    public DataSourceTransactionManager masterTransactionManager(@Qualifier(DataBaseConstant.MASTER_DATA_SOURCE_NAME) DruidDataSource druidDataSource) {
        return new DataSourceTransactionManager(druidDataSource);
    }

    @Bean(name = DataBaseConstant.MASTER_SQL_SESSION_TEMPLATE_MASTER)
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier(DataBaseConstant.MASTER_SQL_SESSION_FACTORY_NAME) SqlSessionFactory masterSqlSessionFactory) {
        return new SqlSessionTemplate(masterSqlSessionFactory);
    }


    /**
     * 设置mybatis TypeHandler
     *
     * @return Configuration
     */
    private org.apache.ibatis.session.Configuration registryTypeHandler() {

        return HandlerRegistry.getConfiguration();
    }
}
