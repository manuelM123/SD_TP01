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

    protected RMIImplNews() throws RemoteException {
        super();
        NewsList = new ArrayList<News>();
        Topics = new ArrayList<Topic>();
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/com/company/app.config")) {
            prop.load(fis);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(prop.getProperty("app.topicLimit"));
        readWriteFile(NEWSLISTREAD);
        readWriteFile(TOPICSREAD);
    }

    public boolean add_Topic(String Topic) throws RemoteException {
        for (Topic t : Topics) {
            if(t.getName().equalsIgnoreCase(Topic))
                return false;
        }
        Topics.add(new Topic(Topic,0));
        readWriteFile(TOPICSWRITE);
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
                            //send data to backup before removing

                            NewsList.remove(n);
                            removed++;
                        }
                    }
                }
                news.setTimestamp(new Date());
                NewsList.add(news);
                readWriteFile(NEWSLISTWRITE);
                readWriteFile(TOPICSWRITE);
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

    private void readWriteFile(int i){
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
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                try {
                    is = new ObjectInputStream(new FileInputStream("src/com/company/topics.bin"));
                    Object obj = is.readObject();
                    Topics = (ArrayList<Topic>) obj;
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
    }
}
