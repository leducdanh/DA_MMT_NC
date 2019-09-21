/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastersv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AnhQuynh
 */
public class Config {

    private static int port = -1;
    private static ArrayList<String> LstFile = null;

    public static int GetPort() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get("Config_Port.txt"))) {

            // read line by line
            String line;
            line = br.readLine();
            port = Integer.parseInt(line);

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            return -1;
        }

        return port;
    }

    public static ArrayList<String> ReadLstFile() {
        LstFile = new ArrayList<String>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get("ShareFiles.txt"))) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                LstFile.add(line);
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            return null;
        }
        return LstFile;
    }

    public static boolean WriteLstFile() throws FileNotFoundException {
        if (LstFile != null) {
            File fout = new File("ShareFiles.txt");
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            try {

                for (String str : LstFile) {

                    bw.write(str);

                    bw.newLine();
                }

                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }
}
