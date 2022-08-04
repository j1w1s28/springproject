package com.wewishwell.shop.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Controller
public class WebSocketHandler extends TextWebSocketHandler {
	//메세지를 모든 사용자에게 보내기위해서 session을 관리 나중에는 map로 간다.
	List<WebSocketSession> sessions = new ArrayList<>();
	Map<String, WebSocketSession> userSessions = new HashMap<>();
	
	// 웹소켓 연결 성공시
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Map<String,Object> map = session.getAttributes();
		String senderID = getId(session);
		System.out.println("userID :" + senderID);
		System.out.println("afterConnectionEstablished :" + session);
		userSessions.put(senderID, session);
	}
	//웹소켓 연결 종료시
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		System.out.println("afterConnectionClosed : "+ session + " : "+ status);
	}
	
	//웹소켓 메세지 수신 및 송신
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("handleTextMessage : " + session + " : " + message );
		//메세지 받았을 때 누가 보냈는지 senderID로 저장
		String senderID = session.getId();
		System.out.println(senderID);
		System.out.println(message.getPayload());
		
		//모든사용자에게 메세지를 송신
		//  for(WebSocketSession sess : sessions) { 
			//  sess.sendMessage(new TextMessage(senderID + ":" + message.getPayload()));
			//  System.out.println("handler에서 보내는 msg :" + senderID + ":" + message.getPayload());
		 // }
		String msg = message.getPayload();
		String [] strs = msg.split(",");
		if(strs != null) {
			String cmd = strs[0];
			String sender = strs[1];
			String receiver = strs[2];
			String text = strs[3];
			WebSocketSession messageSession1 = userSessions.get(sender);
			WebSocketSession messageSession2 = userSessions.get(receiver);
			if(messageSession1 != null) {
				TextMessage tmpMessage = new TextMessage(sender+"님이 메세지를 보냈습니다. :"+ text);
				messageSession1.sendMessage(tmpMessage );
			}
			if(messageSession2 != null) {
				TextMessage tmpMessage = new TextMessage(sender+"님이 메세지를 보냈습니다. :"+ text);
				messageSession2.sendMessage(tmpMessage );
			}
		}
	}
	private String getId(WebSocketSession session) {
		Map<String, Object> httpSession = session.getAttributes();
		String User = (String) httpSession.get("data");
		if(User == null) {
			return session.getId();
		}else {
			return User;
		}
	}
	
 
}
