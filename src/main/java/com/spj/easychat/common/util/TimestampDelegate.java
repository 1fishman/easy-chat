package com.spj.easychat.common.util;
import java.io.IOException;
import java.sql.Timestamp;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Pipe;
import io.protostuff.WireFormat.FieldType;
import io.protostuff.runtime.Delegate;


public class TimestampDelegate implements Delegate<Timestamp> {


    @Override
    public FieldType getFieldType() {
        return FieldType.FIXED64;
    }

    @Override
    public Class<?> typeClass() {
        return Timestamp.class;
    }

    @Override
    public Timestamp readFrom(Input input) throws IOException {
        return new Timestamp(input.readFixed64());
    }

    @Override
    public void writeTo(Output output, int number, Timestamp value,
                        boolean repeated) throws IOException {
        output.writeFixed64(number, value.getTime(), repeated);
    }

    @Override
    public void transfer(Pipe pipe, Input input, Output output, int number,
                         boolean repeated) throws IOException {
        output.writeFixed64(number, input.readFixed64(), repeated);
    }
}