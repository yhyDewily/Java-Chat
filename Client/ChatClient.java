package demo.chat.Client;

import demo.chat.Server.ChatServer;
import demo.chat.jdbc.JdbcUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatClient extends Frame {
    private String clientname = "";
    private TextArea ta = new TextArea();
    private TextField tf = new TextField();
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private Socket socket = null;
    private boolean bConnected = false;
    private Thread thread=null;
    private List<String> userports = new ArrayList<String>();
//    public static void main(String[] args) {
//        new ChatClient().frameClient("Dewily");
//    }
    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }


    //客户端窗口
    public void frameClient(String name){
        this.setClientname(name);
        setLayout(null);

        //系统消息框标签
        Label lb1 = new Label("--------Message Box--------");
        lb1.setBounds(10, 55, 300, 20);

        //系统消息框
        ta.setBackground(Color.GRAY);
        ta.setEditable(false);
        ta.setBounds(10, 80, 400 ,400);


        //当前在线好友列表标签
        Label lb2 = new Label("--------Online List--------");
        lb2.setBounds(450, 55, 400 ,20);

        //好友列表框
        final Button Bshow = new Button("Show Online");
        Bshow.setBounds(450, 80, 200, 20);
        Bshow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        //发送框
        tf.setEditable(true);
        tf.setBounds(10, 500, 400, 20);
        tf.addActionListener(new TfListener());

        //发送按钮
        final Button Bsend = new Button("Send");
        Bsend.setBounds(450, 500, 150, 20);
        Bsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = tf.getText();
                tf.setText("");
                try {
                    if(str!=null && !str.trim().equals("")){
                        dos.writeUTF(getClientname() +  ">>" + str);
                        dos.flush();
                    }
                    else {
                        dos.writeUTF("发送信息不能为空");
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        add(lb1);
        add(lb2);
        add(ta);
        add(tf);
        add(Bsend);
        add(Bshow);

        setSize(700, 600);
        setVisible(true);
        setResizable(false);
        setTitle(name + "——Chat Client");
        this.connect();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnected();
                setLogout();
                System.exit(0);
            }
        });

    }


    //链接服务器地址
    private void connect(){
        try {
            socket = new Socket("127.0.0.1", 8888);
            thread=new Thread(new ChatThread());  //开启一个客户端线程
            thread.start();
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            setLogout();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            setLogout();
        }
    }
    //断开连接
    private void disconnected(){
        bConnected = false;
        try {
            dos.close();
            dis.close();
            socket.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    //登录状态置0
    private void setLogout(){
        try {
            Connection conn = JdbcUtils.getConncetion();
            PreparedStatement ps = null;
            String sql = "UPDATE chatuser SET login_status = FALSE WHERE name = ?" ;
            ps = conn.prepareStatement(sql);
            ps.setString(1, clientname);
            ps.execute();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    //键盘回车事件
    private class TfListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String strMsg = tf.getText();
            tf.setText("");
            try {
                if(strMsg!=null && !strMsg.trim().equals("")){
                    dos.writeUTF(getClientname() +  ">>" + strMsg);
                    dos.flush();
                }
                else {
                    dos.writeUTF("发送信息不能为空");
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }
    //开启线程接受服务器信息
    private class ChatThread implements Runnable{
        @Override
        public void run() {
            try {
                bConnected = true;
                while(bConnected){
                    String msg = dis.readUTF();
                    String taText = ta.getText();
                    ta.setText(taText+msg+"\n");
                }
            } catch (SocketException e) {
                System.out.println("退出");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}