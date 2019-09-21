/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastersv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AnhQuynh
 */
public class TCPServer {

    private static TCPServer s_tcpServer = null;
    private static ServerSocket s_listener;

    private static int s_port;

    public ServerSocket GetListener() {
        return s_listener;
    }

    private TCPServer() throws IOException {
        s_port = Config.GetPort();
        s_listener = new ServerSocket(s_port);
    }

    public static TCPServer GetInstance() throws IOException {
        if (s_tcpServer == null) {

            s_tcpServer = new TCPServer();
        }
        return s_tcpServer;
    }

    public static void Start() throws ClassNotFoundException {
        ArrayList<String> LstFile = Config.ReadLstFile();
        if (LstFile == null) {
            System.err.println("Failed to load file to share!");
            return;
        }
        Socket socketOfServer = null;
        try {
            System.out.println("Server is waiting to accept user...");

            // Chấp nhận một yêu cầu kết nối từ phía Client.
            // Đồng thời nhận được một đối tượng Socket tại server.
            // Nhận được dữ liệu từ người dùng và gửi lại trả lời.
            while (true) {
                // Đọc dữ liệu tới server (Do client gửi tới).
                socketOfServer = TCPServer.GetInstance().GetListener().accept();
                System.out.println("Accept a client!");
                final ObjectInputStream ois = new ObjectInputStream(socketOfServer.getInputStream());
                final ObjectOutputStream oos = new ObjectOutputStream(socketOfServer.getOutputStream());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String catClient = (String) ois.readObject();
                            if (catClient.equals("filescv")) {
                                oos.writeObject("filescv");
                                ArrayList<String> line = (ArrayList<String>) ois.readObject();
                                for (String iStr : line) {
                                    if (!LstFile.contains(iStr)) {
                                        LstFile.add(iStr);
                                    }
                                }
                                Config.WriteLstFile();
                                System.out.println(line);
                            } else {
                                oos.writeObject("client");
                                String reqFileName = (String) ois.readObject();
                                for (String nameFile : LstFile) {
                                    if (nameFile.split(">>>")[1].equals(reqFileName)) {
                                        oos.writeObject(nameFile.split(">>>")[0]);
                                        break;
                                    }
                                }
                                System.out.println(reqFileName);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                });

                thread.start();
            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
