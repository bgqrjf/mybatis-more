package com.bgqrj.orm.mybatis.entity;

import com.bgqrj.orm.mybatis.annotations.OneToMany;

import java.lang.reflect.Field;

/**
 * 2019/9/20
 *
 * @author yx
 */
public class OneToManyInfo {

     private Field field;
     private OneToMany oneToMany;

     public Field getField() {
          return field;
     }

     public void setField(Field field) {
          this.field = field;
     }

     public OneToMany getOneToMany() {
          return oneToMany;
     }

     public void setOneToMany(OneToMany oneToMany) {
          this.oneToMany = oneToMany;
     }

     public OneToManyInfo(Field field, OneToMany oneToMany) {
          this.field = field;
          this.oneToMany = oneToMany;
     }

}
