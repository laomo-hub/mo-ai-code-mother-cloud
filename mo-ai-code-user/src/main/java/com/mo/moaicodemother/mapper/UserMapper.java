package com.mo.moaicodemother.mapper;


import com.mo.moaicodemother.entity.User;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 映射层。
 *
 * @author <a href="https://github.com/laomo">程序员烙馍</a>
 * @since 2026-05-16
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
