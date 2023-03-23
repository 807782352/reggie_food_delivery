package com.ziyi.xfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziyi.xfd.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
