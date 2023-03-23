package com.ziyi.xfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziyi.xfd.dto.DishDto;
import com.ziyi.xfd.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据（需要操作两张表：dish、dish_flavor)
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    // 更新菜品
    public void updateWithFlavor(DishDto dishDto);

    // 更新状态
    public void updateStatusWithIds(int status, Long[] ids);

}
