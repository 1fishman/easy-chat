package com.spj.easychat.common.codec;

import com.spj.easychat.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MsgDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public MsgDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes()<4){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0){
            ctx.close();
        }
        if (in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = SerializationUtil.deserialie(data,genericClass);
        list.add(obj);
    }
}
