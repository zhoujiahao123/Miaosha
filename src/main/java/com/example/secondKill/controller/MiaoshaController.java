package com.example.secondKill.controller;


import com.example.secondKill.domain.MiaoshaOrder;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.domain.OrderInfo;
import com.example.secondKill.rabbitmq.MQSender;
import com.example.secondKill.rabbitmq.MiaoshaMessage;
import com.example.secondKill.redis.GoodsKey;
import com.example.secondKill.result.CodeMsg;
import com.example.secondKill.result.Result;
import com.example.secondKill.service.GoodsService;
import com.example.secondKill.service.MiaoshaService;
import com.example.secondKill.service.OrderService;
import com.example.secondKill.service.RedisService;
import com.example.secondKill.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    /**
     * 秒杀优化的思路：减少数据库的访问
     * 1.系统初始化的时候，把商品的库存数量加载到Redis
     * 2.在收到秒杀请求的时候，Redis先预减库存，如果库存不足，则直接返回，否则继续做第三步
     * 3.请求发送至消息队列，立即返回{排队中}（而不是成功或者失败，类似于12306抢票时候的排队）
     * 4.请求出队，生成订单，减少库存
     * （这里需要注意的是，这样导致了数据库与缓存的数据不一致出现）
     */

    /**
     * 测试：
     * 1365 QPS
     * in 10000 * 10
     * 返回值：
     * 0  ： 排队中
     * -1 ： 秒杀失败
     * 订单id :秒杀成功
     */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, MiaoshaUser user,
                                @RequestParam("goodsId") long goodsId) throws Exception {
        model.addAttribute("user",user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //例如10个商品，同一个用户在发起的两个请求同时到了这里req1 req2
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        //从数据库中取库存改成从redis中取库存
        int stockCount = redisService.get(GoodsKey.getMiaoshaGoodsStock, String.valueOf(goodsId), Integer.class);
        //判断库存是否够
//        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //例如10个商品，同一个用户在发起的两个请求同时到了这里req1 req2,显然都没有秒杀到
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //然后就都开始秒杀，这就出现了同一个用户秒杀了两件商品的情况，针对这个问题，可以建立数据库唯一索引来解决，这就是为什么后面
        //需要建立唯一索引来解决这个问题。
//        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        mqSender.sendMessage(message);
        return Result.success(0);
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> result(MiaoshaUser user, @RequestParam("goodsId") long goodsId) throws Exception {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    /**
     * 该方法会在应用初始化的时候回调
     * 该方法主要是利用redis进行每个商品库存的预存
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) return;
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, String.valueOf(goodsVo.getId()), goodsVo.getStockCount());
        }
    }
}
