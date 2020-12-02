package com.example.secondKill.service;

import com.example.secondKill.domain.MiaoshaOrder;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.domain.OrderInfo;
import com.example.secondKill.redis.MiaoshaKey;
import com.example.secondKill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {


    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        //先减库存
        boolean success = goodsService.reduceStock(goodsVo.getId());
        if (!success) {
            setGoodsOver(goodsVo.getId());
            return null;
        }
        //再下订单
        return orderService.createOrder(user, goodsVo);
    }

    /**
     * 该方法为客户端提供轮询的请求
     * 0  ： 排队中
     * -1 ： 失败
     * orderID ： 秒杀成功
     *
     * @param id
     * @param goodsId
     */
    public Long getMiaoshaResult(Long id, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
        if (order != null) {
            return order.getId();
        } else {
            if (getGoodsOver(goodsId)) {
                return -1L;
            } else {
                return 0L;
            }
        }
    }

    private void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.getMiaoshaGoodsIsOver(), String.valueOf(goodsId), true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exist(MiaoshaKey.getMiaoshaGoodsIsOver(), String.valueOf(goodsId));
    }
}
