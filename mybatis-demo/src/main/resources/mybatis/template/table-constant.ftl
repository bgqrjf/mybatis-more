package ${basePackage}.constant.table;

/**
*
* ${tableName}表字段常量类
* 日期: ${date}
* @author ${author}.
*
*/
public class ${className}
{
    private ${className}(){
     }
<#list propertyNameList as property>
    public static final String ${property.upperUnderscoreName} = "${property.name}";
</#list>
}