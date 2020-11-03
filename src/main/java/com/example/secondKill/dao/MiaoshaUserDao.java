package com.example.secondKill.dao;

import com.example.secondKill.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id=#{id}")
    MiaoshaUser getById(@Param("id") long id);
}
