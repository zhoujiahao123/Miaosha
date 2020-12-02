package com.example;

import com.example.secondKill.Application;
import com.example.secondKill.dao.MiaoshaUserDao;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.rabbitmq.MQSender;
import com.example.secondKill.rabbitmq.MiaoshaMessage;
import com.example.secondKill.util.MD5Util;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Test {

    @Autowired(required = false)
    MiaoshaUserDao miaoshaUserDao;
    @org.junit.Test
    public void insertUserToDb(){
        for(int i = 1;i<=5000;i++){
            MiaoshaUser user = new MiaoshaUser();
            user.setId(13000000000L+i);
            user.setLoginCount(1);
            user.setNickname("user"+i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDbPass("123456", user.getSalt()));
            miaoshaUserDao.insert(user);

        }
    }

    @Autowired
    MQSender mqSender;
    @org.junit.Test
    public void testRabbitMq() throws Exception {
        MiaoshaUser user = new MiaoshaUser();
        user.setId((long) 444);
        long goodsId = 555;
        MiaoshaMessage message = new MiaoshaMessage();
        message.setGoodsId(goodsId);
        message.setUser(user);
        mqSender.sendMessage(message);
    }
}
