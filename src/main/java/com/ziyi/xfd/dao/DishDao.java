package com.ziyi.xfd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziyi.xfd.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishDao extends BaseMapper<Dish> {
}
