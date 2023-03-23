package com.ziyi.xfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziyi.xfd.dao.UserDao;
import com.ziyi.xfd.entity.User;
import com.ziyi.xfd.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}
