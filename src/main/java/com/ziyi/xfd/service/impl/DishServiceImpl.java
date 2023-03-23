package com.ziyi.xfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziyi.xfd.dao.DishDao;
import com.ziyi.xfd.dto.DishDto;
import com.ziyi.xfd.entity.Dish;
import com.ziyi.xfd.entity.DishFlavor;
import com.ziyi.xfd.service.DishFlavorService;
import com.ziyi.xfd.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.DosFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的[基本信息]到 Dish表 （因为dishDto继承了dish表）
        this.save(dishDto);

        // 保存菜品口味数据到菜品口味表 dish_flavor
        Long dishId = dishDto.getId(); // 获取菜品的id
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 重新封装dish_flavor (可以用流形式[效率高些？]，也可以用for循环）
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品的基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应口味的数据 -- dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        // 添加当前提交过来的口味的数据 -- dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 新的dish_flavor少了dishId，把它填上
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 更新状态 [只涉及dish表即可】
     * @param ids
     */
    @Override
    public void updateStatusWithIds(int status, Long[] ids) {
        // 注意：status本身就是更改后的状态，所以无需反转
        for (Long id : ids){
            Dish dish = this.getById(id);
            dish.setStatus(status);
            this.updateById(dish);
        }
    }




}
