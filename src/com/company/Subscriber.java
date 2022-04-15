package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class Subscriber extends Person implements Serializable {
    private ArrayList<String> subscribedTopics;
    public Subscriber(String name, String password, String username, String role){
        super(name, password, username, role);
        subscribedTopics = new ArrayList<String>();
    }

    public ArrayList<String> getSubscribedTopics() {
        return subscribedTopics;
    }

    public void setSubscribedTopics(ArrayList<String> subscribedTopics) {
        this.subscribedTopics = subscribedTopics;
    }

    public boolean addTopic(String newTopic){
        if(subscribedTopics.contains(newTopic))
            return false;
        subscribedTopics.add(newTopic);
        return true;
    }
}
