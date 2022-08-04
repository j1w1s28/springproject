<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="include/ajaxFile.jsp"%>
<%@include  file="include/jquery.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script type="text/javascript"
	src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.5/sockjs.min.js">	</script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
	
<title>GIF 채팅방</title>
</head>
<body>
	<div id="connectwebsocket">
		<input type="button" onclick="connect()" value="연결하기">
	</div>
	<div id="messageArea" class="messageArea" hidden="hidden">
		채팅창 <input id="message" type="text" onclick="" value=""> <input
			type="button" onclick="sendMessage()" value="전송하기">
		<div id="chatBox"></div>
	</div>
	<div class="messageArea" hidden="hidden">
		<input type="button" onclick="onClose()" value="채팅창 나가기">
	</div>
	<script>
//웹소켓
let websocket;


//입장 버튼을 눌렀을 때 호출되는 함수
function connect() {
	// 웹소켓 주소
	//var wsUri = "http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/test2"
   var wsUri = "http://localhost:8080/shop/test2";
    // 소켓 객체 생성
    websocket = new SockJS(wsUri);
    console.log(websocket);
    var client = Stomp.over(websocket);
    console.log(client);
	client.connect({},function(){
		setConnected(true);
		console.log("connected stomp test");
		//controller messagemapping,header,message(자유형식)
		client.send('/greeting',{},"msg:hahahah");
		
		//해당 토픽을 구독한다.
		client.subscribe('/topic/message',function(event){
			console.log("event>>>>>>",event)
		});
	});
    //웹 소켓에 이벤트가 발생했을 때 호출될 함수 등록
    websocket.onopen = onOpen;
    websocket.onmessage = onMessage;
    websocket.onclose = onClose; 
}
function onConnected(){
}
function onError(){
	console.log('onError');
}
function onMessageReceived(){
	console.log('onMessageReceived');
}

//웹 소켓에 연결되었을 때 호출될 함수
function onOpen() {
	var sections = document.querySelectorAll(".messageArea");
	for (var i = 0; i < sections.length; i++) {
		  const item = sections.item(i);
		  item.hidden = false;
		}
	document.querySelector("#connectwebsocket").hidden = true;
	
}

// * 1 메시지 전송
function sendMessage(){
	var senderid = '<%=(String)session.getAttribute("data")%>';
	msg = 'msg,' + senderid + ',shin'; 
	msg += ','+document.querySelector("#message").value;
	console.log(msg);
	websocket.send(msg);
	document.querySelector("#message").value="";
}

// * 2 메세지 수신
function onMessage(msg) {
	var data = msg.data; //전달받은 데이터
	$("#messageArea").append("<p>"+ data + "<p/>");	
}
// * websocket 연결 끊기
function onClose(){
	var sections = document.querySelectorAll(".messageArea");
	for (var i = 0; i < sections.length; i++) {
		  const item = sections.item(i);
		  item.hidden = true;
		}
	document.querySelector("#connectwebsocket").hidden = false;
	var str = "님이 퇴장하셨습니다."
	websocket.send(str);
	websocket.close();
	
}

</script>
</body>
</html>