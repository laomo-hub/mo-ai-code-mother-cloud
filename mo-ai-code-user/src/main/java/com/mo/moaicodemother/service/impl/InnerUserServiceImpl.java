package com.mo.moaicodemother.service.impl;

import com.mo.moaicodemother.innerservice.InnerUserService;
import com.mo.moaicodemother.entity.User;
import com.mo.moaicodemother.vo.UserVO;
import com.mo.moaicodemother.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@DubboService
@Service("innerUserServiceAdapter")
public class InnerUserServiceImpl implements InnerUserService {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Override
    public List<User> listByIds(Collection<? extends Serializable> ids) {
        return userService.listByIds(ids);
    }

    @Override
    public User getById(Serializable id) {
        return userService.getById(id);
    }

    @Override
    public UserVO getUserVO(User user) {
        return userService.getUserVO(user);
    }
}