package com.ziyi.xfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziyi.xfd.common.CustomException;
import com.ziyi.xfd.dao.SetMealDao;
import com.ziyi.xfd.dto.SetmealDto;
import com.ziyi.xfd.entity.Setmeal;
import com.ziyi.xfd.entity.SetmealDish;
import com.ziyi.xfd.service.SetmealDishService;
import com.ziyi.xfd.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetMealDao, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal表，执行insert保存（到数据库）操作
        this.save(setmealDto);

        // 保存套餐和菜品的关联信息，操作setmeal_dish表，执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 因为setmeal_dishes表中缺少category_id，所以需要补充进去
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        // 操作setmeal_dish表，执行insert操作【saveBatch是批量插入】
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids setmeal的id
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询套餐的状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0){
            // 如果不能删除，直接抛出一个业务异常
            // 说明有售卖中
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        // 如果可以删除，先删除套餐表中的数据 --- setmeal表
        this.removeByIds(ids);  // 批量删除

        // 删除关系表中的数据 --- setmeal_dish表
        // delete from setmeal_dish where setmeal_id in ( ..ids)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(lambdaQueryWrapper);

    }

    /**
     * 修改套餐的起售状态
     * @param status
     * @param ids
     */
    @Override
    public void changeStatus(int status, List<Long> ids){
        for (Long id : ids){
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(status);
            this.updateById(setmeal);
        }
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 更新套餐信息 setmeal表
        this.updateById(setmealDto);

        // 删除setmeal_dish 表对应数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        // 添加新改变的 setmeal_dish的数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 补一个套餐id进去
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
