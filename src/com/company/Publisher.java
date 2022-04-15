package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class Publisher extends Person implements Serializable {
    //private ArrayList<News> News_Published;
    public Publisher(String name, String password, String username, String role){
        super(name, password, username, role);
    }
}
