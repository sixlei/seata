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
package org.apache.seata.core.rpc.netty.v0;

import io.netty.buffer.ByteBuf;
import org.apache.seata.core.protocol.MessageTypeAware;

/**
 * The interface Message codec.
 *
 */
public interface MessageCodecV0<T> extends MessageTypeAware {

    /**
     * Encode byte [ ].
     *
     * @return the byte [ ]
     */
    byte[] encode();

    /**
     * Decode boolean.
     *
     * @param in the in
     * @return the boolean
     */
    boolean decode(ByteBuf in);

    boolean decode(ByteBuf in, T req);
}
