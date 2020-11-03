package com.example.secondKill.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();


}
