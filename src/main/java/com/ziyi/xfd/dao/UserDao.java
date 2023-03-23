package com.ziyi.xfd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ziyi.xfd.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
