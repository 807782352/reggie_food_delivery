package com.ziyi.xfd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ziyi.xfd.common.R;
import com.ziyi.xfd.entity.User;
import com.ziyi.xfd.service.UserService;
import com.ziyi.xfd.utils.SMSUtils;
import com.ziyi.xfd.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 发送手机短信验证码（模拟）
     * @param user （用user自动装配，但其实前端页面只会传一个手机号）
     * @param httpSession
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code = {}", code);
            // 调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("外卖", "", phone, code);

            // 需要将生成的验证码保存到Session中，来做校验
            httpSession.setAttribute(phone, code);


            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户校验
     * @param map  map也能接收json数据（当然也可以再创建个UserDto)
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> sendMsg(@RequestBody Map map, HttpSession httpSession){

        log.info(map.toString());

        // 获取手机号（页面提交过来的）
        String phone = map.get("phone").toString();

        // 获取验证码
        String code = map.get("code").toString();

        // 从Session中获取保存的验证码
        Object codeInSession = httpSession.getAttribute(phone);

        // 进行验证码的匹对（页面提交的验证码 和 Session中保存的验证码进行匹对）
        if (codeInSession != null && codeInSession.toString().equals(code)){
            // 如果能够匹对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){
                // 判断当前手机号对应的用户是否为新用户，若是，则自动为新用户完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("短信发送失败");
    }
}
