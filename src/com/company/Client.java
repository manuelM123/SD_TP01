package com.company;

import com.myinputs.Ler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;


public class Client extends java.rmi.server.UnicastRemoteObject implements ClientCallbackInterface{
    private static RMIInterfaceLogin LoginObject;
    private static RMIInterfaceNews NewsObject;
    private static Person user = null;
    private static Client C;
    private static Properties prop;

    public Person getUser() throws RemoteException {
        return user;
    }

    public Client() throws RemoteException{
        super();
        System.setSecurityManager(new SecurityManager());
    }

    public static void main(String[] args) {
        C = null;
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/com/company/client.config")) {
            prop.load(fis);
        } catch (EOFException ex){
            System.out.println("App.config file was read.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            //method to bind server object to object in client (shared remote object)
            LoginObject = (RMIInterfaceLogin) Naming.lookup("rmi://"+prop.getProperty("app.mainServerIp")+":"+prop.getProperty("app.mainServerPort")+"/RMIImplLogin");
            NewsObject = (RMIInterfaceNews) Naming.lookup("rmi://"+prop.getProperty("app.mainServerIp")+":"+prop.getProperty("app.mainServerPort")+"/RMIImplNews");
            C = new Client();

        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            System.out.println(e.getMessage());
        }

        try{
            int option;
            do{
                Login_menu();
                option = Ler.umInt();

                switch (option){
                    //Login method
                    case 1:
                        user = Login_methods();
                        if(user != null){
                            if(user.getRole().equalsIgnoreCase("subscriber")){
                                NewsObject.subscribe((ClientCallbackInterface) C);
                            }
                            Menu_Role(user.getRole());
                        }
                        else
                            System.out.println("Wrong credentials! Retry login.");

                        break;
                    //Register method
                    case 2:
                        user = Register_Methods();
                        if(user != null) {
                            LoginObject.Register(user);
                            if(user.getRole().equalsIgnoreCase("subscriber")){
                                NewsObject.subscribe((ClientCallbackInterface) C);
                            }
                            Menu_Role(user.getRole());
                        }
                        else
                            System.out.println("Failed to register!");
                        break;
                    //View news method
                    case 3:
                        Menu_Role("");
                        break;

                    case 4:
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Invalid option! Insert again!");
                        break;
                }
            }while(option != 4);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

    public static void Login_menu(){
        System.out.println("1- Login");
        System.out.println("2- Register");
        System.out.println("3- View news");
        System.out.println("4- Exit");
        System.out.println("-----------------------");
    }

    public static void Menu_Role(String role){
        int option = 0;
        if(role.equals("Publisher")){
            do {
                System.out.println("Publisher Menu");
                System.out.println("1- Add a topic");
                System.out.println("2- View existing topics");
                System.out.println("3- Insert news from a topic");
                System.out.println("4- View every published news");
                System.out.println("5- Logout");

                option = Ler.umInt();

                switch (option){
                    case 1:
                        System.out.println("Insert topic:");
                        try {
                            if(NewsObject.add_Topic(Ler.umaString()))
                                System.out.println("Topic added!");
                            else
                                System.out.println("Topic already exists!");
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            ArrayList<String> topicsList = NewsObject.consult_Topics();
                            for(String t: topicsList){
                                System.out.println(" - " + t);
                            }
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 3:
                        try{
                            News n = createNews();
                            if(n == null){
                                System.out.println("News not created!");
                                System.out.println("Returning to menu...");
                            }else{
                                System.out.println("News created!");
                                NewsObject.add_News(n);
                            }
                        } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                        }

                        break;

                    case 4:

                        try{
                            ArrayList<News> newsArrayList = NewsObject.consult_news_publisher(user);
                            for(News n : newsArrayList){
                                System.out.println(n.toString());
                            }
                        }catch (RemoteException e){
                            System.out.println(e.getMessage());
                        }
                        ArrayList<String> backupIpPort = null;
                        ArrayList<News>BackupsNewsList= new ArrayList<News>();
                        try {
                            backupIpPort = NewsObject.news_from_backup(user.getUsername());
                            if(backupIpPort.size() != 0){
                                System.out.println("There are news in the arquive.");
                                System.out.println("Do you want to see it? (Yes or No)");
                                String s = Ler.umaString();
                                if(s.equalsIgnoreCase("yes")){
                                    BackupsNewsList = news_from_backup_publisher(backupIpPort.get(0),backupIpPort.get(1));
                                    for(News n: BackupsNewsList){
                                        System.out.println(n.toString());
                                    }
                                }
                            }else{
                                System.out.println("No news in the arquive in that timestamp.");
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        break;

                    case 5:
                        System.out.println("Returning to login menu.");
                        /**
                         * unsubscribe client from logged in clients
                         * */
                        break;

                    default:
                        System.out.println("Invalid option! Insert again!");
                        break;
                }
                System.out.println("-----------------------");
            }while (option != 5);
        }
        else if(role.equals("Subscriber")){
            do {
                System.out.println("Subscriber Menu");
                System.out.println("1- Subscribe a topic");
                System.out.println("2- View news from a topic in a timestamp");
                System.out.println("3- View last news from a topic");
                System.out.println("4- Logout");
                System.out.println("-----------------------");

                option = Ler.umInt();

                switch (option) {
                    case 1:
                        ArrayList<String> currentTopics, subscribedTopics;
                        try{
                            int choice=-1;
                            currentTopics = NewsObject.consult_Topics();
                            subscribedTopics = ((Subscriber) user).getSubscribedTopics();
                            if(currentTopics.size() == 0 || currentTopics.size() == subscribedTopics.size()) {
                                System.out.println("No topics available");
                                break;
                            }
                            System.out.println("Choose a topic from the list:");
                            System.out.println("0 - Cancel operation.");
                            for (int i = 0; i < currentTopics.size(); i++) {
                                if(!subscribedTopics.contains(currentTopics.get(i)))
                                    System.out.println((i+1) + " - " + currentTopics.get(i)+".");
                            }
                            do {
                                choice = Ler.umInt();
                                if((choice < 0 || choice > currentTopics.size()) && subscribedTopics.contains(currentTopics.get(choice-1)))
                                    System.out.println("Wrong choice, please choose again.");
                                else
                                    break;
                            }while (true);
                            if(choice != 0){
                                ((Subscriber) user).addTopic(currentTopics.get(choice-1));
                                LoginObject.addTopic(user);
                            }
                        }catch (RemoteException e){
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 2:
                        /**Request the date from the user by input(Ler.method)
                         * Keep the values from input
                         * Verify if news exist with such timestamp
                         * if true -> show the list of news in that timestamp
                         *            request if the user wants to check the backup for more news
                         * else -> show an error to the user
                         * */
                        view_news_from_topic_timestamp();
                        break;

                    case 3:
                        view_last_news_from_topic();
                        break;

                    case 4:
                        System.out.println("Returning to login menu.");
                        try {
                            NewsObject.remove_callback_client(C);
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    default:
                        System.out.println("Invalid option! Insert again!");
                        break;
                }
                System.out.println("-----------------------");
            }while (option != 4);
        } else{
            do {
                System.out.println("Non-Subscriber Menu");
                System.out.println("1- View news from a topic in a timestamp");
                System.out.println("2- View last news from a topic");
                System.out.println("3- Logout");
                System.out.println("-----------------------");
                option=Ler.umInt();
                switch (option) {
                    case 1:
                        view_news_from_topic_timestamp();
                        break;

                    case 2:
                        view_last_news_from_topic();
                        break;

                    case 3:
                        System.out.println("Returning to login menu.");
                        break;

                    default:
                        System.out.println("Invalid option! Insert again!");
                        break;
                }
                System.out.println("-----------------------");
            }while (option != 3);
        }
    }

    public static Person Login_methods() throws RemoteException {
        String username;
        System.out.println("Insert username");
        username = Ler.umaString();

        if(!(LoginObject.Username(username)))
            return null;

        String password;

        System.out.println("Insert password");
        password = Ler.umaString();

        Person P = LoginObject.Password(username,password);

        if(P != null) {
            System.out.println("Login succeeded! Entering menu...");
            return P;
        }

        return null;
    }

    public static Person Register_Methods() throws RemoteException {
        System.out.println("Insert name: ");
        String name = Ler.umaString();

        System.out.println("-----------------------");


        System.out.println("Insert username: ");
        String username = Ler.umaString();

        System.out.println("-----------------------");

        //see in future implementation to keep input of data until it's right or user cancels operation
        if(LoginObject.Username(username)){
            System.out.println("Username already exists!");
            return null;
        }

        System.out.println("Insert password: ");
        String password = Ler.umaString();

        System.out.println("-----------------------");

        int option;
        String role = "";
        Person P = null;
        do {
            System.out.println("Choose role: ");
            System.out.println("1 - Publisher ");
            System.out.println("2 - Subscriber ");
            System.out.println("3 - Cancel operation");

            option = Ler.umInt();

            switch (option) {
                case 1:
                    role = "Publisher";
                    P = new Publisher(name,password,username,role);
                        break;
                case 2: role = "Subscriber";
                    P = new Subscriber(name,password,username,role);
                        break;

                case 3: return null;

                default:
                    System.out.println("Wrong option. Choose again!");
            }

        }while (option >= 3 || option <= 0);


        System.out.println("-------------------------------------------");

        return P;
    }


    public static News createNews(){
        News n = new News();
        String s;
        System.out.println("Insert topic:");
        int opcao;
        try {
            ArrayList<String> topicsList=NewsObject.consult_Topics();
            for (int i = 0; i < topicsList.size(); i++) {
                System.out.println((i+1) + " - " + topicsList.get(i));
            }
            do{
                opcao=Ler.umInt();

            }while(opcao<0 || opcao > topicsList.size());
            n.setTopic(topicsList.get(opcao-1));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("Insert title:");
        n.setTitle(Ler.umaString());
        do {
            System.out.println("Insert content: (max 180 characters)");
            s = Ler.umaString();
            if(s.length() <= 180)
                break;
            else{
                System.out.println("Maximum of 180 characters reach. Please insert again!");
                System.out.println("Type Yes to continue, No to cancel.");
                s = Ler.umaString();
                if(s.equalsIgnoreCase("no"))
                    return null;
            }
        }while(true);
        n.setContent(s);
        n.setPublisher(user);
        System.out.println(n.toString());
        return n;
    }

    public static Date insertDate(){
        int year,month,day,hours,minutes,seconds;
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        do{
            System.out.println("Year:");
            year = Ler.umInt();
        }while (year < 1970 || year > Calendar.getInstance().get(Calendar.YEAR));
        do {
            System.out.println("Month:");
            month = Ler.umInt();
        }while (month < 1 || (month > Calendar.getInstance().get(Calendar.MONTH) +1
                && year == Calendar.getInstance().get(Calendar.YEAR)) || month > 12);
        do {
            System.out.println("Day:");
            day = Ler.umInt();
        }while (day < 1 || day  > getMonthDays(month,year) ||
                (day > Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH) +1
                        && year == Calendar.getInstance().get(Calendar.YEAR)));
        do {
            System.out.println("Hours:");
            hours = Ler.umInt();
        }while(hours < 0 || hours > 23 ||
                (hours > Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        && day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH) +1
                        && year == Calendar.getInstance().get(Calendar.YEAR)));
        do{
            System.out.println("Minutes:");
            minutes = Ler.umInt();
        }while(minutes < 0 || minutes > 59 ||
                (minutes > Calendar.getInstance().get(Calendar.MINUTE) && hours == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        && day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH) +1
                        && year == Calendar.getInstance().get(Calendar.YEAR)));
        do {
            System.out.println("Seconds:");
            seconds = Ler.umInt();
        }while (seconds < 0 || seconds > 59 ||
                (seconds > Calendar.getInstance().get(Calendar.SECOND) && minutes == Calendar.getInstance().get(Calendar.MINUTE) && hours == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        && day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH) +1
                        && year == Calendar.getInstance().get(Calendar.YEAR)));

        try {
            date = sourceDateFormat.parse(year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static int getMonthDays(int month, int year) {
        int daysInMonth ;
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        }
        else {
            if (month == 2) {
                daysInMonth = (year % 4 == 0) ? 29 : 28;
            } else {
                daysInMonth = 31;
            }
        }
        return daysInMonth;
    }

    public static ArrayList<News> news_from_backup(Date start, Date end, String ip, String port){
        Socket S = null;
        ArrayList<News> newsListFromBackup = new ArrayList<News>();
        try {
            S = new Socket(ip,Integer.parseInt(port));
            ObjectOutputStream os = new ObjectOutputStream(S.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(S.getInputStream());
            os.writeObject(start);
            os.flush();
            os.writeObject(end);
            os.flush();
            newsListFromBackup = (ArrayList<News>) is.readObject();
            os.close();
            is.close();
            return newsListFromBackup;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newsListFromBackup;
    }

    public static ArrayList<News> news_from_backup_publisher(String ip,String port){
        Socket S = null;
        ArrayList<News> newsListFromBackup = new ArrayList<News>();
        try {
            S = new Socket(ip,Integer.parseInt(port));
            ObjectOutputStream os = new ObjectOutputStream(S.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(S.getInputStream());
            os.writeObject(null);
            os.flush();
            os.writeObject(null);
            os.flush();
            os.writeObject(user.getUsername());
            os.flush();
            newsListFromBackup = (ArrayList<News>) is.readObject();
            os.close();
            is.close();
            return newsListFromBackup;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newsListFromBackup;
    }

    public static void view_news_from_topic_timestamp(){
        try {
            ArrayList<String> allTopics;
            allTopics = NewsObject.consult_Topics();
            System.out.println("Choose a topic from the list:");
            System.out.println("0 - Cancel operation.");
            for (int i = 0; i < allTopics.size(); i++) {
                System.out.println((i+1) + " - " + allTopics.get(i)+".");
            }
            int choice=-1;
            do {
                choice= Ler.umInt();
                if((choice < 0 || choice > allTopics.size()))
                    System.out.println("Wrong choice, please choose again.");
                else
                    break;
            }while (true);
            if(choice!=0){
                String topic= allTopics.get(choice-1);
                System.out.println("Insert starting date:");
                Date date1 = insertDate();
                System.out.println("Insert final date:");
                Date date2 = insertDate();
                if(date1.after(date2)){
                    Date aux = date2;
                    date2 = date1;
                    date1 = aux;
                }
                try {
                    ArrayList<News> newsFromTimestamp = NewsObject.news_from_timestamp(date1,date2,topic);
                    if(newsFromTimestamp.size() !=0){
                        for(News n: newsFromTimestamp){
                            System.out.println(n.toString());
                        }
                    }else{
                        System.out.println("There are no news in the main server.");
                    }


                    ArrayList<String> backupIpPort = NewsObject.news_from_timestamp_backup(date1,date2,topic);
                    if(backupIpPort.size() != 0){
                        System.out.println("There are news in the arquive within that timestamp.");
                        System.out.println("Do you want to see it? (Yes or No)");
                        String s = Ler.umaString();
                        if(s.equalsIgnoreCase("yes")){
                            newsFromTimestamp = news_from_backup(date1,date2,backupIpPort.get(0),backupIpPort.get(1));
                            for(News n: newsFromTimestamp){
                                System.out.println(n.toString());
                            }
                        }
                    }else{
                        System.out.println("No news in the arquive in that timestamp.");
                    }
                }catch (RemoteException e){
                    System.out.println(e.getMessage());
                }
            }
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void view_last_news_from_topic(){
        try {
            ArrayList<String> allTopics;
            allTopics = NewsObject.consult_Topics();
            System.out.println("Choose a topic from the list:");
            System.out.println("0 - Cancel operation.");
            for (int i = 0; i < allTopics.size(); i++) {
                System.out.println((i+1) + " - " + allTopics.get(i)+".");
            }
            int choice=-1;
            do {
                choice= Ler.umInt();
                if((choice < 0 || choice > allTopics.size()))
                    System.out.println("Wrong choice, please choose again.");
                else
                    break;
            }while (true);
            if(choice != 0){
                News latestNewsFromTopic = NewsObject.latest_news_from_topic(allTopics.get(choice-1));
                if(latestNewsFromTopic!=null)
                    System.out.println(latestNewsFromTopic);
                else{
                    System.out.println("There are no news with that topic.");
                }
            }
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void showNotificationOnClient(String s) throws RemoteException {
        System.out.println(s);
    }
}
