package com.example.secondKill.service;


import com.example.secondKill.dao.GoodsDao;
import com.example.secondKill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired(required = false)
    GoodsDao goodsDao;

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }


    public boolean reduceStock(long goodsId) {
        int result = goodsDao.reduceStock(goodsId);
        return result > 0;
    }
}
