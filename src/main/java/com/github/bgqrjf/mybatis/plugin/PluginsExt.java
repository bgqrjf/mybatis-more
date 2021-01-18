package com.github.bgqrjf.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;
import tk.mybatis.mapper.generator.FalseMethodPlugin;
import tk.mybatis.mapper.generator.MapperCommentGenerator;

import java.util.*;

/**
 * 生成实体模板插件
 * 2019/9/12
 *
 * @author yx
 */
public class PluginsExt extends FalseMethodPlugin {

     @Override
     public boolean validate(List<String> warnings) {
          return true;
     }

     private Set<String> mappers = new HashSet<>();
     private boolean caseSensitive = false;
     private boolean useMapperCommentGenerator = true;
     private String beginningDelimiter = "";
     private String endingDelimiter = "";
     private String schema;
     private CommentGeneratorConfiguration commentCfg;
     private boolean forceAnnotation = false;
     private boolean needsData = false;
     private boolean needsGetter = true;
     private boolean needsSetter = true;
     private boolean needsToString = false;
     private boolean needsAccessors = false;
     private boolean needsEqualsAndHashCode = false;
     private boolean generateColumnConsts = false;
     private boolean generateDefaultInstanceMethod = false;
     private boolean needsSwagger = false;

     private String getDelimiterName(String name) {
          StringBuilder nameBuilder = new StringBuilder();
          if (StringUtility.stringHasValue(this.schema)) {
               nameBuilder.append(this.schema);
               nameBuilder.append(".");
          }

          nameBuilder.append(this.beginningDelimiter);
          nameBuilder.append(name);
          nameBuilder.append(this.endingDelimiter);
          return nameBuilder.toString();
     }

     @Override
     public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
          FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

          for (Object mapper : this.mappers) {
               interfaze.addImportedType(new FullyQualifiedJavaType(mapper.toString()));
               interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
          }

          interfaze.addImportedType(entityType);
          return true;
     }

     private void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
          topLevelClass.addImportedType("javax.persistence.*");
          if (this.needsData) {
               topLevelClass.addImportedType("lombok.Data");
               topLevelClass.addAnnotation("@Data");
          }

          if (this.needsGetter) {
               topLevelClass.addImportedType("lombok.Getter");
               topLevelClass.addAnnotation("@Getter");
          }

          if (this.needsSetter) {
               topLevelClass.addImportedType("lombok.Setter");
               topLevelClass.addAnnotation("@Setter");
          }

          if (this.needsToString) {
               topLevelClass.addImportedType("lombok.ToString");
               topLevelClass.addAnnotation("@ToString");
          }

          if (this.needsAccessors) {
               topLevelClass.addImportedType("lombok.experimental.Accessors");
               topLevelClass.addAnnotation("@Accessors(chain = true)");
          }

          if (this.needsEqualsAndHashCode) {
               topLevelClass.addImportedType("lombok.EqualsAndHashCode");
               topLevelClass.addAnnotation("@EqualsAndHashCode");
          }

          String tableName;
          if (this.needsSwagger) {
               topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
               topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
               tableName = introspectedTable.getRemarks();
               if (tableName == null) {
                    tableName = "";
               }

               topLevelClass.addAnnotation("@ApiModel(\"" + tableName.replaceAll("\r", "").replaceAll("\n", "") + "\")");
          }

          tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
          if (StringUtility.stringContainsSpace(tableName)) {
               tableName = this.context.getBeginningDelimiter() + tableName + this.context.getEndingDelimiter();
          }

          if (this.caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
               topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
          } else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
               topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
          } else if (!StringUtility.stringHasValue(this.schema) && !StringUtility.stringHasValue(this.beginningDelimiter) && !StringUtility.stringHasValue(this.endingDelimiter)) {
               if (this.forceAnnotation) {
                    topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
               }
          } else {
               topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
          }

          if (this.generateColumnConsts) {

               List<IntrospectedColumn> introspectedColumnList = introspectedTable.getAllColumns();
               for (IntrospectedColumn introspectedColumn : introspectedColumnList) {
                    Field field = new Field();
                    field.setVisibility(JavaVisibility.PUBLIC);
                    field.setStatic(true);
                    field.setFinal(true);
                    field.setName(introspectedColumn.getActualColumnName().toUpperCase());
                    field.setType(new FullyQualifiedJavaType(String.class.getName()));
                    field.setInitializationString("\"" + introspectedColumn.getJavaProperty() + "\"");
                    this.context.getCommentGenerator().addClassComment(topLevelClass, introspectedTable);
                    topLevelClass.addField(field);
                    Field columnField = new Field();
                    columnField.setVisibility(JavaVisibility.PUBLIC);
                    columnField.setStatic(true);
                    columnField.setFinal(true);
                    columnField.setName("DB_" + introspectedColumn.getActualColumnName().toUpperCase());
                    columnField.setType(new FullyQualifiedJavaType(String.class.getName()));
                    columnField.setInitializationString("\"" + introspectedColumn.getActualColumnName() + "\"");
                    topLevelClass.addField(columnField);
               }
          }

          if (this.generateDefaultInstanceMethod) {
               Method defaultMethod = new Method();
               defaultMethod.setStatic(true);
               defaultMethod.setName("defaultInstance");
               defaultMethod.setVisibility(JavaVisibility.PUBLIC);
               defaultMethod.setReturnType(topLevelClass.getType());
               defaultMethod.addBodyLine(String.format("%s instance = new %s();", topLevelClass.getType().getShortName(), topLevelClass.getType().getShortName()));
               List<IntrospectedColumn> introspectedColumnList = introspectedTable.getAllColumns();
               for (IntrospectedColumn introspectedColumn : introspectedColumnList) {
                    String shortName = introspectedColumn.getFullyQualifiedJavaType().getShortName();
                    List<String> supportType = Arrays.asList("Byte", "Short", "Character", "Integer", "Long", "Float", "Double", "String", "BigDecimal", "BigInteger");
                    if (supportType.contains(shortName) && introspectedColumn.getDefaultValue() != null) {
                         String defaultValue = introspectedColumn.getDefaultValue();
                         if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                              if (defaultValue.length() == 2) {
                                   defaultValue = "";
                              } else {
                                   defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                              }
                         }

                         if ("Boolean".equals(shortName)) {
                              if ("0".equals(defaultValue)) {
                                   defaultValue = "false";
                              } else if ("1".equals(defaultValue)) {
                                   defaultValue = "true";
                              }
                         }

                         defaultMethod.addBodyLine(String.format("instance.%s = new %s(\"%s\");", introspectedColumn.getJavaProperty(), shortName, defaultValue));
                    }
               }

               defaultMethod.addBodyLine("return instance;");
               topLevelClass.addMethod(defaultMethod);
          }

     }

     @Override
     public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
          return !this.needsData && !this.needsGetter;
     }
     @Override
     public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
          return !this.needsData && !this.needsSetter;
     }
     @Override
     public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
          this.processEntityClass(topLevelClass, introspectedTable);
          return true;
     }
     @Override
     public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
          this.processEntityClass(topLevelClass, introspectedTable);
          return true;
     }
     @Override
     public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
          this.processEntityClass(topLevelClass, introspectedTable);
          return false;
     }
     @Override
     public void setContext(Context context) {
          super.setContext(context);
          this.useMapperCommentGenerator = !"FALSE".equalsIgnoreCase(context.getProperty("useMapperCommentGenerator"));
          if (this.useMapperCommentGenerator) {
               this.commentCfg = new CommentGeneratorConfiguration();
               this.commentCfg.setConfigurationType(MapperCommentGenerator.class.getCanonicalName());
               context.setCommentGeneratorConfiguration(this.commentCfg);
          }

          context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
          context.getJdbcConnectionConfiguration().addProperty("useInformationSchema", "true");
     }
     @Override
     public void setProperties(Properties properties) {
          super.setProperties(properties);
          String mappers = this.getProperty("mappers");
          if (!StringUtility.stringHasValue(mappers)) {
               throw new RuntimeException("Mapper插件缺少必要的mappers属性!");
          } else {
               String[] var3 = mappers.split(",");

               this.mappers.addAll(Arrays.asList(var3));

               this.caseSensitive = Boolean.parseBoolean(this.properties.getProperty("caseSensitive"));
               this.forceAnnotation = this.getPropertyAsBoolean("forceAnnotation");
               this.beginningDelimiter = this.getProperty("beginningDelimiter", "");
               this.endingDelimiter = this.getProperty("endingDelimiter", "");
               this.schema = this.getProperty("schema");
               String lombok = this.getProperty("lombok");
               if (lombok != null && !"".equals(lombok)) {
                    this.needsData = lombok.contains("Data");
                    this.needsGetter = !this.needsData && lombok.contains("Getter");
                    this.needsSetter = !this.needsData && lombok.contains("Setter");
                    this.needsToString = !this.needsData && lombok.contains("ToString");
                    this.needsEqualsAndHashCode = !this.needsData && lombok.contains("EqualsAndHashCode");
                    this.needsAccessors = lombok.contains("Accessors");
               }
               String swagger = this.getProperty("swagger", "false");
               if ("true".equalsIgnoreCase(swagger)) {
                    this.needsSwagger = true;
               }

               if (this.useMapperCommentGenerator) {
                    this.commentCfg.addProperty("beginningDelimiter", this.beginningDelimiter);
                    this.commentCfg.addProperty("endingDelimiter", this.endingDelimiter);
                    String forceAnnotation = this.getProperty("forceAnnotation");
                    if (StringUtility.stringHasValue(forceAnnotation)) {
                         this.commentCfg.addProperty("forceAnnotation", forceAnnotation);
                    }

                    this.commentCfg.addProperty("needsSwagger", this.needsSwagger + "");
               }

               this.generateColumnConsts = this.getPropertyAsBoolean("generateColumnConsts");
               this.generateDefaultInstanceMethod = this.getPropertyAsBoolean("generateDefaultInstanceMethod");
          }
     }

     private String getProperty(String key) {
          return this.properties.getProperty(key);
     }

     private String getProperty(String key, String defaultValue) {
          return this.properties.getProperty(key, defaultValue);
     }

     private Boolean getPropertyAsBoolean(String key) {
          return Boolean.parseBoolean(this.getProperty(key));
     }
}
