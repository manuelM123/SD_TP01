package com.company;

import com.myinputs.Ler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;


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

                        break;

                    case 2:

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

        do {
            System.out.println("Choose role: ");
            System.out.println("1 - Publisher ");
            System.out.println("2 - Subscriber ");
            System.out.println("3 - Cancel operation");

            option = Ler.umInt();

            switch (option) {
                case 1: role = "Publisher";
                        break;
                case 2: role = "Subscriber";
                        break;

                case 3: return null;

                default:
                    System.out.println("Wrong option. Choose again!");
            }

        }while (option >= 3 || option <= 0);

        Person P = new Person(name,password,username,role);

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
}
