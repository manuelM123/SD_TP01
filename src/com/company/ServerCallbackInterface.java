package com.company;

public interface ServerCallbackInterface extends java.rmi.Remote{
    public void showOnServer(String s) throws java.rmi.RemoteException;
    public void subscribe(String s, ClientCallbackInterface client) throws java.rmi.RemoteException;
}
