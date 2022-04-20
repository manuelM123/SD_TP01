package com.company;

import java.rmi.RemoteException;

public interface ClientCallbackInterface extends java.rmi.Remote{

    public void showNotificationOnClient (String s) throws java.rmi.RemoteException;
    public Person getUser() throws RemoteException;
}
