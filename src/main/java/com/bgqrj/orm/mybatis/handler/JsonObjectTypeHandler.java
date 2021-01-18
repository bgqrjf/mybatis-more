package com.bgqrj.orm.mybatis.handler;


import com.bgqrj.orm.mybatis.utils.JsonUtils;
import com.bgqrj.orm.mybatis.utils.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


/**
 * mybatis jsonObject对象类型转成实体
 *
 * @author yangxin
 */
public class JsonObjectTypeHandler<T> extends BaseTypeHandler<T> {
    private static final String EMPTY_ARR = "[]";
    private Class<T> clazz;

    public JsonObjectTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.nonNull(parameter)) {
            ps.setString(i, JsonUtils.toJson(parameter));
        }

    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName), clazz);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex), clazz);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex), clazz);
    }

    private T toObject(String content, Class<T> clazz) {
        if (StringUtils.isNotBlank(content) && !EMPTY_ARR.equals(content)) {
            return JsonUtils.toObject(content, clazz);
        }
        return null;
    }

}