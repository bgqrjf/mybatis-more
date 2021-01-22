package com.bgqrj.mybatis.demo.entity;

import com.bgqrj.mybatis.demo.vo.base.BaseEntity;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "test_table")
public class TestTable extends BaseEntity {
    private String name1;

    private String name2;
}