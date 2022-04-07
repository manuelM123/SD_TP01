package com.company;

public interface RMIInterfaceLogin extends java.rmi.Remote{
    //methods to implement
    public boolean Username(String username) throws java.rmi.RemoteException;
    public Person Password(String username, String password) throws java.rmi.RemoteException;
    public void Register(Person P) throws java.rmi.RemoteException;
}
