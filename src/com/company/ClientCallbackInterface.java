package com.company;

public interface ClientCallbackInterface extends java.rmi.Remote{
    public void showNotificationOnClient (String s)throws java.rmi.RemoteException;
}
