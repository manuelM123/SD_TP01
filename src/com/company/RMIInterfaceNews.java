package com.company;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

public interface RMIInterfaceNews extends java.rmi.Remote{
    public boolean add_Topic(String Topic) throws RemoteException;
    public ArrayList<String> consult_Topics() throws RemoteException;
    public boolean add_News(News news) throws RemoteException;
    public ArrayList<News> consult_news_publisher(Person P) throws RemoteException;
    public ArrayList<String> news_from_backup(String username) throws RemoteException;
    public ArrayList<News> news_from_timestamp(Date start, Date end, String topic) throws RemoteException;
    public ArrayList<String> news_from_timestamp_backup(Date start, Date end, String topic) throws RemoteException;
    public News latest_news_from_topic(String topic) throws RemoteException;
    public void subscribe( ClientCallbackInterface client) throws java.rmi.RemoteException;
    public void remove_callback_client(ClientCallbackInterface removeclientsCallback) throws RemoteException;
}
