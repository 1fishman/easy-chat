package com.spj.easychat.server.redis;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.spj.easychat.common.entity.CommandEnum;
import com.spj.easychat.common.entity.CommonMessage;
import com.spj.easychat.common.entity.Message;
import com.spj.easychat.common.util.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class RedisService {

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    JedisPool jedisPool;


    /**
     *  向redis中存储对象
     */
    public <T> boolean set(String key, T value){
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            byte[] str = SerializationUtil.serialize(value);
            String realKey = key;
            jedis.set(realKey, Arrays.toString(str));
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    public List<CommonMessage> getAllCacheList(String key){
        Jedis jedis = null;
        try {
            lock.readLock().lock();
            jedis = jedisPool.getResource();
            List<byte[]> ls = jedis.lrange(key.getBytes(),0,-1);
            List<CommonMessage> res = new ArrayList<>(ls.size());
            for (byte [] bytes : ls){
                res.add(SerializationUtil.deserialie(bytes,CommonMessage.class));
            }
            return res;
        }finally {
            lock.readLock().unlock();
            returnToPool(jedis);
        }
    }

    public List<CommonMessage> getEarlyCacheList(String key){
        Jedis jedis = null;
        try {
            lock.writeLock().lock();
            jedis = jedisPool.getResource();
            List<byte[]> ls = jedis.lrange(key.getBytes(),20,-1);
            jedis.ltrim(key.getBytes(),0,20);
            List<CommonMessage> res = new ArrayList<>(ls.size());
            for (byte [] bytes : ls){
                res.add(SerializationUtil.deserialie(bytes,CommonMessage.class));
            }
            return res;
        }finally {
            lock.writeLock().unlock();
            returnToPool(jedis);
        }
    }

    public void lpush(String key,CommonMessage message){
        Jedis jedis = null;
        try {
            lock.readLock().lock();
            jedis = jedisPool.getResource();
            jedis.lpush(key.getBytes(), SerializationUtil.serialize(message));
        }finally {
            lock.readLock().unlock();
            returnToPool(jedis);
        }
    }




    public void returnToPool(Jedis jedis){
        if (jedis != null){
            jedis.close();
        }
    }


}
