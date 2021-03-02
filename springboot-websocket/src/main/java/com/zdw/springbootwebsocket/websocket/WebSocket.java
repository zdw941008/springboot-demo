package com.zdw.springbootwebsocket.websocket;


import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhoudw
 * 2021-03-01 14:53.
 */

@ServerEndpoint("/websocket/{pageCode}")
@Component
public class WebSocket {

    private static final String loggerName=WebSocket.class.getName();
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static Map<String, List<Session>> electricSocketMap = new ConcurrentHashMap<String, List<Session>>();


    /**
     * 连接建立成功调用的方法
     * @param pageCode
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("pageCode") String pageCode,Session session){
        List<Session> sessions = electricSocketMap.get(pageCode);
        if (null==sessions){
            List<Session> sessionsList = new ArrayList<>();
            sessionsList.add(session);
            electricSocketMap.put(pageCode,sessionsList);
        }else {
            sessions.add(session);
        }
    }


    /**
     * 连接关闭调用的方法
     * @param pageCode
     * @param session
     */
    @OnClose
    public void onClose(@PathParam("pageCode") String pageCode,Session session){
        if (electricSocketMap.containsKey(pageCode)){
            electricSocketMap.get(pageCode).remove(session);
        }
    }


    /***
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("websocket 收到消息："+message);
        try {
            //向客户端发送消息
            session.getBasicRemote().sendText("这是推送测试数据！您刚发送的消息是："+message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误"+error.getMessage());
    }


}
