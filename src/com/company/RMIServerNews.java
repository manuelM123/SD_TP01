package com.company;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class RMIServerNews {
    public static void main(String[] args) {
        System.setSecurityManager(new SecurityManager());
        try{
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("News Server Ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //instantiate remote object
        try{
            RMIInterfaceLogin login_remote = new RMIImplLogin();

            //register object in RMI registry
            Naming.rebind("RMIImplLogin",login_remote);
            System.out.println("Remote object ready");
        }catch (RemoteException | MalformedURLException e){
            System.out.println(e.getMessage());
        }
    }
}
