package com.laoxing.skill.chat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-28 15:18
 */
@ServerEndpoint("/api/chat/{nickname}")
@Component
@Scope(scopeName = "prototype") //设置IOC的 创建Bean 默认：单例 改为：多例
public class WebSocketServer {
    public WebSocketServer(){
        System.out.println("服务端构造");
    }
    //实例化集合 存储目前在线的聊天对象信息
    public static ConcurrentHashMap<String,WebSocketServer> map=new ConcurrentHashMap<>();
    //记录当前的会话对象
    private Session session;
    //昵称
    private String nickname;
    //接收  连接消息
    @OnOpen
    public void open(Session session,@PathParam("nickname") String nickname) throws IOException {
        if(map.containsKey(nickname)){
            session.getBasicRemote().sendText("亲，昵称已存在！");
            session.close();
        }else {
            System.out.println("连接：" + nickname);
            this.session = session;
            this.nickname = nickname;
            map.put(nickname,this);
        }
    }
    //接收 传输消息
    @OnMessage
    public void message(String msg,Session session) throws IOException {
        System.out.println("接收消息："+msg);
        batchMsg(msg);
        //session.getBasicRemote().sendText("Over："+System.currentTimeMillis()/1000);
    }
    //错误信息
    @OnError
    public void error(Session session,Throwable throwable){
        System.out.println("崩了"+throwable.getMessage());
    }
    //接收 关闭连接
    @OnClose
    public void close(Session session) throws IOException {
        System.out.println("关闭");
        map.remove(nickname);
        //告诉别人 谁谁  下线了
        batchMsg(nickname+" 下线啦");
    }

    //群发消息
    private void batchMsg(String msg) throws IOException {
        for(String k:map.keySet()){
            if(!k.equals(nickname)){
                map.get(k).session.getBasicRemote().sendText(nickname+"-说："+msg);
            }
        }
    }
}