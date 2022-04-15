package com.company;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;



public class RMIImplNews extends UnicastRemoteObject implements RMIInterfaceNews {

    private ArrayList<News> NewsList;
    private ArrayList<Topic> Topics;
    private Properties prop;
    public static int NEWSLISTWRITE = 1;
    public static int TOPICSWRITE = 2;
    public static int NEWSLISTREAD = 3;
    public static int TOPICSREAD = 4;
    public static int BACKUPWRITE = 5;

    protected RMIImplNews() throws RemoteException {
        super();
        NewsList = new ArrayList<News>();
        Topics = new ArrayList<Topic>();
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

    public boolean add_Topic(String Topic) throws RemoteException {
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

    public boolean add_News(News news) throws RemoteException{
        /**
        * Check if limit of news' topic has been reached
        * if true: 50% of the news on that topic will go to backup
        * else: add received news into the current amount of news
        */
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
                            //send data to backup file before removing
                            readWriteFile(BACKUPWRITE,n);
                            NewsList.remove(n);
                            removed++;
                        }
                    }
                }
                news.setTimestamp(new Date());
                NewsList.add(news);
                readWriteFile(NEWSLISTWRITE,null);
                readWriteFile(TOPICSWRITE,null);
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
        return publisherNews;
    }

    private void readWriteFile(int i, News n){
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
                try {
                    os = new ObjectOutputStream(new FileOutputStream("src/com/company/backupnews.bin",true));
                    os.writeObject(n);
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
    }

    public ArrayList<News> news_from_timestamp(Date start, Date end) throws RemoteException{
        ArrayList<News> newsFromTimestamp = new ArrayList<News>();
        for(News n: NewsList){
            if(n.getTimestamp().after(start) && n.getTimestamp().before(end))
                newsFromTimestamp.add(n);
        }
        return newsFromTimestamp;
    }

    public ArrayList<String> news_from_timestamp_backup(Date start, Date end) throws RemoteException{
        ArrayList<String> backupIpPort = new ArrayList<String>();
        ObjectInputStream is = null;
        News backupNews = null;
        try {
            is = new ObjectInputStream(new FileInputStream("src/com/company/backupnews.bin"));
            Object obj = null;
            while( (obj = is.readObject()) != null)
            {
                backupNews = (News) obj;
                if(backupNews.getTimestamp().after(start) && backupNews.getTimestamp().before(end)){
                    backupIpPort.add(prop.getProperty("app.backupIp"));
                    backupIpPort.add(prop.getProperty("app.backupPort"));
                    return backupIpPort;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
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
}
