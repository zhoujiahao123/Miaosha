package com.example.secondKill.service;

import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.domain.OrderInfo;
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

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo){
        //先减库存
        goodsService.reduceStock(goodsVo.getId());
        //再下订单
        return orderService.createOrder(user,goodsVo);
    }
}
