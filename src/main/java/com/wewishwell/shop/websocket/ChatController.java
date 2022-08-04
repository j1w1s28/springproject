package com.wewishwell.shop.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

	@MessageMapping("/greeting")
	@SendTo("/topic/message")
		public String handle(String message) throws Exception {
			System.out.println(message);
			return message;
		}
	@GetMapping("/test2")
		public String test2() {
		return"test2";
	}
}
