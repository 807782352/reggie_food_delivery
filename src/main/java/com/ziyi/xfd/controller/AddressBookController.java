package com.ziyi.xfd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ziyi.xfd.common.BaseContext;
import com.ziyi.xfd.common.R;
import com.ziyi.xfd.entity.AddressBook;
import com.ziyi.xfd.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 查询特定用户的全部地址 （无分页）
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        // 获得登录用户的id信息
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        // 条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 新增收货地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        addressBookService.save(addressBook);
        return R.success("添加成功");
    }

    /**
     * 设置默认地址
     * @return
     */
    @PutMapping("default")
    @Transactional
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
//        updateWrapper.eq(AddressBook::getUserId, addressBook.getUserId()); 不能这么写，因为此时getUserId为null,传过去的id是地址id
//        【代替方法：可以先用地址id 找到UserId，然后再下一步去设置】
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());

        log.info("userId, {}", addressBook.getUserId());
        log.info("currentId, {}", BaseContext.getCurrentId());

        updateWrapper.set(AddressBook::getIsDefault, 0);
        // SQL: update address_book set is_default = 0 where user_id = ?
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        // SQL: update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success("更新成功");
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    public R<AddressBook> getDefault(){
        // 获取当前的userId，从而得到当前的addressBook
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook == null){
            return R.error("没有找到该对象");
        } else{
            return R.success(addressBook);
        }
    }


    /**
     * 回显id
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<AddressBook> getAddressById(@PathVariable Long id){
//        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(AddressBook::getId, id);

        return R.success(addressBookService.getById(id));
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }


    /**
     * 删除地址
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long[] ids){
        for (Long id : ids){
            addressBookService.removeById(id);
        }
        return R.success("删除成功");
    }
}
