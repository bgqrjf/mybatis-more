package ${basePackage}.service.impl;

import ${basePackage}.dao.mapper.${mapperPath}Mapper;
import ${modelPackage}.${modelNameUpperCamel};
import ${basePackage}.service.${modelNameUpperCamel}Service;
import com.github.bgqrjf.mybatis.query.base.AbstractService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


/**
 * Created by ${author} on ${date}.
 */
@Service
public class ${modelNameUpperCamel}ServiceImpl extends AbstractService<${modelNameUpperCamel}> implements ${modelNameUpperCamel}Service {
    @Resource
    private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;


}
