package main.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Servers {
    //将接收到的socket变成一个集合
    protected static List<Socket> sockets = new Vector<>();

    public static void main(String[] args) throws IOException {
        //创建服务端
        ServerSocket server = new ServerSocket(5200);
        boolean flag = true;
        //接受客户端请求
        while (flag){
            try {
                //阻塞等待客户端的连接
                Socket accept = server.accept();
                synchronized (sockets){
                    sockets.add(accept);
                }
                //多个服务器线程进行对客户端的响应
                Thread thread = new Thread(new ServerThead(accept));
                thread.start();
                //捕获异常
            }catch (Exception e){
                flag = false;
                e.printStackTrace();
            }
        }
        //关闭服务器
        server.close();
    }

}