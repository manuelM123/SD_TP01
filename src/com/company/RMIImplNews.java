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
        super(1099);
        clientsCallback=new ArrayList<ClientCallbackInterface>();
        NewsList = new ArrayList<News>();
        Topics = new ArrayList<Topic>();
        BackupNewsList = new ArrayList<News>();
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/com/company/app.config")) {
            prop.load(fis);
        } catch (EOFException ignored){

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        readWriteFile(NEWSLISTREAD,null);
        readWriteFile(TOPICSREAD,null);
    }

    /**
     * add a Topic
     * @param Topic the topic's name to add
     * @return true if the topic was added or false if not
     * @throws RemoteException
     */
    public synchronized boolean add_Topic(String Topic) throws RemoteException {
        for (Topic t : Topics) {
            if(t.getName().equalsIgnoreCase(Topic))
                return false;
        }
        Topics.add(new Topic(Topic,0));
        readWriteFile(TOPICSWRITE,null);
        return true;
    }

    /**
     * Consult all topics
     * @return the topics list
     * @throws RemoteException
     */
    public ArrayList<String> consult_Topics() throws RemoteException{
        ArrayList<String> topicsNames = new ArrayList<String>();
        for (Topic t : Topics) {
            topicsNames.add(t.getName());
        }
        return topicsNames;
    }

    /**
     * add a news. if the topic limit is reached, 50% goes to the backup
     * @param news the news to add
     * @return true if the news was added successfuly or false if it wasn't added
     * @throws RemoteException
     */
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
                System.out.println("1");
                if(newsToRemove.size()!=0){
                    System.out.println("2");
                    readWriteFile(BACKUPWRITE,newsToRemove);
                    for(News n: newsToRemove){
                        NewsList.remove(n);
                    }
                }

                news.setTimestamp(new Date());
                NewsList.add(news);
                System.out.println("3");
                readWriteFile(NEWSLISTWRITE,null);
                System.out.println("4");
                readWriteFile(TOPICSWRITE,null);
                /**
                 * Sending callback to the proper subscriber
                 * */

                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        ArrayList<ClientCallbackInterface> removeclientsCallback = new ArrayList<ClientCallbackInterface>();
                        for(ClientCallbackInterface c : clientsCallback){
                            try{
                                if(((Subscriber) (c.getUser())).getSubscribedTopics().contains(news.getTopic()))
                                    c.showNotificationOnClient("You have a new news on topic "+news.getTopic()+".");
                            }catch (ConnectException e){
                                System.out.println("Cannot connect to client...");
                                removeclientsCallback.add(c);
                            } catch (RemoteException e) {
                                System.out.println("Remote timed out...");
                            }
                        }
                        /**
                         * unsubscribe client from logged in clients
                         * */
                        if(removeclientsCallback.size() != 0)
                            remove_callback_client(removeclientsCallback);
                    }
                }.start();
                return true;
            }
        }
        return false;
    }

    /**
     * Consult the publisher's news's
     * @param P the publisher (user)
     * @return the list of published news's
     * @throws RemoteException
     */
    public ArrayList<News> consult_news_publisher(Person P) throws RemoteException{
        ArrayList<News> publisherNews = new ArrayList<News>();
        for (News n : NewsList) {
            if(n.getPublisher().getUsername().equals(P.getUsername())){
                publisherNews.add(n);
            }
        }
        return publisherNews;
    }

    /**
     * Check if a user (publisher) has news in the backup. If it has return a list with backup ip and port
     * @param username the user's username
     * @return an empty list (if the user hasn't news in the backup) or a list with backup ip and port
     * @throws RemoteException
     */
    public ArrayList<String> news_from_backup(String username) throws RemoteException{
        ArrayList<String> backupIpPort = new ArrayList<String>();
        readWriteFile(BACKUPREAD,null);
        for(News backupNews: BackupNewsList){
            if(username.equals(backupNews.getPublisher().getUsername())){
                backupIpPort.add(prop.getProperty("app.backupIp"));
                backupIpPort.add(prop.getProperty("app.backupPort"));
                return backupIpPort;
            }
        }
        BackupNewsList.clear();
        return backupIpPort;
    }

    /**
     * Write or read from a file
     * @param i the operation to execute
     * @param nArrayList used when are news's to be written in the backup's file or null in the other cases
     * */
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
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                try {
                    os = new ObjectOutputStream(new FileOutputStream("src/com/company/topics.bin"));
                    os.writeObject(Topics);
                    os.flush();
                    os.close();
                }catch (IOException e) {
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
                        System.out.println("EOF NL INPUT");
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
                    System.out.println("EOF T INPUT");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 5:
                BackupNewsList = new ArrayList<>();
                try {
                    is = new ObjectInputStream(new FileInputStream("src/com/company/backupnews.bin"));
                    BackupNewsList = (ArrayList<News>) is.readObject();
                    is.close();
                }catch(EOFException ignored){
                    System.out.println("EOF BNL INPUT 1");
                } catch (ClassNotFoundException | IOException e){
                    System.out.println(e.getMessage());
                }
                BackupNewsList.addAll(nArrayList);
                try {
                    os = new ObjectOutputStream(new FileOutputStream("src/com/company/backupnews.bin"));
                    os.writeObject(BackupNewsList);
                    os.flush();
                    os.close();
                    BackupNewsList.clear();
                }catch (IOException e) {
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
                }catch(EOFException ex){
                    System.out.println("EOF BNL INPUT 2");
                }catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
    }

    /**
     * get the news's from a timestamp and a specific topic.
     * @param start the first date
     * @param end the end date
     * @param topic the specific topic
     * @return a list with all the news's between that timestamp and topic «
     * @throws RemoteException
     */
    public ArrayList<News> news_from_timestamp(Date start, Date end, String topic) throws RemoteException{
        ArrayList<News> newsFromTimestamp = new ArrayList<News>();
        for(News n: NewsList){
            if(n.getTimestamp().after(start) && n.getTimestamp().before(end) && n.getTopic().equalsIgnoreCase(topic))
                newsFromTimestamp.add(n);
        }
        return newsFromTimestamp;
    }

    /**
     * Check if the backup's server has news in a specific timestamp and topic. If it has return a list with backup ip and port
     * @param start the first date
     * @param end the end date
     * @param topic the specific topic
     * @return an empty list (if there isn´t any news in the backup) or a list with backup ip and port
     * @throws RemoteException
     */
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

    /**
     * Get the latest news from a specific topic
     * @param topic a specific topic
     * @return the last news
     * @throws RemoteException
     */
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

    /**
     * Subscribe a client to receive notification's
     * @param client
     * @throws RemoteException
     */
    @Override
    public void subscribe(ClientCallbackInterface client) throws RemoteException {
        clientsCallback.add(client);
    }

    /**
     * remove all client's from callback
     * @param removeclientsCallback
     */
    public synchronized void remove_callback_client(ArrayList<ClientCallbackInterface> removeclientsCallback){
        for(ClientCallbackInterface c : removeclientsCallback){
            clientsCallback.remove(c);
        }
    }

    /**
     * remove one client from callback
     * @param removeclientsCallback
     * @throws RemoteException
     */
    public synchronized void remove_callback_client(ClientCallbackInterface removeclientsCallback) throws RemoteException{
            clientsCallback.remove(removeclientsCallback);
    }
}
