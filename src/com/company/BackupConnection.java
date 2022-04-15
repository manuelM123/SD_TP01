package com.company;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

public class BackupConnection extends Thread{
    private Socket S;
    private ArrayList<News> backupNewsList;
    public BackupConnection(Socket S){
        super();
        this.backupNewsList = new ArrayList<>();
        ObjectInputStream is = null;
        this.S = S;
        try {
            is = new ObjectInputStream(new FileInputStream("src/com/company/backupnews.bin"));
            Object obj = null;
            while((obj = is.readObject()) != null)
            {
                backupNewsList.add((News) obj);
            }
            is.close();
        }catch (EOFException ex){
            System.out.println("Backup News file was read.");
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(e.getMessage());
        }
        start();
    }

    public void run() {
        ObjectInputStream is = null;
        ObjectOutputStream os = null;
        try {
            is = new ObjectInputStream(S.getInputStream());
            os = new ObjectOutputStream(S.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try{
            Date start = (Date) is.readObject();
            Date end = (Date) is.readObject();
            ArrayList<News> backupNewsFromTimestamp = news_from_timestamp_backup(start,end);
            os.writeObject(backupNewsFromTimestamp);
            os.flush();
            os.close();
            is.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    public ArrayList<News> news_from_timestamp_backup(Date start, Date end){
        ArrayList<News> backupNewsFromTimestamp = new ArrayList<News>();
        for(News n: backupNewsList){
            if(n.getTimestamp().after(start) && n.getTimestamp().before(end))
                backupNewsFromTimestamp.add(n);
        }
        return backupNewsFromTimestamp;
    }
}
