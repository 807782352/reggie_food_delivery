package com.ziyi.xfd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziyi.xfd.common.R;
import com.ziyi.xfd.entity.Employee;
import com.ziyi.xfd.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 需求：
         * 1. 将页面提交的密码password进行md5加密处理
         * 2. 根据页面提交的用户username查询数据库
         * 3. 如果没有查询到则返回登录失败
         * 4. 密码匹对，如果不一致则返回登录失败的结果
         * 5. 查看员工状态，如果为已禁用的状态，则返回员工已禁用的结果
         * 6. 登录成功， 将员工id存入Session并返回登录成功结果
         */

        // 1. 将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的用户username查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper); // 因为能确定username唯一

        // 3. 如果没有查询到则返回登录失败
        if(emp == null){
            return R.error("登录失败");
        }

        // 4. 密码匹对，如果不一致则返回登录失败的结果
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        // 5. 查看员工状态，如果为已禁用的状态，则返回员工已禁用的结果
        if (emp.getStatus() == 0) { // 0表示禁用
            return R.error("账号已禁用");
        }

        // 6. 登录成功， 将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request  因为要操作session
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 清理Session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     *
     * @param request
     * @param employee
     * @return 因为不用回传数据，所以返回String就可以了
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息:{}", employee.toString());

        // 设置初始密码（比如身份证后6位），需要进行md5的加密处理
        String pswd = employee.getIdNumber().substring(12);
        employee.setPassword(DigestUtils.md5DigestAsHex(pswd.getBytes()));


        // 下面的代码可以使用【公共字段自动填充来替代】
//        // 设置创建时间
//        employee.setCreateTime(LocalDateTime.now());
//
//        // 设置更新时间
//        employee.setUpdateTime(LocalDateTime.now());
//
//        // 获取当前用户【比如管理员】登录的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);


        employeeService.save(employee);

        return R.success("新增员工成功");
    }


    /**
     * 员工信息分页查询
     * [注：这里GetMapping()中，因为变量名与路径上变量一致，所以自动装配，无需@PathVariable
     * [一般来说， 使用restful风格才需要@PathVariable去获得变量】
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 添加一个过滤条件
            // 【注：StringUtils （apache下的包）的isNotEmpty作用
            // 相比于 name != null
            // 多了一个判断字符长度的功能（即name.length() != 0）】
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        // 添加一个排序条件(按照更新时间，降序排）
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 根据id来修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

        // 改用自动填充
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);


        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id来查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
}
