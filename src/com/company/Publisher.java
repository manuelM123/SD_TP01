package com.company;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Subclass of Person for the publisher
 */
public class Publisher extends Person implements Serializable {
    public Publisher(String name, String password, String username, String role){
        super(name, password, username, role);
    }
}
