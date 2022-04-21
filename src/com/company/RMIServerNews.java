package com.company;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Properties;

public class RMIServerNews {
    public static void main(String[] args) {
        System.setSecurityManager(new SecurityManager());
        Properties prop;
        //instantiate remote object
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/com/company/app.config")) {
            prop.load(fis);
        } catch (EOFException ex){
            System.out.println("App.config file was read.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try{
            java.rmi.registry.LocateRegistry.createRegistry(Integer.parseInt(prop.getProperty("app.mainServerPort")));
            System.out.println("News Server Ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try{
            String hostname = prop.getProperty("app.mainServerIp"); //hostname that resolves for a local address
            System.setProperty("java.rmi.server.hostname",hostname);
            RMIInterfaceLogin login_remote = new RMIImplLogin();
            RMIInterfaceNews news_remote = new RMIImplNews();
            //register object in RMI registry

            Naming.rebind("rmi://"+prop.getProperty("app.mainServerIp")+":"+prop.getProperty("app.mainServerPort")+"/RMIImplLogin",login_remote);
            Naming.rebind("rmi://"+prop.getProperty("app.mainServerIp")+":"+prop.getProperty("app.mainServerPort")+"/RMIImplNews",news_remote);
            System.out.println("Remote object ready");
        }catch (RemoteException | MalformedURLException e){
            System.out.println(e.getMessage());
        }
    }
}
