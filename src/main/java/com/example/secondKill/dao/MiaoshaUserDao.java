package com.example.secondKill.dao;

import com.example.secondKill.domain.MiaoshaUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id=#{id}")
    MiaoshaUser getById(@Param("id") long id);

    @Insert("insert into miaosha_user(id,nickname,password,salt,head,register_date,last_login_date,login_count) values(#{id},#{nickname},#{password},#{salt},#{head},#{registerDate},#{lastLoginDate},#{loginCount})")
    int insert(MiaoshaUser user);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void updatePassword(MiaoshaUser user);
}
