package com.example.secondKill.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {
    private String host;
    private int port;
    private int timeout;
    private int pollMaxIdle;
    private int pollMaxTotal;
    private int pollMaxWait;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getPollMaxIdle() {
        return pollMaxIdle;
    }

    public void setPollMaxIdle(int pollMaxIdle) {
        this.pollMaxIdle = pollMaxIdle;
    }

    public int getPollMaxTotal() {
        return pollMaxTotal;
    }

    public void setPollMaxTotal(int pollMaxTotal) {
        this.pollMaxTotal = pollMaxTotal;
    }

    public int getPollMaxWait() {
        return pollMaxWait;
    }

    public void setPollMaxWait(int pollMaxWait) {
        this.pollMaxWait = pollMaxWait;
    }
}
