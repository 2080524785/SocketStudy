package com.example.main;


import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
//@Component
@Slf4j
public class WebSocket {
    private Session session;

    private String userId;

    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<WebSocket>();
    private static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value="uesrId")String userId){
        try {
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            sessionPool.put(userId,session);
            log.info("【websocket消息】有新的连接，总数为:"+webSockets.size());
        }catch (Exception e) {

        }
    }

    @OnClose
    public void onClose(){
        try{
            webSockets.remove(this);
            sessionPool.remove(this.userId);
            log.info("【websocket消息】连接断开，总数为:"+webSockets.size());

        }catch(Exception e){

        }
    }

    @OnMessage
    public void onMessage(String message){
        log.info("【websocket消息】收到客户端消息:"+message);
    }

    @OnError
    public void onError(Session session, Throwable error){
        log.error("用户错误,原因:"+error.getMessage());
        error.printStackTrace();
    }

    public void sendAllMessage(String message){
        log.info("【websocket消息】广播消息:"+message);
        for(WebSocket webSocket:webSockets){
            try{
                if(webSocket.session.isOpen()){
                    webSocket.session.getAsyncRemote().sendText(message);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void sendOneMessage(String userId,String message){
        Session session = sessionPool.get(userId);
        if(session != null&&session.isOpen()){
            try{
                log.info("【websocket消息】 单点消息:"+message);
                session.getAsyncRemote().sendText(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
    public void sendMoreMessage(String[] userIds, String message) {
        for(String userId:userIds) {
            Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                try {
                    log.info("【websocket消息】 单点消息:"+message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

