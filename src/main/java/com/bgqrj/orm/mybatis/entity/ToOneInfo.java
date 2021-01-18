package com.bgqrj.orm.mybatis.entity;

import com.bgqrj.orm.mybatis.annotations.ToOne;

import java.lang.reflect.Field;

/**
 * 2019/9/20
 *
 * @author yx
 */
public class ToOneInfo {

    private Field field;
    private ToOne toOne;
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public ToOne getToOne() {
        return toOne;
    }

    public void setToOne(ToOne toOne) {
        this.toOne = toOne;
    }
    public ToOneInfo(Field field, ToOne toOne) {
        this.field = field;
        this.toOne = toOne;
    }

}
