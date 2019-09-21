/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ThinkKING
 */
public class MyThread extends Thread {

    private String IP;
    private Integer PORT;
    private Thread th;
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket, sendpacket;
    private DatagramPacket fromClient1;

    public MyThread(String ipFileSV, Integer portFileSV, DatagramPacket fromClient1, DatagramSocket serverSocket) {
        this.IP = ipFileSV;
        this.PORT = portFileSV;
        this.fromClient1 = fromClient1;
        this.serverSocket = serverSocket;
    }

    public void run() {
        System.out.println("run thread");
    }

    public void start() {
        //tao mang byte de chua du lieu gui len tu client
        byte inFromClient1[];
        inFromClient1 = new byte[256];
        int leng1 = inFromClient1.length;
        // tao goi de nhan du lieu gui len tu client
        String fileRequest = (new String(fromClient1.getData(), 0, inFromClient1.length)).trim();
        System.out.println(fileRequest);
        ////////////////////////////
        FileReader fr = null;
        try {
            fr = new FileReader("Share/" + fileRequest);
            
            //send size
            File f = new File("Share/" + fileRequest);
            byte outToClient[];
            outToClient = Long.toString(f.length()).getBytes();
            InetAddress address = fromClient1.getAddress();
            // lay so port
            int port = fromClient1.getPort();
            // tao goi de gui ve client
            DatagramPacket toClient = new DatagramPacket(outToClient, outToClient.length, address, port);
            serverSocket.send(toClient);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br = new BufferedReader(fr);

        while (true) {
            String str = null;
            try {
                str = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (str == null) {
                str = ">>>@the_end<<<";
                byte outToClient[];
                outToClient = str.getBytes();
                //lay kich thuoc mang
                leng1 = outToClient.length;
                //lay dia chi cua may khach, no nam luon trong goi ma da gui len server
                InetAddress address = fromClient1.getAddress();
                // lay so port
                int port = fromClient1.getPort();
                // tao goi de gui ve client
                DatagramPacket toClient = new DatagramPacket(outToClient, leng1, address, port);
                //gui goi ve client
                System.out.println(str);
                try {
                    serverSocket.send(toClient);
                } catch (IOException ex) {
                    Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }

            // dong goi ket qua
            byte outToClient[];
            outToClient = str.getBytes();
            //lay kich thuoc mang
            leng1 = outToClient.length;
            //lay dia chi cua may khach, no nam luon trong goi ma da gui len server
            InetAddress address = fromClient1.getAddress();
            // lay so port
            int port = fromClient1.getPort();
            // tao goi de gui ve client
            DatagramPacket toClient = new DatagramPacket(outToClient, leng1, address, port);
            //gui goi ve client
            System.out.println(str);
            try {
                serverSocket.send(toClient);
            } catch (IOException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
