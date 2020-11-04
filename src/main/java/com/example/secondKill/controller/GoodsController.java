package com.example.secondKill.controller;

import com.example.secondKill.domain.Goods;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.service.GoodsService;
import com.example.secondKill.service.MiaoshaUserService;
import com.example.secondKill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.rmi.MarshalledObject;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String toList(Model model,MiaoshaUser user){
        model.addAttribute("user",user);
        //查询并显示商品
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        System.err.println(goodsVoList.size());
        model.addAttribute("goodsList",goodsVoList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser user,
                           @PathVariable("goodsId") long goodsId){
        model.addAttribute("user",user);
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        //判断是否正在秒杀
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        System.err.println("`````");

        int miaoshaStatus = 0;//0未开始，1进行中，2已结束
        int remainSecond = 0;//-1已结束
        if(now < startAt){
            miaoshaStatus = 0;
            remainSecond = (int) ((startAt - now)/1000);
        }else if(now > endAt){
            miaoshaStatus = 2;
            remainSecond = -1;
        }else{
            miaoshaStatus = 1;
            remainSecond = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSecond",remainSecond);
        return "goods_detail";
    }



}
