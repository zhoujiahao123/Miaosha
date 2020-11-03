package com.example.secondKill.redis;

public class OrderKey extends BasePrefix {
    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    private OrderKey(String prefix) {
        super(prefix);
    }
}
