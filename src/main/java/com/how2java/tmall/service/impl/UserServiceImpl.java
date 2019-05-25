package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.UserMapper;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.pojo.UserExample;
import com.how2java.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public void add(User u) {
        userMapper.insert(u);
    }

    @Override
    public void delete(int id) {
        userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(User u) {
        userMapper.updateByPrimaryKeySelective(u);
    }

    @Override
    public User get(int id) {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public List<User> list() {
        UserExample example = new UserExample();
        example.setOrderByClause("id DESC");
        List<User> users = userMapper.selectByExample(example);
        return users;
    }

    @Override
    public boolean isExist(String name) {
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(example);
        if (!users.isEmpty())
            return true;
        return false;
    }

    @Override
    public User get(String name, String password) {
        //用户名登录
        UserExample example1 = new UserExample();
        example1.createCriteria().andNameEqualTo(name).andPasswordEqualTo(password);
        List<User> users1 = userMapper.selectByExample(example1);

        //手机号码登录
        UserExample example2 = new UserExample();
        example1.createCriteria().andTelephoneEqualTo(name).andPasswordEqualTo(password);
        List<User> users2 = userMapper.selectByExample(example2);
        if (users1.isEmpty() && users2.isEmpty())
            //不论输入的是用户名还是手机号，用户都不存在
            return null;
        else if (users1.isEmpty())
            //用户本来存在，但输入的是 手机号
            return users2.get(0);
        else
            //用户本来存在，但输入的是 用户名
            return users1.get(0);
    }
}
