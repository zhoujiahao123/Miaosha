package com.example.secondKill.controller;


import com.example.secondKill.domain.MiaoshaOrder;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.domain.OrderInfo;
import com.example.secondKill.result.CodeMsg;
import com.example.secondKill.result.Result;
import com.example.secondKill.service.GoodsService;
import com.example.secondKill.service.MiaoshaService;
import com.example.secondKill.service.OrderService;
import com.example.secondKill.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;


    /**
     * 测试：
     *1365 QPS
     * in 10000 * 10
     */
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId")long goodsId){
//        model.addAttribute("user",user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //例如10个商品，同一个用户在发起的两个请求同时到了这里req1 req2
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断库存是否够
        int stockCount = goods.getStockCount();
        if(stockCount <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //例如10个商品，同一个用户在发起的两个请求同时到了这里req1 req2,显然都没有秒杀到
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //然后就都开始秒杀，这就出现了同一个用户秒杀了两件商品的情况，针对这个问题，可以建立数据库唯一索引来解决，这就是为什么后面
        //需要建立唯一索引来解决这个问题。
        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
        return Result.success(orderInfo);
    }
}
