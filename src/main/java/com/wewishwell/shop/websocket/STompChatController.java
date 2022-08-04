package com.wewishwell.shop.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.TextMessage;

@Controller
public class STompChatController {

	private final SimpMessagingTemplate template = null;

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(TextMessage message){
        template.convertAndSend("/sub/chat/room/" + message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(TextMessage message){
        template.convertAndSend("/sub/chat/room/" + message);
    }
}
