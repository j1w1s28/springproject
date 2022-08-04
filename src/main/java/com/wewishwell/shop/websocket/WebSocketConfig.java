package com.wewishwell.shop.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
	//메시지 브로커가 /topic/이 들어가는 구독자들에게 메시지를 전달해준다. 		
			registry.enableSimpleBroker("/sub"); 		 		
			
			//클라이언트가 서버에게 /shop 을 붙이고 메시지를 전달할 주소 		
			registry.setApplicationDestinationPrefixes("/pub"); 	
}
@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
	//클라이언트가 서버에 접속할 Endpoint를설정한다.엔드포인트는 여러개 추가가능하다 . 		
	//client에서 Websocket대신 향상된 SockJS로 접속하려면 .withSockJS.()를 붙여준다.! 		
	registry.addEndpoint("/shop/test2").setAllowedOrigins("http://localhost:8080").withSockJS(); 
	}
}
