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
        }catch(EOFException ignored){

        }catch (IOException e) {
            System.out.println("No users to show");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Amount of users registered: " + users.size());
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
    public void Register(Person P) throws RemoteException {
        users.add(P);
        saveUsers();
    }

    @Override
    public void addTopic(Person P) throws RemoteException {
        for(Person user: users){
            if(user.getUsername().equals(P.getUsername()))
                users.set(users.indexOf(user),P);
        }
        saveUsers();
    }

    private void saveUsers(){
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
