package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
    public Socket S;

    public Cliente(String IP,int port){
        try{
            S = new Socket(IP,port);
            ObjectOutputStream os = new ObjectOutputStream(S.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(S.getInputStream());
            while(true){

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
