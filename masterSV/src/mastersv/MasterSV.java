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
    public final static int SERVER_PORT = 7; // Cổng mặc định của Echo Server
    public final static byte[] BUFFER = new byte[4096]; // Vùng đệm chứa dữ liệu cho gói tin nhận

    public static void main(String[] args) throws ClassNotFoundException {
        ServerSocket listener = null;
        ArrayList<String> LstFile = new ArrayList<String>();
        BufferedReader is;
        BufferedWriter os;
        Socket socketOfServer = null;

        try {
            listener = new ServerSocket(9999);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        try {
            System.out.println("Server is waiting to accept user...");

            // Chấp nhận một yêu cầu kết nối từ phía Client.
            // Đồng thời nhận được một đối tượng Socket tại server.
            // Nhận được dữ liệu từ người dùng và gửi lại trả lời.
            while (true) {
                // Đọc dữ liệu tới server (Do client gửi tới).
                socketOfServer = listener.accept();
                System.out.println("Accept a client!");
                ObjectInputStream ois = new ObjectInputStream(socketOfServer.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socketOfServer.getOutputStream());
                String catClient = (String) ois.readObject();
                if (catClient.equals("filescv")) {
                    oos.writeObject("filescv");
                    ArrayList<String> line = (ArrayList<String>) ois.readObject();
                    for (String iStr : line){
                        LstFile.add(iStr);
                    }
                    System.out.println(line);
                } else {
                    oos.writeObject("client");
                    String reqFileName = (String) ois.readObject();
                    for (String nameFile : LstFile){
                        if (nameFile.split(">>>")[1].equals(reqFileName)){
                            oos.writeObject(nameFile.split(">>>")[0]);
                            break;
                        }
                    }
                    System.out.println(reqFileName);
                }

            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
