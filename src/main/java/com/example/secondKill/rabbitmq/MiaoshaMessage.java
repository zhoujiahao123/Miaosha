package com.example.secondKill.rabbitmq;

import com.example.secondKill.domain.MiaoshaUser;

import java.io.Serializable;

public class MiaoshaMessage implements Serializable {
    private MiaoshaUser user;
    private long goodsId;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "MiaoshaMessage{" +
                "user=" + user +
                ", goodsId=" + goodsId +
                '}';
    }
}
