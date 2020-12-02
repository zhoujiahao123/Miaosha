package com.example.secondKill.redis;

public class MiaoshaKey extends BasePrefix{
    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MiaoshaKey getMiaoshaGoodsIsOver(){return new MiaoshaKey(0,"go");}
}
