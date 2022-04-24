package com.company;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Subclass of Person for the subscriber
 */
public class Subscriber extends Person implements Serializable {
    private ArrayList<String> subscribedTopics;
    public Subscriber(String name, String password, String username, String role){
        super(name, password, username, role);
        subscribedTopics = new ArrayList<String>();
    }

    /**
     * get the subscribed topics
     * @return the list of subscribed topics
     */
    public ArrayList<String> getSubscribedTopics() {
        return subscribedTopics;
    }

    /**
     * set the subscribed topics
     * @param subscribedTopics the list of subscribed topics
     */
    public void setSubscribedTopics(ArrayList<String> subscribedTopics) {
        this.subscribedTopics = subscribedTopics;
    }

    /**
     * add a topic to the subscriber
     * @param newTopic the topic to add
     * @return true if added or false if the subscriber is already subscribed to that topic
     */
    public boolean addTopic(String newTopic){
        if(subscribedTopics.contains(newTopic))
            return false;
        subscribedTopics.add(newTopic);
        return true;
    }
}
