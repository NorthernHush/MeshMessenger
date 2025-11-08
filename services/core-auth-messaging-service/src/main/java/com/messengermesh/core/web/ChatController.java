package com.messengermesh.core.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    public static class ChatMessage {
        public String channelId;
        public String senderId;
        public String content;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/channel")
    public ChatMessage sendMessage(@Payload ChatMessage msg) {
        // In a real app save message to DB and publish to other instances
        return msg;
    }
}
