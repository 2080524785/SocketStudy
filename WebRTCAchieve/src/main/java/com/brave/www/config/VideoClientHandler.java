package com.brave.www.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class VideoClientHandler implements WebSocketHandler {
    private static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        log.info("afterConnectionEstablished,{}",webSocketSession.getRemoteAddress());
        URI uri = webSocketSession.getUri();
        String query = uri.getQuery();
        String userId = query.substring(query.indexOf("=") + 1);
        log.info("query userId,{},{}", query, userId);
        setSession(userId,webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        Object messagePayload = webSocketMessage.getPayload();
        log.info("handleMessage,{}", messagePayload);
        if(null == messagePayload){
            return;
        }
        JSONObject jsonObject = JSON.parseObject(messagePayload.toString());
        sendMessage(jsonObject.getString("toUserId"),jsonObject.toJSONString());

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.info("handleTransportError");
        removeSession(webSocketSession.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.info("afterConnectionClosed");
        removeSession(webSocketSession.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

// 用于对session进行管理
    public synchronized static void setSession(String userId,WebSocketSession session){
        sessionMap.put(userId,session);
    }
    public static void removeSession(String sessionId){
        if(null == sessionId){
            return;
        }
        sessionMap.forEach((k,v) -> {
            if(sessionId.equals(v.getId())){
                sessionMap.remove(k);
            }
        });
    }
    public  static void sendMessage(String userId, String message) {
        log.info("给用户发送消息,{},{}",userId,message);
        WebSocketSession session = sessionMap.get(userId);
        if(session != null){
            synchronized (session){
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.info("发送websocket消息是异常,{}",e);
                }
            }
        }
    }

}
