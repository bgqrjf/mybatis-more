package com.bgqrj.mybatis.demo;

import com.github.bgqrjf.mybatis.codegen.CodeGenerator;

/**
 * @author yangxin
 * @Description: (用一句话描述该文件做什么)
 * @date
 */
public class Test {
    public static void main(String[] args) {
      new CodeGenerator().create(CodeGenerator.CREATE_LEVEL_OF_CONTROLLER, "test_table");
    }
}
