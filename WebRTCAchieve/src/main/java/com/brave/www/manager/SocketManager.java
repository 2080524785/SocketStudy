package com.example.demo.manager;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket管理
 */
@Slf4j
public class SocketManager {

    /**
     * 客户端连接session集合 <id,Session></>
     */
    private static Map<String,Session> sessionMap = new ConcurrentHashMap<String,Session>();

    /**
     * 存放新加入的session 便于以后对session管理
     * @param userId 用户id
     * @param session 用户session
     */
    public synchronized static void setSession(String userId,Session session){
        sessionMap.put(userId,session);
    }

    /**
     * 根据session ID移除session
     * @param sessionId
     */
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

    /**
     * 给用户发送消息
     * @param userId 用户id
     * @param message 消息
     */
    public  static void sendMessage(String userId, String message) {
        log.info("给用户发送消息,{},{}",userId,message);
        Session session = sessionMap.get(userId);
        if(session != null){
            synchronized (session){
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.info("发送websocket消息是异常,{}",e);
                }
            }
        }
    }
}