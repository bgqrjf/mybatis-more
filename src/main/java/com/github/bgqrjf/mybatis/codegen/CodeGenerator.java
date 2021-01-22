package com.github.bgqrjf.mybatis.codegen;

import com.github.bgqrjf.mybatis.exception.CodeGenException;
import com.google.common.base.CaseFormat;
import com.github.bgqrjf.mybatis.utils.FieldUtils;
import com.github.bgqrjf.mybatis.utils.StringUtils;

import freemarker.template.TemplateExceptionHandler;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 代码生成器，根据数据表名称生成对应的Model、Mapper、BaseService、Controller简化开发。
 *
 * @author yangxin
 */
public class CodeGenerator {
    private static final Logger log = LoggerFactory.getLogger(CodeGenerator.class);
    /**
     * 生成表的MVC层级全部文件
     */
    public static final String CREATE_LEVEL_OF_ALL = "ALL";
    /**
     * 只生成表service层级结构
     */
    public static final String CREATE_LEVEL_OF_SERVICE = "SERVICE";
    /**
     * 生成表dao+entity层级结构
     */
    public static final String CREATE_LEVEL_OF_DAO = "DAO";
    /**
     * 只生成controller层级
     */
    public static final String CREATE_LEVEL_OF_CONTROLLER = "CONTROLLER";

    /**
     * 项目基础包名称
     */
    private String basePackage;
    /**
     * Model所在包
     */
    private String modelPackage;
    /**
     * Mapper所在包
     */
    private String mapperPackage;
    /**
     * Service所在包
     */
    private String servicePackage;

    private String constantTablePackage;
    /**
     * ServiceImpl所在包
     */
    private String serviceImplPackage;
    /**
     * Controller所在包
     */
    private String controllerPackage;
    /**
     * 实体的基类
     */
    private String rootClass;
    /**
     * 资源文件路径
     */
    private String mapperTargetPackage = "/mapper/";

    private String mapperInputFileName;

    /**
     * JDBC配置
     */
    private String jdbcUrl;
    private String jdbcUserName;
    private String jdbcPassword;
    private String jdbcDriverClassName;
    private String realProjectFilePath;
    /**
     * 是否覆盖 mapper class
     */
    private boolean coverMapperJava = false;

    /**
     * 项目在硬盘上的基础路径
     */
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    /**
     * mybatis资源目录名
     */
    private static final String RESOURCES_FOLDER = "mybatis";
    /**
     * 资源文件路径
     */
    private static final String RESOURCES_PATH = "/src/main/resources";

    /**
     * 模板位置
     */
    private static final String TEMPLATE_FILE_PATH =
            CodeGenerator.class.getClassLoader().getResource(RESOURCES_FOLDER + "/template").getPath();
    /**
     * java文件路径
     */
    private static final String JAVA_PATH = "/src/main/java";
    /**
     * 生成文件的author
     */
    private static final String AUTHOR = "BGQRJF-MYBATIS";
    /**
     * 文件的 @date
     */
    private static final String DATE = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    /**
     * Mapper插件基础接口的完全限定名
     */
    private static final String MAPPER_INTERFACE_REFERENCE = "com.github.bgqrjf.mybatis.query.mapper.Mapper";

    public CodeGenerator() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        InputStream propInputStream = this.getClass().getClassLoader().getResourceAsStream(RESOURCES_FOLDER +
                "/MybatisGen.properties");
        Properties prop = new Properties();
        try {
            prop.load(Objects.requireNonNull(propInputStream));
        } catch (IOException e1) {
            throw new CodeGenException("mybatis文件获取失败!", e1);
        }

        String rootPackage = prop.getProperty(ConfProperties.CONF_ROOT_PACKAGE);
        if (StringUtils.isEmpty(rootPackage)) {
            throw new CodeGenException("项目根目录配置不能为空");
        }

        String jdbcConUrl = prop.getProperty(ConfProperties.CONF_DB_JDBC_URL);
        if (StringUtils.isNotEmpty(jdbcConUrl) && !jdbcConUrl.contains("nullCatalogMeansCurrent")) {
            jdbcConUrl += jdbcConUrl.contains("&") ? "&nullCatalogMeansCurrent=true" : "nullCatalogMeansCurrent=true";
        }
        this.jdbcUrl = jdbcConUrl;
        this.jdbcUserName = prop.getProperty(ConfProperties.CONF_DB_JDBC_USER_NAME);
        this.jdbcPassword = prop.getProperty(ConfProperties.CONF_DB_JDBC_PASSWORD);
        this.jdbcDriverClassName = prop.getProperty(ConfProperties.CONF_DB_JDBC_DRIVER_CLASS_NAME);
        this.basePackage = rootPackage;
        String entityPath = basePackage + ConfProperties.PACKAGE_MODEL;
        String entityPutFileNameKey = ConfProperties.CONF_ENTITY_PUT_FILE;
        if (prop.containsKey(entityPutFileNameKey)) {
            String entityPutFile = prop.getProperty(entityPutFileNameKey);
            if (StringUtils.isNotEmpty(entityPutFile)) {
                entityPath = entityPath + "." + entityPutFile;
            }
        }
        this.modelPackage = entityPath;
        this.mapperPackage = basePackage + ConfProperties.PACKAGE_MAPPER_CLASS;
        String mapperPutFileNameKey = ConfProperties.CONF_MAPPER_XML_PUT_FILE;
        if (prop.containsKey(mapperPutFileNameKey)) {
            String mapperXmlPath = prop.getProperty(mapperPutFileNameKey);
            if (!StringUtils.isBlank(mapperXmlPath)) {
                this.mapperInputFileName = mapperXmlPath;
                this.mapperPackage = this.mapperPackage + "." + this.mapperInputFileName;
                this.mapperTargetPackage = this.mapperTargetPackage + this.mapperInputFileName;
            }
        }
        String rootClassPathKey = ConfProperties.CONF_ENTITY_CLASS;
        if (prop.containsKey(rootClassPathKey)) {
            String rootPath = prop.getProperty(rootClassPathKey);
            if (StringUtils.isNotEmpty(rootPath)) {
                this.rootClass = rootPath;
            }
        }
        this.realProjectFilePath = PROJECT_PATH;
        String moduleNameKey = ConfProperties.CONF_MODULE_NAME;
        if (prop.containsKey(moduleNameKey)) {
            String moduleName = prop.getProperty(moduleNameKey);
            if (StringUtils.isNotEmpty(moduleName)) {
                this.realProjectFilePath = PROJECT_PATH + "/" + moduleName;
            }
        }
        String coverMapper = prop.getProperty(ConfProperties.CON_COVER_MAPPER_JAVA);
        if (StringUtils.isNotEmpty(coverMapper)) {
            this.coverMapperJava = Boolean.TRUE.equals(Boolean.valueOf(coverMapper));
        }
        this.servicePackage = basePackage + ConfProperties.PACKAGE_SERVICE;
        this.serviceImplPackage = servicePackage + ConfProperties.PACKAGE_SERVICE_IMPL;
        this.controllerPackage = basePackage + ConfProperties.PACKAGE_CONTROLLER;
        this.constantTablePackage = basePackage + ConfProperties.PACKAGE_TABLE_CONSTANT;

    }

    /**
     * 按照驼峰命名，生成数据库表列表的结构
     *
     * @param level      生成模板的范围
     * @param tableNames 表名
     */
    public void create(String level, String... tableNames) {
        if (tableNames == null || tableNames.length == 0) {
            throw new CodeGenException("表名不能为空");
        }
        genCode(level, tableNames);
    }

    /**
     * 按照驼峰命名或者指定名称，生成一个表的相关结构
     *
     * @param level     生成模板的范围
     * @param tableName 要生成的表名
     * @param modelName 生成java文件的前缀名
     */
    public void create(String level, String tableName, String modelName) {
        if (StringUtils.isEmpty(tableName) && StringUtils.isEmpty(modelName)) {
            throw new CodeGenException(String.format("表名不允许为空,tableNames:%s,modelName:%s", tableName, modelName));
        }
        genCodeForModelName(level, tableName, modelName);
    }

    /**
     * 通过数据表名称生成代码，Model 名称通过解析数据表名称获得，下划线转大驼峰的形式。
     * 如输入表名称 "t_user_detail" 将生成 TUserDetail、TUserDetailMapper、TUserDetailService ...
     *
     * @param tableNames 数据表名称...
     */

    private void genCode(String level, String... tableNames) {
        for (String tableName : tableNames) {
            genCodeByCustomModelName(tableName, null, level);
        }
    }

    private void genCodeForModelName(String level, String tableName, String modelName) {
        genCodeByCustomModelName(tableName, modelName, level);
    }

    /**
     * 通过数据表名称或自定义名称生成代码
     * 如果输入表名称 "user_order" 和自定义的名称 "Order", 则将生成 Order、OrderMapper、OrderService ...
     *
     * @param tableName 数据表名称
     * @param modelName 自定义名称
     */
    private void genCodeByCustomModelName(String tableName, String modelName, String level) {
        if (CREATE_LEVEL_OF_CONTROLLER.equals(level)) {
            genController(tableName, modelName);

        } else if (CREATE_LEVEL_OF_SERVICE.equals(level)) {
            genService(tableName, modelName);
        } else if (CREATE_LEVEL_OF_DAO.equals(level)) {
            genModelAndMapper(tableName, modelName);
        } else {
            genModelAndMapper(tableName, modelName);
            genService(tableName, modelName);
            genController(tableName, modelName);
        }
    }

    /**
     * 生成实体类和对应的Mapper.xml
     *
     * @param tableName 表名
     * @param modelName 自定义实体名
     */
    private void genModelAndMapper(String tableName, String modelName) {
        Context context = new Context(ModelType.FLAT);
        context.setId("Potato");
        context.setTargetRuntime("MyBatis3Simple");
        context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(jdbcUrl);
        jdbcConnectionConfiguration.setUserId(jdbcUserName);
        jdbcConnectionConfiguration.setPassword(jdbcPassword);
        jdbcConnectionConfiguration.setDriverClass(jdbcDriverClassName);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        //自定义model生成的插件，此处自定义使用lombok
        pluginConfiguration.setConfigurationType("com.github.bgqrjf.mybatis.plugin.PluginsExt");
        pluginConfiguration.addProperty("mappers", MAPPER_INTERFACE_REFERENCE);
        context.addPluginConfiguration(pluginConfiguration);

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetProject(realProjectFilePath + JAVA_PATH);
        javaModelGeneratorConfiguration.setTargetPackage(modelPackage);
        //配置生成实体的基类
        if (StringUtils.isNotEmpty(rootClass)) {
            javaModelGeneratorConfiguration.addProperty("rootClass", rootClass);
        } else {
            //带上序列化接口
            PluginConfiguration pluginConfiguration2 = new PluginConfiguration();
            pluginConfiguration2.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
            context.addPluginConfiguration(pluginConfiguration2);
        }
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(realProjectFilePath + RESOURCES_PATH);
        sqlMapGeneratorConfiguration.setTargetPackage(mapperTargetPackage);
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
        if (this.coverMapperJava) {
            JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
            javaClientGeneratorConfiguration.setTargetProject(realProjectFilePath + JAVA_PATH);
            javaClientGeneratorConfiguration.setTargetPackage(mapperPackage);
            javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
            context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
        }
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        if (!StringUtils.isEmpty(modelName)) {
            tableConfiguration.setDomainObjectName(modelName);
        }
        tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
        context.addTableConfiguration(tableConfiguration);

        List<String> warnings;
        MyBatisGenerator generator;
        try {
            Configuration config = new Configuration();
            config.addContext(context);
            config.validate();
            DefaultShellCallback callback = new DefaultShellCallback(true);
            warnings = new ArrayList<>();
            generator = new MyBatisGenerator(config, callback, warnings);
            generator.generate(null);
        } catch (Exception e) {
            throw new CodeGenException("生成Model和Mapper失败", e);
        }

        if (generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
            throw new CodeGenException("生成Model和Mapper失败：" + warnings);
        }
        if (StringUtils.isEmpty(modelName)) {
            modelName = nameConvertUpperCamel(tableName);
        }
        log.info("{}.java 生成成功", modelName);
        if (this.coverMapperJava) {
            log.info("{}Mapper.java 生成成功", modelName);
        }
        log.info("{}Mapper.xml 生成成功", modelName);
    }

    private void genService(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? nameConvertUpperCamel(tableName) : modelName;
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            String mapperPath;
            if (StringUtils.isEmpty(this.mapperInputFileName)) {
                mapperPath = modelNameUpperCamel;
            } else {
                mapperPath = this.mapperInputFileName + "." + modelNameUpperCamel;
            }
            data.put("mapperPath", mapperPath);

            data.put("modelNameLowerCamel", nameConvertLowerCamel(tableName));
            data.put("basePackage", basePackage);
            data.put("mapperPackage", mapperPackage);
            data.put("modelPackage", modelPackage);

            File file = new File(
                    realProjectFilePath + JAVA_PATH + packageConvertPath(servicePackage) + modelNameUpperCamel
                            + "Service.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("service.ftl").process(data,
                    new FileWriter(file));
            log.info("{}Service.java 生成成功", modelNameUpperCamel);

            File file1 = new File(
                    realProjectFilePath + JAVA_PATH + packageConvertPath(serviceImplPackage) + modelNameUpperCamel
                            + "ServiceImpl.java");
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            cfg.getTemplate("service-impl.ftl").process(data,
                    new FileWriter(file1));
            log.info("{}ServiceImpl.java 生成成功", modelNameUpperCamel);
        } catch (Exception e) {
            throw new CodeGenException("生成Service失败", e);
        }
    }

    private void genController(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? nameConvertUpperCamel(tableName) : modelName;
            data.put("baseRequestMapping", modelNameConvertMappingPath(modelNameUpperCamel));
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
            data.put("basePackage", basePackage);
            data.put("modelPackage", modelPackage);
            String path =
                    realProjectFilePath + JAVA_PATH + packageConvertPath(controllerPackage) + modelNameUpperCamel
                            + "Controller.java";
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("controller.ftl")
                    .process(data, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),
                            StandardCharsets.UTF_8)));

            log.info("{}Controller.java 生成成功", modelNameUpperCamel);
        } catch (Exception e) {
            throw new CodeGenException("生成Controller失败", e);
        }

    }

    /**
     * 生成表的实体字段常量
     *
     * @param clazz 实体class
     */
    public void createTableConstant(Class clazz) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelName = clazz.getSimpleName();
            data.put("tableName", CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName).toLowerCase());
            data.put("className", modelName + "TableConstant");
            data.put("basePackage", basePackage);
            data.put("modelPackage", modelPackage);
            data.put("propertyNameList", tableEntityConstant(clazz));
            File file = new File(realProjectFilePath + JAVA_PATH + packageConvertPath(constantTablePackage) + modelName
                    + "TableConstant.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("table-constant.ftl").process(data, new FileWriter(file));

            log.info("{}TableConstant.java 生成成功", modelName);
        } catch (Exception e) {
            throw new CodeGenException("生成TableConstant失败", e);
        }

    }

    private List<Map<String, String>> tableEntityConstant(Class clazz) {
        List<Map<String, String>> constantFieldMap = new ArrayList<>();
        Map<String, Field> fieldMap = FieldUtils.getFieldMap(clazz);
        fieldMap.forEach(((fieldName, field) -> {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            Transient aTransient = field.getAnnotation(Transient.class);
            if (!isStatic && !isFinal && aTransient == null) {
                Map<String, String> map = new HashMap<>(2);
                map.put("upperUnderscoreName",
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName).toUpperCase());
                map.put("name", fieldName);
                constantFieldMap.add(map);
            }
        }));
        return constantFieldMap;
    }

    private freemarker.template.Configuration getConfiguration() throws IOException {
        freemarker.template.Configuration cfg =
                new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
        cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_PATH));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return cfg;
    }

    private String nameConvertLowerCamel(String name) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name.toLowerCase());
    }

    private String nameConvertUpperCamel(String name) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.toLowerCase());

    }

    private String tableNameConvertMappingPath(String tableName) {
        //兼容使用大写的表名
        tableName = tableName.toLowerCase();
        return "/" + (tableName.contains(StringUtils.REG_UNDERLINE) ? tableName
                .replaceAll(StringUtils.REG_UNDERLINE, "/") : tableName);
    }

    private String modelNameConvertMappingPath(String modelName) {
        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
        return tableNameConvertMappingPath(tableName);
    }

    private String packageConvertPath(String packageName) {
        return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
    }

}
