/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author ThinkKING
 */
public class Client {

    private DatagramSocket clientSocket;
    private DatagramPacket sendPacket, receivePacket;
    private InputStream inStream = null;
    private String checkSumValue;
    private InetAddress addr;

    public Client() throws SocketException, UnknownHostException, IOException {
//        this.addr = InetAddress.getByName("localhost");
        this.clientSocket = new DatagramSocket(12345);
        byte[] buf = new byte[1024];
        this.receivePacket = new DatagramPacket(buf, 1024);
        byte[] receiveByte = this.receivePacket.getData();
        String str = new String(receiveByte, 0, this.receivePacket.getLength());
        System.out.println(str);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        ////////////////Connect MasterSV/////////////////
        // ip master.
//        final String serverHost = "localhost";
        final String serverHost = "172.19.201.67";

        Socket socketOfClient = null;
        
        String IpAndPortFileSV = "";
        ArrayList<String> lstFIle = new ArrayList<String>();

        try {
            // request to 'localhost' port 9999.
//            socketOfClient = new Socket(serverHost, 9999);
            socketOfClient = new Socket(serverHost, 9859);

            ObjectOutputStream oos = new ObjectOutputStream(socketOfClient.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socketOfClient.getInputStream());
            
            oos.writeObject("client");
            lstFIle = (ArrayList<String>) ois.readObject();
            System.out.println(lstFIle);
//            String rep = (String) ois.readObject();
//            if (rep.equals("client")) {
//                oos.writeObject("txt2.txt");
//                IpAndPortFileSV = (String) ois.readObject();
//                System.out.println(IpAndPortFileSV);
//            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }

        ///////////////////////////////////////////////
        //create socket client
        DatagramSocket ClientSocket = new DatagramSocket();
        System.out.println("Connected to File server");

        //nhap yeu cau tu nguoi dung
        DataInputStream inFromUser = new DataInputStream(System.in);
        int a, b, tong;
        try {

            //Khai bao mang byte de chua du lieu gui di server
            byte outToServer1[];
            //dia chi may chu
            InetAddress address = InetAddress.getByName(IpAndPortFileSV.split("-")[0]);
            // so port
            int port = Integer.parseInt(IpAndPortFileSV.split("-")[1]);
            //chuyen kieu du lieu : String -> byte va dua vao mang byte da khai bao o tren
            outToServer1 = "txt2.txt".getBytes();
            //lay kich thuoc cua mang
            int leng1 = outToServer1.length;
//            int leng2 = outToServer2.length;
            // tao goi de gui di
            DatagramPacket toServer1 = new DatagramPacket(outToServer1, leng1, address, port);
//            DatagramPacket toServer2 = new DatagramPacket(outToServer2, leng2, address, port);
            // gui goi len server
            ClientSocket.send(toServer1);
//            ClientSocket.send(toServer2);
            //tao goi de nhan du lieu ve
            byte inFromServer[];
            inFromServer = new byte[1024];

            String data = "";
            while (!data.equals("the_end")) {
                // tao goi nhan du lieu ve
                DatagramPacket fromServer = new DatagramPacket(inFromServer, 1024);
                // nhan goi tra ve tu server
                ClientSocket.receive(fromServer);
                // dua du lieu tu mang byte vao bien data, lay tu vi tri so 0.
                data = (new String(fromServer.getData(), 0, fromServer.getLength())).trim();

                System.out.println("Ket Qua :" + data);
            }

            //in ket qua ra man hinh
            ClientSocket.close();

        } catch (UnknownHostException e) {
            System.out.println("Server Not Found");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot connect to server");
            System.exit(1);
        }
    }

}
