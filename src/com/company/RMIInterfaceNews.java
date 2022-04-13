package com.company;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIInterfaceNews extends java.rmi.Remote{
    //methods to add in future
    public boolean add_Topic(String Topic) throws RemoteException;
    public ArrayList<String> consult_Topics() throws RemoteException;
    public boolean add_News(News news) throws RemoteException;
    public ArrayList<News> consult_news_publisher(Person P) throws RemoteException;

}
