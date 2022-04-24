package com.company;

import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private String password;
    private String username;
    private String role;

    private static final long serialVersionUID = 1L;

    public Person() {

    }

    /**
     * Constructor for the person
     * @param name the name
     * @param password the password
     * @param username the username
     * @param role the role
     */
    public Person(String name, String password, String username, String role) {
        this.name = name;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    /**
     * Get the person's name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * set the person's name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get the person's password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * set the person's password
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * get the person's username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * set the person's username
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * get the person's role
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * set the person's role
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
