/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.serializer.seata.protocol;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import org.apache.seata.serializer.seata.MessageCodecFactory;
import org.apache.seata.serializer.seata.MessageSeataCodec;
import org.apache.seata.core.protocol.AbstractMessage;
import org.apache.seata.core.protocol.AbstractResultMessage;
import org.apache.seata.core.protocol.MergeResultMessage;

/**
 * The type Merge result message codec.
 *
 */
public class MergeResultMessageCodec extends AbstractMessageCodec {

    private byte version;

    public MergeResultMessageCodec(byte version) {
        this.version = version;
    }
    @Override
    public Class<?> getMessageClassType() {
        return MergeResultMessage.class;
    }

    @Override
    public <T> void encode(T t, ByteBuf out) {
        MergeResultMessage mergeResultMessage = (MergeResultMessage)t;
        AbstractResultMessage[] msgs = mergeResultMessage.getMsgs();
        int writeIndex = out.writerIndex();
        out.writeInt(0);
        out.writeShort((short)msgs.length);
        for (AbstractMessage msg : msgs) {
            //get messageCodec
            short typeCode = msg.getTypeCode();
            //put typeCode
            out.writeShort(typeCode);
            MessageSeataCodec messageCodec = MessageCodecFactory.getMessageCodec(typeCode, version);
            messageCodec.encode(msg, out);
        }

        int length = out.readableBytes() - 4;
        out.setInt(writeIndex,length);
        if (msgs.length > 20) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("msg in one services merge packet:" + msgs.length + ",buffer size:" + length);
            }
        }
    }

    @Override
    public <T> void decode(T t, ByteBuffer in) {
        MergeResultMessage mergeResultMessage = (MergeResultMessage)t;

        if (in.remaining() < 4) {
            return;
        }
        int length = in.getInt();
        if (in.remaining() < length) {
            return;
        }
        byte[] buffer = new byte[length];
        in.get(buffer);
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        decode(mergeResultMessage, byteBuffer);
    }

    /**
     * Decode.
     *
     * @param mergeResultMessage the merge result message
     * @param byteBuffer         the byte buffer
     */
    protected void decode(MergeResultMessage mergeResultMessage, ByteBuffer byteBuffer) {
        //msgs size
        short msgNum = byteBuffer.getShort();
        AbstractResultMessage[] msgs = new AbstractResultMessage[msgNum];
        for (int idx = 0; idx < msgNum; idx++) {
            short typeCode = byteBuffer.getShort();
            AbstractMessage abstractResultMessage = MessageCodecFactory.getMessage(typeCode);
            MessageSeataCodec messageCodec = MessageCodecFactory.getMessageCodec(typeCode, version);
            messageCodec.decode(abstractResultMessage, byteBuffer);
            msgs[idx] = (AbstractResultMessage)abstractResultMessage;
        }
        mergeResultMessage.setMsgs(msgs);
    }

}
