package com.company;

import com.myinputs.Ler;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIImplLogin extends UnicastRemoteObject implements RMIInterfaceLogin {
    ArrayList<Person> users;

    public RMIImplLogin() throws java.rmi.RemoteException{
        super();
        users = new ArrayList<Person>();
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("src/com/company/users.bin"));
            Object obj = is.readObject();
            users = (ArrayList<Person>) obj;
        } catch (IOException e) {
            System.out.println("No users to show");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Amount of users registered: " + users.size());
            /*for (int i = 0; i < users.size(); i++) {
                System.out.println(users.get(i).toString());
            }*/
        }
    }

    @Override
    public boolean Username(String username) throws RemoteException {
        for(int i=0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(username))
                return true;
        }
        return false;
    }

    @Override
    public Person Password(String username, String password) throws RemoteException {
        for(int i=0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(username) && users.get(i).getPassword().equals(password)){
                return users.get(i);
            }
        }
        return null;
    }

    @Override
    public void Register(Person P) {
        System.out.println(P);
        users.add(P);
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("src/com/company/users.bin"));
            os.writeObject(users);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
