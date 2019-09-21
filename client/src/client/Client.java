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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        final String serverHost = "172.19.201.67";

        Socket socketOfClient = null;

        String IpAndPortFileSV = "";
        ArrayList<String> lstFIle = new ArrayList<String>();

        try {
            socketOfClient = new Socket(serverHost, 9859);

            ObjectOutputStream oos = new ObjectOutputStream(socketOfClient.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socketOfClient.getInputStream());

            oos.writeObject("client");
            lstFIle = (ArrayList<String>) ois.readObject();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
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
            int size = Integer.parseInt(tmp);
            System.out.println("size: " + size);

            String receive = "";
            String line = "";
            while (!line.equals(">>>@the_end<<<")) {
                ClientSocket.receive(fromServer);
                receive += line;
                line = (new String(fromServer.getData(), 0, fromServer.getLength())).trim();
            }

            System.out.println(receive);
            if(receive.length() == size){
                writeFile("download/" + lstFIle.get(num).split(":")[2], receive);
            }
            
            break;
        }

        ClientSocket.close();
    }

    public static void writeFile(String nameFile, String dataFile){
        try (
            FileWriter writer = new FileWriter(nameFile);
            BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(dataFile);
	} catch (IOException e) {
            System.err.format("IOException: %s%n", e);
	}
    }
}
