package com.ziyi.xfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziyi.xfd.dao.EmployeeDao;
import com.ziyi.xfd.entity.Employee;
import com.ziyi.xfd.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {
}
