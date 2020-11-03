package com.example.secondKill.controller;


import com.example.secondKill.domain.MiaoshaOrder;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.domain.OrderInfo;
import com.example.secondKill.service.GoodsService;
import com.example.secondKill.service.MiaoshaService;
import com.example.secondKill.service.OrderService;
import com.example.secondKill.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;


    @RequestMapping("/do_miaosha")
    public String list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if(user == null){
            return "login";
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断库存是否够
        int stockCount = goods.getStockCount();
        if(stockCount <= 0){
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return "miaosha_fail";
        }
        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
    }
}
