package com.redis.examples.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "redis.config")
public class RedisProperties {

    private String username;
    private String password;
    private Duration timeout;
    private Duration ttl;
    private List<String> nodes;
    private int maxTotalPool;
    private int maxIdlePool;
    private int minIdlePool;
    private Duration maxWait;


    public <T> GenericObjectPoolConfig<T> getGenericObjectPoolConfig() {
        GenericObjectPoolConfig<T> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxTotalPool);
        poolConfig.setMaxIdle(maxIdlePool);
        poolConfig.setMinIdle(minIdlePool);
        poolConfig.setMaxWait(maxWait);
        poolConfig.setTestWhileIdle(true);               // Включить проверку простаивающих соединений перед выдачей клиенту
        poolConfig.setNumTestsPerEvictionRun(maxIdlePool); // Количество проверяемых соединений за одну итерацию чистки
        poolConfig.setTimeBetweenEvictionRuns(ttl); // Интервал периодической очистки
        poolConfig.setSoftMinEvictableIdleDuration(ttl); // Минимальное время простого соединения
        return poolConfig;
    }

    public int getMaxTotalPool() {
        return maxTotalPool;
    }

    public void setMaxTotalPool(int maxTotalPool) {
        this.maxTotalPool = maxTotalPool;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public int getMaxIdlePool() {
        return maxIdlePool;
    }

    public void setMaxIdlePool(int maxIdlePool) {
        this.maxIdlePool = maxIdlePool;
    }

    public int getMinIdlePool() {
        return minIdlePool;
    }

    public void setMinIdlePool(int minIdlePool) {
        this.minIdlePool = minIdlePool;
    }

    public Duration getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Duration maxWait) {
        this.maxWait = maxWait;
    }
}
