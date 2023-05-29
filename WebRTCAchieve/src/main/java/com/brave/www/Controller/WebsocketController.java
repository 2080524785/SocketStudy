package com.brave.www.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brave.www.manager.SocketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/websocket/{id}")
@Controller
@Slf4j
public class WebsocketController {
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "id") String id) {
        //获取连接的用户
        log.info("加入session:" + id );
        SocketManager.setSession(id, session);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("移除不用的session:" + session.getId());
        SocketManager.removeSession(session.getId());
    }


    //收到客户端信息
    @OnMessage
    public void onMessage(String message,Session session) throws IOException {
        log.info("message,{}",message);
        JSONObject jsonObject = JSON.parseObject(message);
        SocketManager.sendMessage(jsonObject.getString("toUserId"),jsonObject.toJSONString());
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("webSocket 错误,{}", throwable.getMessage());
        SocketManager.removeSession(session.getId());
    }
}
