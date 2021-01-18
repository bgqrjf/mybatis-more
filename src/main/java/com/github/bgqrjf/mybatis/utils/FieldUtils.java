package com.github.bgqrjf.mybatis.utils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 2019/9/20
 *
 * @author yx
 */
public class FieldUtils {

    private FieldUtils() {

    }

    public static Field getField(Class clazz, String name) {
        return getFieldMap(clazz).get(name);
    }

    public static Map<String, Field> getFieldMap(Class clazz) {
        Field[] sonFields = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<>(Arrays.asList(sonFields));
        Field[] superFields = clazz.getSuperclass().getDeclaredFields();
        fields.addAll(Arrays.asList(superFields));

        return fields.stream()
                .collect(Collectors.toMap(Field::getName, field -> field, (f1, f2) -> f1));

    }

    public static Field getIdField(Class modelClass) {
        Map<String, Field> fieldMap = getFieldMap(modelClass);
        if (fieldMap == null || fieldMap.isEmpty()) {
            return null;
        }
        Set<Map.Entry<String, Field>> entrySet = fieldMap.entrySet();
        Field pKeyField = null;
        for (Map.Entry<String, Field> entry : entrySet) {
            Field field = entry.getValue();
            Id idAn = field.getAnnotation(Id.class);
            if (idAn != null) {
                pKeyField = field;
                pKeyField.setAccessible(true);
                break;
            }
        }
        return pKeyField;
    }

}
