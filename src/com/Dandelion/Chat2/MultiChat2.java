package com.Dandelion.Chat2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*ChatRoom Online
 * 实现一个客户可以正常收发信息*/
public class MultiChat2 {
    public static void main(String[] args) throws IOException {
        System.out.println("----server----");
        //1.指定端口 使用ServerSocket(8888);
        ServerSocket server = new ServerSocket(8888);
        //2.阻塞式等待连接accept()
        while (true) {
            Socket client = server.accept();
            System.out.println("一个客户端建立了连接");
           new Thread(new Channel(client)).start();
        }
    }
    static class Channel implements Runnable{
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket client;
        private boolean isRunning;

        public Channel(Socket client){
            this.client = client;
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                isRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
                release();
            }
        }
        //接收消息
        private String receive(){
            String msg="";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("---receiveError---");
                release();
            }
            return msg;
        }
        //发送消息
        private void send(String msg) {
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("---sendError---");
                release();
            }
        }
        //释放资源
        private void release(){
            this.isRunning = false;
            MyUtils.close(dis,dos,client);
        }

        @Override
        public void run() {
            while(isRunning){
                String msg = receive();
                if(!msg.equals("")){
                    send(msg);
                }
            }
        }
    }
}
