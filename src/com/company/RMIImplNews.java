package com.company;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;



public class RMIImplNews extends UnicastRemoteObject implements RMIInterfaceNews {

    private ArrayList<News> NewsList;
    private ArrayList<Topic> Topics;
    private ArrayList<News> BackupNewsList;
    private Properties prop;
    public static int NEWSLISTWRITE = 1;
    public static int TOPICSWRITE = 2;
    public static int NEWSLISTREAD = 3;
    public static int TOPICSREAD = 4;
    public static int BACKUPWRITE = 5;
    public static int BACKUPREAD = 6;
    private static ArrayList<ClientCallbackInterface> clientsCallback;
    private ObjectInputStream inputNewsList, inputBackup, inputTopics;
    private ObjectOutputStream outputNewsList, outputBackup, outputTopics;


    protected RMIImplNews() throws RemoteException {
        super();
        clientsCallback=new ArrayList<ClientCallbackInterface>();
        NewsList = new ArrayList<News>();
        Topics = new ArrayList<Topic>();
        BackupNewsList = new ArrayList<News>();
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/com/company/app.config")) {
            prop.load(fis);
        } catch (EOFException ex){
            System.out.println("App.config file was read.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(prop.getProperty("app.topicLimit"));
        readWriteFile(NEWSLISTREAD,null);
        readWriteFile(TOPICSREAD,null);
    }

    public synchronized boolean add_Topic(String Topic) throws RemoteException {
        for (Topic t : Topics) {
            if(t.getName().equalsIgnoreCase(Topic))
                return false;
        }
        Topics.add(new Topic(Topic,0));
        readWriteFile(TOPICSWRITE,null);
        return true;
    }

    public ArrayList<String> consult_Topics() throws RemoteException{
        ArrayList<String> topicsNames = new ArrayList<String>();
        for (Topic t : Topics) {
            topicsNames.add(t.getName());
        }
        return topicsNames;
    }

    public synchronized boolean add_News(News news) throws RemoteException{
        /**
        * Check if limit of news' topic has been reached
        * if true: 50% of the news on that topic will go to backup
        * else: add received news into the current amount of news
        */
        ArrayList<News> newsToRemove = new ArrayList<>();
        int limit = Integer.parseInt(prop.getProperty("app.topicLimit"));
        for (Topic t : Topics) {
            if (t.getName().equalsIgnoreCase(news.getTopic())){
                t.setQuantity(t.getQuantity()+1);
                if(t.getQuantity() == limit){
                    System.out.println("Limit has been reached for topic: " + news.getTopic());
                    /**
                     * Send data to backup server and delete 50% of the arraylist
                     * When deleted, update the amount of news in that topic to 50%
                     * Update the news file
                     * */

                    t.setQuantity(limit/2);
                    int removed=0;
                    for (News n: NewsList){
                        if(removed == limit/2)
                            break;
                        else if(n.getTopic().equalsIgnoreCase(t.getName())){
                            newsToRemove.add(n);
                            removed++;
                        }
                    }
                }
                readWriteFile(BACKUPWRITE,newsToRemove);
                for(News n: newsToRemove){
                    NewsList.remove(n);
                }
                news.setTimestamp(new Date());
                NewsList.add(news);
                readWriteFile(NEWSLISTWRITE,null);
                readWriteFile(TOPICSWRITE,null);
                /**
                 * Sending callback to the proper subscriber
                 * */
                ArrayList<ClientCallbackInterface> removeclientsCallback = new ArrayList<ClientCallbackInterface>();
                for(ClientCallbackInterface c : clientsCallback){
                    try{
                        if(((Subscriber) (c.getUser())).getSubscribedTopics().contains(news.getTopic()))
                            c.showNotificationOnClient("You have a new news on topic "+news.getTopic()+".");
                    }catch (ConnectException e){
                        System.out.println("Cannot connect to client...");
                        removeclientsCallback.add(c);
                    }
                }
                /**
                 * unsubscribe client from logged in clients
                 * */
                if(removeclientsCallback.size() != 0)
                    remove_callback_client(removeclientsCallback);
                return true;
            }
        }
        return false;
    }

    public ArrayList<News> consult_news_publisher(Person P) throws RemoteException{
        ArrayList<News> publisherNews = new ArrayList<News>();
        for (News n : NewsList) {
            if(n.getPublisher().getUsername().equals(P.getUsername())){
                publisherNews.add(n);
            }
        }
        readWriteFile(BACKUPREAD,null);
        for(News backupNews: BackupNewsList){
            if(P.getUsername().equals(backupNews.getPublisher().getUsername())){
                publisherNews.add(backupNews);
            }
        }
        BackupNewsList.clear();
        return publisherNews;
    }

    private synchronized void readWriteFile(int i, ArrayList<News> nArrayList){
        ObjectOutputStream os = null;
        ObjectInputStream is = null;
        switch (i){
            case 1:
                try {
                    os = new ObjectOutputStream(new FileOutputStream("src/com/company/newslist.bin"));
                    os.writeObject(NewsList);
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                try {
                    os = new ObjectOutputStream(new FileOutputStream("src/com/company/topics.bin"));
                    os.writeObject(Topics);
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                try {
                    is = new ObjectInputStream(new FileInputStream("src/com/company/newslist.bin"));
                    Object obj = is.readObject();
                    NewsList = (ArrayList<News>) obj;
                    is.close();
                } catch (EOFException ex){
                    System.out.println("News list file was read.");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                try {
                    is = new ObjectInputStream(new FileInputStream("src/com/company/topics.bin"));
                    Object obj = is.readObject();
                    Topics = (ArrayList<Topic>) obj;
                    is.close();
                } catch (EOFException ex){
                    System.out.println("Topics file was read.");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 5:
                BackupNewsList = new ArrayList<>();
                try{
                    is = new ObjectInputStream(new FileInputStream("src/com/company/backupnews.bin"));
                    BackupNewsList = (ArrayList<News>) is.readObject();
                }catch (ClassNotFoundException | FileNotFoundException e){
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                BackupNewsList.addAll(nArrayList);
                try {
                    os = new ObjectOutputStream(new FileOutputStream("src/com/company/backupnews.bin"));
                    os.writeObject(BackupNewsList);
                    os.flush();
                    os.close();
                    is.close();
                    BackupNewsList.clear();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 6:
                BackupNewsList = new ArrayList<>();
                try {
                    is = new ObjectInputStream(new FileInputStream("src/com/company/backupnews.bin"));
                    Object obj = is.readObject();
                    BackupNewsList = (ArrayList<News>) obj;
                    is.close();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
    }

    public ArrayList<News> news_from_timestamp(Date start, Date end, String topic) throws RemoteException{
        ArrayList<News> newsFromTimestamp = new ArrayList<News>();
        for(News n: NewsList){
            if(n.getTimestamp().after(start) && n.getTimestamp().before(end) && n.getTopic().equalsIgnoreCase(topic))
                newsFromTimestamp.add(n);
        }
        return newsFromTimestamp;
    }

    public ArrayList<String> news_from_timestamp_backup(Date start, Date end, String topic) throws RemoteException{
        ArrayList<String> backupIpPort = new ArrayList<String>();
        readWriteFile(BACKUPREAD,null);
        for(News backupNews: BackupNewsList){
            if(backupNews.getTimestamp().after(start) && backupNews.getTimestamp().before(end) && backupNews.getTopic().equalsIgnoreCase(topic)){
                backupIpPort.add(prop.getProperty("app.backupIp"));
                backupIpPort.add(prop.getProperty("app.backupPort"));
                return backupIpPort;
            }
        }
        BackupNewsList.clear();
        return backupIpPort;
    }

    public News latest_news_from_topic(String topic) throws RemoteException{
        News newsFromTopic = null;
        for (int i = NewsList.size()-1; i >= 0 ; i--) {
            if(NewsList.get(i).getTopic().equalsIgnoreCase(topic)){
                newsFromTopic = NewsList.get(i);
                return newsFromTopic;
            }
        }
        return null;
    }

    @Override
    public synchronized void subscribe(ClientCallbackInterface client) throws RemoteException {
        System.out.println("Subscribing: " + client);
        clientsCallback.add(client);
        client.showNotificationOnClient("I connected.");
    }

    public synchronized void remove_callback_client(ArrayList<ClientCallbackInterface> removeclientsCallback){
        for(ClientCallbackInterface c : removeclientsCallback){
            clientsCallback.remove(c);
        }
    }

    public synchronized void remove_callback_client(ClientCallbackInterface removeclientsCallback) throws RemoteException{
            clientsCallback.remove(removeclientsCallback);
    }
}
