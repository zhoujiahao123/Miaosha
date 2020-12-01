package com.example.secondKill.rabbitmq;

import com.example.secondKill.domain.MiaoshaOrder;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.service.GoodsService;
import com.example.secondKill.service.MiaoshaService;
import com.example.secondKill.service.MiaoshaUserService;
import com.example.secondKill.service.OrderService;
import com.example.secondKill.vo.GoodsVo;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MQReceive {
    private static final Logger logger = LoggerFactory.getLogger(MQReceive.class);
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
    exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
    type = "${spring.rabbitmq.listener.order.exchange.type}",
    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
    key = "${spring.rabbitmq.listener.order.key}"
    ))
    @RabbitHandler
    public void onMessage(@Payload MiaoshaMessage message, Channel channel, @Headers Map<String, Object> headers) throws Exception {
        logger.info(message.toString());
        MiaoshaUser user = message.getUser();
        long goodsId = message.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stockCount = goodsVo.getStockCount();
        if (stockCount <= 0) {
            return;
        }
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) return;
        miaoshaService.miaosha(user, goodsVo);
        channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG),false);
    }
}
