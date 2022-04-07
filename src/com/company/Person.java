package com.company;

public class Person {
    private String name;
    private String password;
    private String username;
    private String role;

    public Person() {

    }

    public Person(String name, String password, String username, String role) {
        this.name = name;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
