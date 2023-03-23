package com.ziyi.xfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziyi.xfd.common.CustomException;
import com.ziyi.xfd.dao.CategoryDao;
import com.ziyi.xfd.entity.Category;
import com.ziyi.xfd.entity.Dish;
import com.ziyi.xfd.entity.Setmeal;
import com.ziyi.xfd.service.CategoryService;
import com.ziyi.xfd.service.DishService;
import com.ziyi.xfd.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService{

    // 记得一定要自动匹配，否则service会为空
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setMealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);

        // 查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count > 0){
            // 已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        // 查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setMealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            // 已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 正常删除分类
        super.removeById(id);
    }
}
