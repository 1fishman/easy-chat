package com.spj.easychat.common.util;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.DefaultIdStrategy;
import io.protostuff.runtime.Delegate;
import io.protostuff.runtime.RuntimeEnv;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import javax.xml.validation.SchemaFactory;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationUtil {

    /** 时间戳转换Delegate，解决时间戳转换后错误问题 @author jiujie 2016年7月20日 下午1:52:25 */
    private final static Delegate<Timestamp> TIMESTAMP_DELEGATE = new TimestampDelegate();

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private final static DefaultIdStrategy idStrategy  = ((DefaultIdStrategy) RuntimeEnv.ID_STRATEGY);

    private static Objenesis objenesis = new ObjenesisStd(true);

    static {
        idStrategy.registerDelegate(TIMESTAMP_DELEGATE);
    }

    private SerializationUtil(){

    }


    public static <T> byte[] serialize(T obj){
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer bytebuf = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema = getSchema(clazz);
            return ProtobufIOUtil.toByteArray(obj,schema,bytebuf);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }finally {
            bytebuf.clear();
        }

    }

    /**
     * 反序列化
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T deserialie(byte [] data,Class<T> cls){
        Schema<T> schema = getSchema(cls);
        T message = schema.newMessage();
        ProtobufIOUtil.mergeFrom(data,message,schema);
        return message;
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls,idStrategy);
            cachedSchema.put(cls, schema);
        }
        return schema;
    }
}
