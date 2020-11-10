package com.example.secondKill.service;

import com.example.secondKill.dao.MiaoshaUserDao;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.exception.GlobalException;
import com.example.secondKill.redis.MiaoshaUserKey;
import com.example.secondKill.result.CodeMsg;
import com.example.secondKill.result.Result;
import com.example.secondKill.util.MD5Util;
import com.example.secondKill.util.UUIDUtil;
import com.example.secondKill.util.ValidatorUtil;
import com.example.secondKill.vo.LoginVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    @Autowired(required = false)
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    @Autowired(required = false)
    MiaoshaUserDao userDao;

    public static final String COOKIE_NAME_TOKEN = "token";

    /**
     * 通过id获取用户，先取缓存，再取数据库
     * @param id
     * @return
     */
    public MiaoshaUser getById(long id) {
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById,String.valueOf(id),MiaoshaUser.class);
        if(user != null) return user;
        user = miaoshaUserDao.getById(id);
        redisService.set(MiaoshaUserKey.getById,String.valueOf(id),user);
        return user;
    }

    /**
     * 更新操作，采用的是简单的双删策略
     * @param token
     * @param id
     * @param formPassword
     * @return
     */
    public boolean updatePassword(String token,long id,String formPassword){
        MiaoshaUser user = getById(id);
        if(user == null) throw new GlobalException(CodeMsg.USER_NOTFOUND);
        //删缓存
        redisService.delete(MiaoshaUserKey.getById,String.valueOf(id));
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        String newPass = MD5Util.formPassToDbPass(formPassword,user.getSalt());
        toBeUpdate.setPassword(newPass);
        //更新数据库
        userDao.updatePassword(toBeUpdate);
        //再删缓存
        redisService.delete(MiaoshaUserKey.getById,String.valueOf(id));
        user.setPassword(newPass);
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        //延长有效期
        if(user != null) addCookie(response,token,user);
        return user;
    }
    public void addCookie(HttpServletResponse response,String token,MiaoshaUser miaoshaUser){
        redisService.set(MiaoshaUserKey.token,token,miaoshaUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public Result<String> login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPassword = loginVo.getPassword();
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.USER_NOTFOUND);
        }
        String dbPassword = miaoshaUser.getPassword();
        String salt = miaoshaUser.getSalt();
        if(MD5Util.formPassToDbPass(formPassword,salt).equals(dbPassword)){
            //生成cookie
            String token = UUIDUtil.uuid();
            addCookie(response,token,miaoshaUser);
            return Result.success(token);
        }else{
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

    }
}
