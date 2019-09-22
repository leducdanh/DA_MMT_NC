/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Properties;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author ThinkKING
 */
public class FileSV {

    private String checkSumValue;
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket, sendpacket;
    private OutputStream ouStream = null;
    private InputStream inStream = null;

//    public FileSV() throws SocketException, FileNotFoundException, IOException {
//        InetAddress addr = InetAddress.getByName("localhost");
//        this.serverSocket = new DatagramSocket(12345, addr);
//        //reading file
//        FileReader fr = new FileReader("Share/txt1.txt");
//        BufferedReader br = new BufferedReader(fr);
//        byte[] sendData = new byte[1024];
//        ServerSocket listen = new ServerSocket(12346);
//        Socket s = listen.accept();
//        ouStream = s.getOutputStream();
//
//        while (true) {
//            String Acc = br.readLine();
//            if (Acc == null) {
//                break;
//            }
//            sendData = Acc.getBytes();
//            this.sendpacket = new DatagramPacket(sendData, sendData.length, addr, 12345);
//            this.serverSocket.send(this.sendpacket);
//
//        }
//    }

    /**
     * @param args the command line arguments
     */ 
//    public final static String SERVER_IP = "127.0.0.70";
    public final static String SERVER_IP = "172.19.201.87";
    public final static int SERVER_PORT = 1027; // Cổng mặc định của Echo Server
    public final static byte[] BUFFER = new byte[4096]; // Vùng đệm chứa dữ liệu cho gói tin nhận

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, SocketException, IOException {
        ///////////////////////connect /////////////////////////////////////////
        // Địa chỉ máy chủ.
        final String serverHost = "localhost";
//        final String serverHost = "172.19.201.67";

        Socket socketOfClient = null;

        try {
            // Gửi yêu cầu kết nối tới Server đang lắng nghe
            // trên máy 'localhost' cổng 9999.
            socketOfClient = new Socket(serverHost, 9999);
//            socketOfClient = new Socket(serverHost, 9859);;

            ObjectOutputStream oos = new ObjectOutputStream(socketOfClient.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socketOfClient.getInputStream());
            File folder = new File("Share");
            oos.writeObject("filescv");
            String rep = (String) ois.readObject();
            if (rep.equals("filescv")) {
                ArrayList<String> lst = new ArrayList<String>();
                for (File f : folder.listFiles()) {
                    lst.add(SERVER_IP + ":" + SERVER_PORT + ":" + f.getName());
                }
                oos.writeObject(lst);
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }

        /////////////////////////////////////////////////////////////////
        // tao ket noi
        DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
        //thong bao server da san sang ket noi
        System.out.println("Server is now already");
        //tao mang byte de chua du lieu gui len tu client
        byte inFromClient1[];
        inFromClient1 = new byte[256];
        // lay kich thuoc mang
        int leng1 = inFromClient1.length;
        while (true) {

            DatagramPacket fromClient1 = new DatagramPacket(inFromClient1, leng1);
            // nhan goi ve server
            serverSocket.receive(fromClient1);
//            fr.close();
            
            MyThread th = new MyThread(SERVER_IP, SERVER_PORT, fromClient1, serverSocket);
            th.run();
            /////////////////////////
        }
    }

}
