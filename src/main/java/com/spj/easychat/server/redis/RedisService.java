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

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取对象
     */
    public <T> T get(String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            String realKey = key;
            String value = jedis.get(realKey);
            return stringToBean(value,clazz);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     */
    public boolean exists(String key){
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            return jedis.exists(key);
        }finally {
            returnToPool(jedis);
        }
    }


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

    /**
     * 使得对象加1
     */
    public Long incr(String key){
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            String realKey =key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public List<CommonMessage> getCacheList(String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<byte[]> ls = jedis.lrange(key.getBytes(),0,-1);
            jedis.del(key.getBytes());
            List<CommonMessage> res = new ArrayList<>(ls.size());
            for (byte [] bytes : ls){
                res.add(SerializationUtil.deserialie(bytes,CommonMessage.class));
            }
            return res;
        }finally {
            returnToPool(jedis);
        }
    }

    public void lpush(String key,CommonMessage message){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key.getBytes(), SerializationUtil.serialize(message));
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 是对象减1
     */
    public Long decr(String key){
        Jedis jedis = null;
        try {

            jedis =  jedisPool.getResource();
            String realKey = key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }



    /**
     * json形式序列化对象
     */
    public <T> String beanToString(T value){
        if (value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class){
            return ""+value;
        }else if (clazz == Long.class || clazz == long.class){
            return ""+value;
        }else if(clazz == String.class){
            return ""+value;
        }else{
            return JSON.toJSONString(value);
        }
    }



    /**
     * 反序列化对象
     */
    @SuppressWarnings("unchecked")
    public <T> T stringToBean(String str,Class<T> clazz){
        if (str == null || str.length() == 0 || clazz == null){
            return null;
        }
        if (clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if (clazz == Long.class || clazz == long.class){
            return (T)Long.valueOf(str);
        }else if(clazz == String.class){
            return (T)str;
        }else{
            return SerializationUtil.deserialie(str.getBytes(),clazz);
        }

    }

    public void returnToPool(Jedis jedis){
        if (jedis != null){
            jedis.close();
        }
    }

    public static void main(String[] args) {

        CommonMessage message = new CommonMessage(1, CommandEnum.LOGIN);
        byte[] arr = SerializationUtil.serialize(message);
        SerializationUtil.deserialie(arr,CommonMessage.class);

        long time = System.currentTimeMillis();

        time = System.currentTimeMillis();
        for (int i = 1; i < 20000000; i++){
            CommonMessage message1 = new CommonMessage(1, CommandEnum.LOGIN);
            byte[] bytes = SerializationUtil.serialize(message);
            //message = SerializationUtil.deserialie(bytes,message.getClass());
        }
        System.out.println(System.currentTimeMillis()-time);
    }

}
