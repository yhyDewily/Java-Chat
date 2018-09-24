package demo.chat.Server;

import Enter.TestTreeSet;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class ChatServer extends Frame {
    private boolean started = false;
    private List<ChatThread> chatThreads = new ArrayList<ChatThread>();
    private TextArea ta = new TextArea();
    private TextArea ta2 = new TextArea();
    private TextField tf = new TextField();
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    public static void main(String[] args) {
        new ChatServer().startServer();
    }
    private void startServer(){
        setLayout(null);

        //系统消息提示框
        Label lb1 = new Label("--------System Message--------");
        lb1.setBounds(10, 55, 300 ,20);

        //系统消息提示框
        ta.setBackground(Color.GRAY);
        ta.setEditable(false);
        ta.setBounds(10, 80, 400, 400);

        //当前用户列表
        Label lb2 = new Label("--------Current User List--------");
        lb2.setBounds(450, 55, 300, 20);

        //当前用户列表框
        ta2.setBackground(Color.GRAY);
        ta2.setEditable(false);
        ta2.setBounds(450, 80, 200 ,400);

        //添加组件
        add(lb1);
        add(lb2);
        add(ta);
        add(ta2);
        setSize(700, 600);
        setVisible(true);
        setResizable(false);
        setTitle("Chat Server");

//        setSize(400,400);
//        setLocation(400,300);
//        add(ta, BorderLayout.NORTH);
//        add(tf, BorderLayout.SOUTH);
//        pack();
//        ta.setEditable(false);
//        setVisible(true);

        try {
            //开启服务端Socket
            ServerSocket seso = new ServerSocket(8888);
            started = true;
            ta.setText("服务器已开启，等待客户端连接");
            while(started){
                //接受客户端连接请求
                Socket sos = seso.accept();
                //开启线程处理客户端通信
                ChatThread ct = new ChatThread(sos);
                chatThreads.add(ct);
                new Thread(ct).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class ChatThread implements Runnable{
        private Socket socket;
        private DataInputStream din=null;
        private DataOutputStream don=null;
        private boolean bConnected = false;
        public ChatThread(Socket socket) {
            super();
            this.socket = socket;
        }
        //发送信息的函数
        private void send(String strMsgIn){
            try{
                don.writeUTF(strMsgIn);
                don.flush();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try{
                din = new DataInputStream(socket.getInputStream());
                don = new DataOutputStream(socket.getOutputStream());
                //读取数据
                bConnected = true;
                while(bConnected){
                    String strMsgIn = din.readUTF();
                    ta.append("\n" + strMsgIn + "\n");
                    //接收到数据后发送给每个客户端
                    for(int i =0;i<chatThreads.size();i++){
                        chatThreads.get(i).send(strMsgIn);
                    }
                }
            }catch (IOException e) {
                try {
                    //如果客户端出错或关闭，直接关闭连接，并移除List中的当前线程
                    socket.close();
                    chatThreads.remove(this);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } finally{
                try {
                    din.close();
                    don.close();
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }
}
