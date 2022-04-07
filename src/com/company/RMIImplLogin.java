package com.company;

import com.myinputs.Ler;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIImplLogin extends UnicastRemoteObject implements RMIInterfaceLogin {
    ArrayList<Person> users;

    public RMIImplLogin() throws java.rmi.RemoteException{
        super();
        users = new ArrayList<Person>();
        //read users from file
        //file XPTO
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
        users.add(P);
        //after insertion, update file with new user
    }
}
