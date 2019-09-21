/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastersv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author ThinkKING
 */
public class MasterSV {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws ClassNotFoundException {        

        try {
           TCPServer.GetInstance().Start();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }


    }

}
