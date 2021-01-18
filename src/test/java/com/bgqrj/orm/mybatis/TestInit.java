package com.bgqrj.orm.mybatis;

import com.bgqrj.orm.mybatis.codegen.CodeGenerator;

/**
 * 2019/9/4
 *
 * @author yx
 */
public class TestInit {

     public static void main(String[] args) {
          new CodeGenerator().create(CodeGenerator.CREATE_LEVEL_OF_ALL,"goods");
     }
}
