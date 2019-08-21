package com.spj.easychat.server.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        JedisPool jedisPool = new JedisPool(poolConfig,redisConfig.getHostname(),redisConfig.getPort()
                ,redisConfig.getPoolMaxWait()*1000);
        return jedisPool;
    }
}
