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

    /**
     * @param args the command line arguments
     */
//    public final static String SERVER_IP = "127.0.0.70";
    public static String SERVER_IP = "";
    public static int SERVER_PORT; // Cổng mặc định của Echo Server
    public static String MASTERSERVER_IP = "";
    public static int MASTERSERVER_PORT; // Cổng mặc định của master Server
    public final static byte[] BUFFER = new byte[4096]; // Vùng đệm chứa dữ liệu cho gói tin nhận

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, SocketException, IOException {
        ///////////////////////init///////////////////////////
        FileReader fr = new FileReader("Config.txt");
        BufferedReader br = new BufferedReader(fr);
        String str = br.readLine();
        MASTERSERVER_IP = str;
        str = br.readLine();
        MASTERSERVER_PORT = Integer.parseInt(str);
        str = br.readLine();
        SERVER_IP = str;
        str = br.readLine();
        SERVER_PORT = Integer.parseInt(str);
///////////////////////connect /////////////////////////////////////////
        // Địa chỉ máy chủ.

        Socket socketOfClient = null;

        try {
            // Gửi yêu cầu kết nối tới Server đang lắng nghe
            socketOfClient = new Socket(MASTERSERVER_IP, MASTERSERVER_PORT);

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
            System.err.println("Don't know about host " + MASTERSERVER_IP);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + MASTERSERVER_IP);
            return;
        }

        /////////////////////////////////////////////////////////////////
        // tao ket noi
        DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
        //thong bao server da san sang ket noi
        System.out.println("Server is now already");

        while (true) {
//tao mang byte de chua du lieu gui len tu client
            byte inFromClient1[] = new byte[256];
            DatagramPacket fromClient1 = new DatagramPacket(inFromClient1, 256);
            // nhan goi ve server
            serverSocket.receive(fromClient1);

            MyThread th = new MyThread(SERVER_IP, SERVER_PORT, fromClient1, serverSocket);
            th.run();
            /////////////////////////
        }
    }

}
