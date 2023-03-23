package com.ziyi.xfd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziyi.xfd.entity.Orders;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrdersDao extends BaseMapper<Orders> {
}
