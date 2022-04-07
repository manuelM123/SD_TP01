package com.company;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIImplNews extends UnicastRemoteObject implements RMIInterfaceNews {

    protected RMIImplNews() throws RemoteException {
        super();
    }
}
