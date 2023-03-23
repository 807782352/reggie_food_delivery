package com.ziyi.xfd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziyi.xfd.common.R;
import com.ziyi.xfd.dto.SetmealDto;
import com.ziyi.xfd.entity.Category;
import com.ziyi.xfd.entity.Setmeal;
import com.ziyi.xfd.entity.SetmealDish;
import com.ziyi.xfd.service.CategoryService;
import com.ziyi.xfd.service.SetmealDishService;
import com.ziyi.xfd.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name){
        // 构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoInfo = new Page<>();

        // 创造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        // 进行分页查询
        setmealService.page(pageInfo, queryWrapper);

        // 展示上，还要完善 套餐分类 信息展示

        // 对象属性拷贝 (除了records，因为泛型不一样）
        BeanUtils.copyProperties(pageInfo, dtoInfo, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            // 对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据分类id来查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoInfo.setRecords(setmealDtos);

        return R.success(dtoInfo);
    }

    /**
     * 删除套餐
     * @param ids 【基础数据类型可以不用加 @RequestParam】
     *            RequestBody 接收的是请求体里面的数据；
     *            RequestParam 接收的是key-value里面的参数
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids为：", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }


    /**
     * 更改停售/起售状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, @RequestParam List<Long> ids){
        log.info("ids为：",ids);
        setmealService.changeStatus(status, ids);
        return R.success("更新起售状态成功");
    }


    /**
     * 回显套餐
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("id为：", id);
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        // 复制属性
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 在setmeal_dish关系表中，找到对应要修改的菜品，并提取
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);

        return R.success(setmealDto);
    }


    /**
     * 套餐更新
     * @param setmealDto
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("更改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(queryWrapper);

        return R.success(setmealList);
    }
}
