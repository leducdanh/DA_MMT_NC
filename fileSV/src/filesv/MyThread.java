/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

    public void start() {
        System.out.println("start thread");
    }

    public void run() {
        //tao mang byte de chua du lieu gui len tu client
        byte inFromClient1[];
        inFromClient1 = new byte[256];
        // tao goi de nhan du lieu gui len tu client
        String fileRequest = (new String(fromClient1.getData(), 0, inFromClient1.length)).trim();
        System.out.println(fileRequest);
        ////////////////////////////
        FileReader fr = null;
        try {
            //send size
            BufferedInputStream f = new BufferedInputStream(new FileInputStream("Share/" + fileRequest));
            byte[] buff = f.readAllBytes();
            byte outToClient[];
            outToClient = Long.toString(buff.length).getBytes();
            InetAddress address = fromClient1.getAddress();
            // get port
            int port = fromClient1.getPort();
            DatagramPacket toClient = new DatagramPacket(outToClient, outToClient.length, address, port);
            serverSocket.send(toClient);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedInputStream bin = null;
        try {
            bin = new BufferedInputStream(
                    new FileInputStream("Share/" + fileRequest));

            while (true) {
                byte[] datum = bin.readNBytes(10000);

                if (datum.length < 1) {
                    //lay dia chi cua may khach, no nam luon trong goi ma da gui len server
                    InetAddress address = fromClient1.getAddress();
                    // lay so port
                    int port = fromClient1.getPort();
                    // tao goi de gui ve client
                    DatagramPacket toClient = new DatagramPacket(datum, datum.length, address, port);
                    //gui goi ve client
                    try {
                        serverSocket.send(toClient);
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }

                //lay dia chi cua may khach, no nam luon trong goi ma da gui len server
                InetAddress address = fromClient1.getAddress();
                // lay so port
                int port = fromClient1.getPort();
                // tao goi de gui ve client
                DatagramPacket toClient = new DatagramPacket(datum, datum.length, address, port);
                try {
                    byte[] inFromCL = new byte[1];
                    DatagramPacket fromCL = new DatagramPacket(inFromCL, 1);
                    inFromCL[0] = 0;
//                    Thread.sleep(5000);
                    int i = 0;
                    // process lost data package
                    while (inFromCL[0] == 0) {
//                        if(i != 0)
                        {
                            serverSocket.send(toClient);
                        }
                        i++;
                        System.out.println(datum.length);
                        serverSocket.receive(fromCL);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bin.close();

        } catch (IOException ex) {
            Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
