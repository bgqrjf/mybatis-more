package com.bgqrj.orm.mybatis.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 描述: json工具类(jackson)
 *
 * @author yangxin
 * 日期: 2020/9/21
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, Boolean.TRUE);

        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, false);
        //在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
    }

    private JsonUtils() {
    }

    /**
     * json转java对象
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @param <T>   对象泛型
     * @return 对象实例
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Parse json \"" + json + "\" to object \"" + clazz.getName() + "\" error.", e);
            return null;
        }
    }

    /**
     * json转List，其中List为泛型定义
     *
     * @param json         json字符串
     * @param genericClazz List中泛型数据的真实类型
     * @param <T>          泛型
     * @return 对象List
     */
    public static <T> List<T> toGenericList(String json, Class<T> genericClazz) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, genericClazz);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            log.error("Parse json \"" + json + "\" to generic list \"" + genericClazz.getName() + "\" error.", e);
            return Collections.emptyList();
        }
    }

    public static <G, O> O toGenericListObject(String json, Class<G> genericClazz, Class<O> clazz) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, genericClazz);
            JavaType finalJavaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(clazz, javaType);
            return OBJECT_MAPPER.readValue(json, finalJavaType);
        } catch (Exception e) {
            log.error("Parse json \"" + json + "\" to generic \"" + genericClazz.getName() + "\" list object \"" + clazz.getName() + "\" error.", e);
            return null;
        }
    }

    /**
     * json转对象，其中O为目标对象，T为O中的泛型对象
     *
     * @param json         json字符串
     * @param genericClazz 目标对象O中泛型数据的真实类型
     * @param clazz        目标对象的类型
     * @param <G>          标对象O中泛型数据的的泛型
     * @param <O>          标对象O的泛型
     * @return 目标对象实例
     */
    public static <G, O> O toGenericObject(String json, Class<G> genericClazz, Class<O> clazz) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(clazz, genericClazz);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            log.error("Parse json \"" + json + "\" to generic object \"" + genericClazz.getName() + "\" error.", e);
            return null;
        }
    }

    /**
     * java对象转json
     *
     * @param obj Java对象
     * @param <T> 对象泛型
     * @return json字符串
     */
    public static <T> String toJson(T obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Parse from object \"" + obj.getClass().getName() + "\" error.", e);
            return null;
        }
    }
}
