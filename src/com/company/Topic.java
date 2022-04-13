package com.company;

import java.io.Serializable;

public class Topic implements Serializable {
    private String name;
    private Integer quantity;

    public Topic() {
    }

    public Topic(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
