package com.github.bgqrjf.mybatis.query;

import com.github.bgqrjf.mybatis.annotations.OneToMany;
import com.github.bgqrjf.mybatis.annotations.ToOne;
import com.github.bgqrjf.mybatis.entity.OneToManyInfo;
import com.github.bgqrjf.mybatis.entity.ToOneInfo;
import com.github.bgqrjf.mybatis.query.mapper.Mapper;
import com.github.bgqrjf.mybatis.utils.CollectionUtils;
import com.github.bgqrjf.mybatis.utils.FieldUtils;
import com.github.bgqrjf.mybatis.utils.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 描述: (用一句话描述该文件做什么)
 *
 * @author yangxin
 * 日期: 2020/11/13
 */
public class SonModelUtils<T> {

    private String[] sonModelNames;
    private Class modelClass;
    private SqlSessionTemplate sqlSessionTemplate;

    public SonModelUtils(String[] sonModelNames, Class modelClass, SqlSessionTemplate sqlSessionTemplate) {
        this.sonModelNames = sonModelNames;
        this.modelClass = modelClass;
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    public List<T> warpSonModel(List<T> result) {
        if (CollectionUtils.isEmpty(result)) {
            return result;
        }
        if (Objects.isNull(sonModelNames) || sonModelNames.length == 0) {
            return result;
        }
        Map<String, Map> sonModelMap = new HashMap<>();
        for (String str : sonModelNames) {
            String[] fields = str.split(StringUtils.REG_UNDERLINE);
            Map<String, Map> map = sonModelMap;
            for (String field : fields) {
                map = map.computeIfAbsent(field, k -> new HashMap<>());
            }
        }

        try {
            bindSonData(result, modelClass, sonModelMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void bindSonData(List resultList, Class modelClass, Map sonModelMap) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(resultList) || Objects.isNull(sonModelMap) || sonModelMap.isEmpty()) {
            return;
        }
        Field modelClassIdField = FieldUtils.getIdField(modelClass);
        if (Objects.isNull(modelClassIdField)) {
            return;
        }

        Set<String> keys = sonModelMap.keySet();
        List<Object> ids = new LinkedList<>();
        for (Object result : resultList) {
            Object idValue = modelClassIdField.get(result);
            if (Objects.isNull(idValue)) {
                continue;
            }
            ids.add(idValue);
        }
        for (String key : keys) {
            Field field = FieldUtils.getField(modelClass, key);
            if (field == null) {
                continue;
            }
            ToOne toOne = field.getAnnotation(ToOne.class);
            if (toOne != null) {
                ToOneInfo toOneInfo = new ToOneInfo(field, toOne);
                field.setAccessible(true);
                if (StringUtils.isNotEmpty(toOne.masterKey())) {
                    toOneByMasterKey(modelClassIdField, ids, resultList, toOneInfo, (Map) sonModelMap.get(key));
                } else {
                    toOneBySonKey(modelClass, resultList, toOneInfo, (Map) sonModelMap.get(key));
                }
            }

            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            if (oneToMany != null) {
                field.setAccessible(true);
                oneToMany(modelClassIdField, ids, resultList, new OneToManyInfo(field, oneToMany), (Map) sonModelMap.get(key));
            }
        }
    }


    @SuppressWarnings(value = "unchecked")
    private void toOneByMasterKey(Field pKeyField, List<Object> ids, List result, ToOneInfo toOneInfo, Map sonModelMap) {
        ToOne toOne = toOneInfo.getToOne();
        if (StringUtils.isEmpty(toOne.masterKey()) || CollectionUtils.isEmpty(ids)) {
            return;
        }
        Class<Mapper> sonMapperClass = (Class<Mapper>) toOne.sonMapper();
        Map<Object, Object> map = new HashMap<>();
        try {
            Mapper sonMapper = sqlSessionTemplate.getMapper(sonMapperClass);
            ParameterizedType pt = (ParameterizedType) sonMapperClass.getGenericInterfaces()[0];
            Class sonClass = (Class) pt.getActualTypeArguments()[0];
            Condition condition = new Condition(sonClass);
            condition.createCriteria().andIn(toOne.masterKey(), ids);
            List objects = sonMapper.selectByCondition(condition);
            if (CollectionUtils.isEmpty(objects)) {
                return;
            }
            Field sonMasterKeyField = FieldUtils.getField(sonClass, toOne.masterKey());
            sonMasterKeyField.setAccessible(true);
            for (Object object : objects) {
                String key = Optional.ofNullable(sonMasterKeyField.get(object)).orElse(new Object()).toString();
                map.put(key, object);
            }
            for (Object t : result) {
                String pk = Optional.ofNullable(pKeyField.get(t)).orElse(new Object()).toString();
                toOneInfo.getField().set(t, map.get(pk));
            }
            bindSonData(objects, sonClass, sonModelMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toOneBySonKey(Class fieldClass, List result, ToOneInfo toOneInfo, Map sonModelMap) {
        ToOne toOne = toOneInfo.getToOne();
        String sonKeyName = toOne.sonKey();
        if (StringUtils.isEmpty(sonKeyName)) {
            return;
        }
        try {
            //以当前主键去子model查询
            Map<String, Field> fieldMap = FieldUtils.getFieldMap(fieldClass);
            Field fkFiled = fieldMap.get(sonKeyName);
            fkFiled.setAccessible(true);
            Set<Object> fkIds = new HashSet<>();
            for (Object t : result) {
                Object id = fkFiled.get(t);
                if (id != null) {
                    fkIds.add(id);
                }
            }
            if (CollectionUtils.isEmpty(fkIds)) {
                return;
            }
            Class<Mapper> sonMapperClass = (Class<Mapper>) toOne.sonMapper();
            Mapper sonMapper = sqlSessionTemplate.getMapper(sonMapperClass);
            List sonList = sonMapper.selectByIds(StringUtils.join(fkIds));
            if (CollectionUtils.isEmpty(sonList)) {
                return;
            }
            ParameterizedType pt = (ParameterizedType) sonMapperClass.getGenericInterfaces()[0];
            Class sonClass = (Class) pt.getActualTypeArguments()[0];

            Map<Object, Object> map = new HashMap<>();
            Field sonIdField = FieldUtils.getIdField(sonClass);
            for (Object sonObj : sonList) {
                String key = Optional.ofNullable(sonIdField.get(sonObj)).orElse(new Object()).toString();
                map.put(key, sonObj);
            }
            for (Object t : result) {
                String fk = Optional.ofNullable(fkFiled.get(t)).orElse("").toString();
                toOneInfo.getField().set(t, map.get(fk));
            }
            bindSonData(result, fieldClass, sonModelMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void oneToMany(Field pKeyField, List<Object> ids, List resultList, OneToManyInfo oneToManyInfo, Map sonModelMap) {
        if (CollectionUtils.isEmpty(resultList) || CollectionUtils.isEmpty(ids)) {
            return;
        }
        try {
            OneToMany oneToMany = oneToManyInfo.getOneToMany();
            Class<Mapper> sonMapperClass = (Class<Mapper>) oneToMany.sonMapper();
            Map<Object, List<Object>> map = new HashMap<>();

            Mapper sonMapper = sqlSessionTemplate.getMapper(sonMapperClass);

            ParameterizedType pt = (ParameterizedType) sonMapperClass.getGenericInterfaces()[0];
            Class sonClass = (Class) pt.getActualTypeArguments()[0];

            Condition condition = new Condition(sonClass);
            condition.createCriteria().andIn(oneToMany.masterKey(), ids);
            List sonObjectList = sonMapper.selectByCondition(condition);
            //查询子model
            if (CollectionUtils.isEmpty(sonObjectList)) {
                return;
            }
            Field classFiled = FieldUtils.getField(sonClass, oneToMany.masterKey());
            classFiled.setAccessible(true);
            for (Object sonObject : sonObjectList) {
                String key = Optional.ofNullable(classFiled.get(sonObject)).orElse(new Object()).toString();
                List<Object> data = map.computeIfAbsent(key, k -> new LinkedList<>());
                data.add(sonObject);
            }
            Field sonField = oneToManyInfo.getField();
            sonField.setAccessible(true);
            for (Object result : resultList) {
                String pk = Optional.ofNullable(pKeyField.get(result)).orElse(new Object()).toString();
                sonField.set(result, map.get(pk));
            }
            bindSonData(sonObjectList, sonClass, sonModelMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
