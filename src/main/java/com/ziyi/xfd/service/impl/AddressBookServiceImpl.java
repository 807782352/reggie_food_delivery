package com.ziyi.xfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziyi.xfd.dao.AddressBookDao;
import com.ziyi.xfd.entity.AddressBook;
import com.ziyi.xfd.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {
}
