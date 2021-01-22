package ${basePackage}.controller;

import io.swagger.annotations.ApiOperation;
import com.github.pagehelper.PageInfo;
import com.bgqrj.mybatis.demo.vo.base.ResultVO;
import com.bgqrj.mybatis.demo.util.ResultUtils;
import ${modelPackage}.${modelNameUpperCamel};
import ${basePackage}.service.${modelNameUpperCamel}Service;
import com.github.bgqrjf.mybatis.query.weekend.WeekEndQuery;
import com.github.bgqrjf.mybatis.utils.PageUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by ${author}.
*/
@RestController
@RequestMapping("${baseRequestMapping}")
public class ${modelNameUpperCamel}Controller {
    @Resource
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    @ApiOperation("插入")
    @PostMapping
    public ResultVO<Integer> add(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        int cnt = ${modelNameLowerCamel}Service.save(${modelNameLowerCamel});
        return ResultUtils.success(cnt);
    }

    @ApiOperation("删除")
    @DeleteMapping
    public ResultVO<Integer> del(@RequestParam Integer id) {
        int cnt = ${modelNameLowerCamel}Service.deleteById(id);
        return ResultUtils.success(cnt);
    }

    @ApiOperation("修改")
    @PutMapping
    public ResultVO<Integer> mod(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        int cnt = ${modelNameLowerCamel}Service.update(${modelNameLowerCamel});
        return ResultUtils.success(cnt);
    }

    @ApiOperation("根据主键id查询")
    @GetMapping("/{id}")
    public ResultVO<${modelNameUpperCamel}> get(@PathVariable Integer id) {
        ${modelNameUpperCamel} ${modelNameLowerCamel} = ${modelNameLowerCamel}Service.selectOne(id);
        return ResultUtils.success(${modelNameLowerCamel});
    }

    @ApiOperation("查询全部列表")
    @GetMapping("/list")
    public ResultVO<PageInfo<${modelNameUpperCamel}>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           @RequestParam(required = false) String orderBy) {
        WeekEndQuery<${modelNameUpperCamel}> query = ${modelNameLowerCamel}Service.createWeekEndQuery();
        PageUtils.startPage(pageNum, pageSize, orderBy);
        List<${modelNameUpperCamel}> list = query.execute();
        return ResultUtils.success(new PageInfo<>(list));
    }
}
