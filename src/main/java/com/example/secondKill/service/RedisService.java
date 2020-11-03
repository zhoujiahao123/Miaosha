package com.example.secondKill.service;

import com.alibaba.fastjson.JSON;
import com.example.secondKill.redis.KeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

    @Autowired(required = false)
    JedisPool jedisPool;

    public <T> Boolean exist(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPoll(jedis);
        }

    }
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPoll(jedis);
        }

    }
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPoll(jedis);
        }

    }
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(prefix.getPrefix() + key);
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            returnToPoll(jedis);
        }

    }
    public <T> Boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if(str == null || str.length() <=0){
                return false;
            }
            String realKey = prefix.getPrefix() + key;
            int second = prefix.expireSeconds();
            if(second <= 0){
                jedis.set(realKey,str);
            }else{
                jedis.setex(realKey,second,str);
            }
            return true;
        }finally {
            returnToPoll(jedis);
        }

    }

    private <T> String beanToString(T value) {
        if(value == null) return null;
        Class<?> aClass = value.getClass();
        if(aClass == int.class || aClass == Integer.class){
            return "" + value;
        }else if(aClass == String.class){
            return (String) value;
        }else if(aClass == long.class || aClass == Long.class){
            return "" + value;
        }else{
            return JSON.toJSONString(value);
        }

    }

    private <T> T stringToBean(String str,Class<T> aClass) {
        if(str == null || str.length() <= 0 || aClass == null) return null;
        if(aClass == int.class || aClass == Integer.class){
            return (T) Integer.valueOf(str);
        }else if(aClass == String.class){
            return (T) str;
        }else if(aClass == long.class || aClass == Long.class){
            return (T) Long.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),aClass);
        }
    }


    private void returnToPoll(Jedis jedis){
        if(jedis != null){
            jedis.close();
        }
    }



}
