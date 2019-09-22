/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.Scanner;

/**
 *
 * @author ThinkKING
 */
public class Client {

    private static DatagramSocket ClientSocket;
    private DatagramPacket sendPacket, receivePacket;
    private InputStream inStream = null;
    private InetAddress addr;
    public static String MASTERSERVER_IP = "";
    public static int MASTERSERVER_PORT; // Cổng mặc định của master Server

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        ///////////////////////init///////////////////////////
        FileReader fr = new FileReader("Config.txt");
        BufferedReader br = new BufferedReader(fr);
        String str = br.readLine();
        MASTERSERVER_IP = str;
        str = br.readLine();
        MASTERSERVER_PORT = Integer.parseInt(str);

        Socket socketOfClient = null;

        String IpAndPortFileSV = "";
        ArrayList<String> lstFIle = new ArrayList<String>();

        try {
            socketOfClient = new Socket(MASTERSERVER_IP, MASTERSERVER_PORT);

            ObjectOutputStream oos = new ObjectOutputStream(socketOfClient.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socketOfClient.getInputStream());

            oos.writeObject("client");
            lstFIle = (ArrayList<String>) ois.readObject();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + MASTERSERVER_IP);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + MASTERSERVER_IP);
            return;
        }

        ///////////////////////////////////////////////
        //create socket client
        ClientSocket = new DatagramSocket();
        System.out.println("Connected to File server");

        while (true) {
            int count = 0;
            for (String item : lstFIle) {
                String[] tmp = item.split(":");
                String ip = tmp[0];
                String port = tmp[1];
                String nameFile = tmp[2];

                System.out.println(count + "/ " + nameFile + " | " + ip + ":" + port);
                count++;
            }

            System.out.print("Nhập số thứ tự file bạn muốn tải: ");
            Scanner sc = new Scanner(System.in);
            int num = sc.nextInt();
            //send size
            InetAddress inetAddress = InetAddress.getByName(lstFIle.get(num).split(":")[0]);
            DatagramPacket toServer = new DatagramPacket(lstFIle.get(num).split(":")[2].getBytes(),
                    lstFIle.get(num).split(":")[2].getBytes().length,
                    inetAddress,
                    Integer.parseInt(lstFIle.get(num).split(":")[1])
            );
            ClientSocket.send(toServer);

            byte inFromClient1[];
            inFromClient1 = new byte[256];

            DatagramPacket fromServer = new DatagramPacket(inFromClient1, inFromClient1.length);
            ClientSocket.receive(fromServer);
            String tmp = (new String(fromServer.getData(), 0, fromServer.getLength())).trim();
            long SizeFile = Long.parseLong((new String(fromServer.getData(), 0, fromServer.getLength())).trim());
//            System.out.println("size: " + size);

            String receive = "";
//            String line = "";
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream("download/" + lstFIle.get(num).split(":")[2]));
            long size = 0;
            //tao goi de nhan du lieu ve
            byte inFromServer[];
            inFromServer = new byte[10000];
            fromServer = new DatagramPacket(inFromServer, 10000);
            ClientSocket.setSoTimeout(3000);
            while (true) {
                // tao goi nhan du lieu ve
//                byte[] data = 
                fromServer = new DatagramPacket(inFromServer, 10000);
                // nhan goi tra ve tu server
                try {

                    ClientSocket.receive(fromServer);
                } catch (Exception ex) {
                    byte[] valueF = new byte[1];
                    valueF[0] = 0;
                    // lay so port
                    int portSV = 1027;
                    // tao goi de gui ve client
                    DatagramPacket Fpacket = new DatagramPacket(valueF, valueF.length, inetAddress, portSV);
                    ClientSocket.send(Fpacket);
                    continue;
                }

                if (size >= SizeFile) {
//                    bout.write(fromServer.getData());
                    break;
                }
                bout.write(fromServer.getData());
                size += fromServer.getLength();

                byte[] valueOK = new byte[1];
                valueOK[0] = 1;

                // lay so port
                int portSV = Integer.parseInt(lstFIle.get(num).split(":")[1]);
                // tao goi de gui ve client
                DatagramPacket Okpacket = new DatagramPacket(valueOK, valueOK.length, inetAddress, portSV);
//                DatagramPacket Okpacket = new DatagramPacket(valueOK, 1);
                ClientSocket.send(Okpacket);
                System.out.println(fromServer.getLength() + "=" + size + "=" + SizeFile);
            }

            //in ket qua ra man hinh
            bout.flush();
            bout.close();
            ClientSocket.close();

            System.out.println(receive);
//            if(receive.length() == size){
//                writeFile("download/" + lstFIle.get(num).split(":")[2], receive);
//            }

            break;
        }

        ClientSocket.close();
    }

    public static void writeFile(String nameFile, String dataFile) {
        try (
                FileWriter writer = new FileWriter(nameFile);
                BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(dataFile);
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
}
