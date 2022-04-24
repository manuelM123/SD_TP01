package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class BackupServer {

    private Properties prop;
    private ServerSocket SS;
    private Socket S;
    private BackupConnection BC;


    public BackupServer(){
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/com/company/app.config")) {
            prop.load(fis);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try{
            SS = new ServerSocket(Integer.parseInt(prop.getProperty("app.backupPort")));
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
        while (true){
            try {
                S = SS.accept();
                BC = new BackupConnection(S);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    public static void main(String[] args) {
        BackupServer bc = new BackupServer();
    }
}
