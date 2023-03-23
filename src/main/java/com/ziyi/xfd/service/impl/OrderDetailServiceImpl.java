package com.ziyi.xfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziyi.xfd.dao.OrderDetailDao;
import com.ziyi.xfd.entity.OrderDetail;
import com.ziyi.xfd.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {
}
