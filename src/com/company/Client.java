package com.company;

import com.myinputs.Ler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Client {
    /*private static void createGUI(){
        JFrame Login_Menu = new JFrame("Login Menu");
        JPanel panel = new JPanel();

        JTextField J = new JTextField("Insert Username");
        JTextField J1 = new JTextField("Insert Password");
        J.setBounds(90,100, 200,30);
        J1.setBounds(90,150, 200,30);

        Login_Menu.add(J);
        Login_Menu.add(J1);

        J.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                J.setText("");
            }
        });

        J1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                J1.setText("");
            }
        });

        Login_Menu.setSize(400,400);
        Login_Menu.setLayout(null);
        Login_Menu.setVisible(true);
    }*/
    RMIInterfaceLogin LoginObject;
    RMIInterfaceNews NewsObject;
    Person user = null;

    public Client(){
        System.setSecurityManager(new SecurityManager());
        try{
            //method to bind server object to object in client (shared remote object)
            LoginObject = (RMIInterfaceLogin) Naming.lookup("RMIImplLogin");
            NewsObject = (RMIInterfaceNews) Naming.lookup("RMIImplNews");

            int option;
            do{
                Login_menu();
                option = Ler.umInt();

                switch (option){
                    //Login method
                    case 1:
                        user = Login_methods();
                        if(user != null)
                            Menu_Role(user.getRole());
                        else
                            System.out.println("Wrong credentials! Retry login.");

                        break;
                    //Register method
                    case 2:
                        user = Register_Methods();
                        if(user != null) {
                            LoginObject.Register(user);
                            Menu_Role(user.getRole());
                        }
                        else
                            System.out.println("Failed to register!");
                        break;
                    //View news method
                    case 3:

                        break;

                    case 4:
                        break;

                    default:
                        System.out.println("Invalid option! Insert again!");
                        break;

                }
            }while(option != 4);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client C = new Client();
    }

    public void Login_menu(){
        System.out.println("1- Login");
        System.out.println("2- Register");
        System.out.println("3- View news");
        System.out.println("4- Exit");
        System.out.println("-----------------------");
    }

    public void Menu_Role(String role){
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
                        break;

                    case 5:
                        System.out.println("Returning to login menu.");
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
                        int choice=-1;
                        try{
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
                                if(choice == 0)
                                    break;
                                else if((choice < 0 || choice > currentTopics.size()) && subscribedTopics.contains(currentTopics.get(choice-1)))
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
                        Date date = insertDate();

                        break;

                    case 3:

                        break;

                    case 4:
                        System.out.println("Returning to login menu.");
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

                switch (option) {
                    case 1:

                        break;

                    case 2:

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

    public Person Login_methods() throws RemoteException {
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

    public Person Register_Methods() throws RemoteException {
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

    /*public ArrayList<String> Credentials_menu(){
        System.out.println("Insert Username");
        String username = Ler.umaString();

        System.out.println("Insert Password");
        String password = Ler.umaString();

        ArrayList<String> credentials = new ArrayList<String>();
        credentials.add(username);
        credentials.add(password);

        return credentials;
    }*/

    public News createNews(){
        News n = new News();
        String s;
        System.out.println("Insert topic:");
        n.setTopic(Ler.umaString());
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

    public Date insertDate(){
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
        }while (month < 1 || (month > Calendar.getInstance().get(Calendar.MONTH)
                && year == Calendar.getInstance().get(Calendar.YEAR)) || month > 12);
        do {
            System.out.println("Day:");
            day = Ler.umInt();
        }while (day < 1 || day  > getMonthDays(month,year) ||
                (day > Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH)
                        && year == Calendar.getInstance().get(Calendar.YEAR)));
        do {
            System.out.println("Hours:");
            hours = Ler.umInt();
        }while(hours < 0 || hours > 23 ||
                (hours > Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        && day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH)
                        && year == Calendar.getInstance().get(Calendar.YEAR)));
        do{
            System.out.println("Minutes:");
            minutes = Ler.umInt();
        }while(minutes < 0 || minutes > 59 ||
                (minutes > Calendar.getInstance().get(Calendar.MINUTE) && hours == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        && day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH)
                        && year == Calendar.getInstance().get(Calendar.YEAR)));
        do {
            System.out.println("Seconds:");
            seconds = Ler.umInt();
        }while (seconds < 0 || seconds > 59 ||
                (seconds > Calendar.getInstance().get(Calendar.SECOND) && minutes == Calendar.getInstance().get(Calendar.MINUTE) && hours == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        && day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && month == Calendar.getInstance().get(Calendar.MONTH)
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
}
