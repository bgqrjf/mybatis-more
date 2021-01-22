package com.bgqrj.mybatis.demo.service.impl;

import com.bgqrj.mybatis.demo.dao.mapper.TestTableMapper;
import com.bgqrj.mybatis.demo.entity.TestTable;
import com.bgqrj.mybatis.demo.service.TestTableService;
import com.github.bgqrjf.mybatis.query.base.AbstractService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


/**
 * Created by BGQRJF-MYBATIS on 2021-01-22 14:48:54.
 */
@Service
public class TestTableServiceImpl extends AbstractService<TestTable> implements TestTableService {
    @Resource
    private TestTableMapper testTableMapper;


}
