package com.github.emailtohl.building.site.chat;

import com.github.emailtohl.building.websocket.ObjectCoder;
/**
 * 简单地继承com.github.emailtohl.building.websocket.ObjectCoder<T extends Serializable>即可
 * 实际上就是告诉ObjectCoder要序列化的类，本例是ChatMessage
 * @author HeLei
 * @date 2017.02.04
 */
public class ChatMessageCoder extends ObjectCoder<ChatMessage> {
}
