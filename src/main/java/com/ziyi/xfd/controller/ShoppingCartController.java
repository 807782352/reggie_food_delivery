package com.ziyi.xfd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ziyi.xfd.common.BaseContext;
import com.ziyi.xfd.common.R;
import com.ziyi.xfd.entity.ShoppingCart;
import com.ziyi.xfd.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}", shoppingCart);

        // 设置当前userId，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 查询当前菜品或套餐 是否已经在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null){
            // 说明添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else{
            // 添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // SQL: select * from shopping_cart where user_id = ? and dish_id/setmeal_id
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);

        if (shoppingCart1 != null){
            // 若已经存在，则【更改】在原来数量基础上+1
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCart1);
        } else {
            // 若不存在，则【添加】到购物车，数量默认为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
//            这里无法自动装配，因为没有写对应的（创建人）数据类型(错误如下），所以要自己补充
        // There is no setter for property named 'updateTime' in 'class com.ziyi.xfd.entity.ShoppingCart'
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        }

        return R.success(shoppingCart1);
    }


    /**
     * 删减购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        if (shoppingCart.getDishId() != null){
            // 说明是菜品信息
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 说明是套餐信息
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart serviceOne = shoppingCartService.getOne(queryWrapper);

        Integer number = serviceOne.getNumber();
        if (number <= 0){
            return null;
        }
        serviceOne.setNumber(number-1);
        shoppingCartService.updateById(serviceOne);

        return R.success(serviceOne);
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }


    /**
     * 删除购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);

        return R.success("清空完毕");
    }
}
