package com.spj.easychat.common.codec;

import com.spj.easychat.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public MsgEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)){
            byte[] buf =SerializationUtil.serialize(o);
            byteBuf.writeInt(buf.length);
            byteBuf.writeBytes(buf);
        }
    }
}
