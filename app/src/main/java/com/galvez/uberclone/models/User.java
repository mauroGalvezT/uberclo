package com.galvez.uberclone.models;

public class User {
    String Id;
    String name;
    String email;
    public User(String id, String name, String email) {
        Id = id;
        this.name = name;
        this.email = email;
    }

    public User() {

    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
