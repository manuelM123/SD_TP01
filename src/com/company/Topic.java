package com.company;

import java.io.Serializable;

public class Topic implements Serializable {
    private String name;
    private Integer quantity;

    public Topic() {
    }

    /**
     * Constructor for the topics and the quantity of news of that topic in the news's server
     * @param name the topic's name
     * @param quantity the topic's quantity
     */
    public Topic(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * get the name of a topic
     * @return the topic's name
     */
    public String getName() {
        return name;
    }

    /**
     * set the topic's name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get the topic's quantity
     * @return the topic's quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * set the topic's quantity
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
