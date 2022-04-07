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
    Person user = null;

    public Client(){
        System.setSecurityManager(new SecurityManager());
        try{
            //method to bind server object to object in client (shared remote object)
            LoginObject = (RMIInterfaceLogin) Naming.lookup("RMIImplLogin");

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
            while(true){

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

    }

    public void Login_menu(){
        System.out.println("1- Login");
        System.out.println("2- Register");
        System.out.println("3- View news");
        System.out.println("4- Exit");
        System.out.println("-----------------------");
    }

    public void Menu_Role(String role){
        if(role.equals("Publisher")){
            System.out.println("1- Add a topic");
            System.out.println("2- View existing topics");
            System.out.println("3- Insert news from a topic");
            System.out.println("4- View every published news");
            System.out.println("5- Logout");
            System.out.println("-----------------------");
        }
        else if(role.equals("Subscriber")){
            System.out.println("1- Subscribe a topic");
            System.out.println("2- View news from a topic in a timestamp");
            System.out.println("3- View last news from a topic");
            System.out.println("4- Logout");
            System.out.println("-----------------------");
        } else{
            System.out.println("1- View news from a topic in a timestamp");
            System.out.println("2- View last news from a topic");
            System.out.println("3- Logout");
            System.out.println("-----------------------");
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
}
