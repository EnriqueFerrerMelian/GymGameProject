package com.example.gymgameproject.classes;

import java.util.HashMap;
import java.util.Map;

public class User {
    String id;
    String name, password, user, image;
    Map<String, Object> routines;
    Map<String, Object> weight;
    Map<String, Object> advance;
    Map<String, Object> activity;

    public User(String id, String name, String password, String user, String image) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.user = user;
        this.image = image;
        this.routines = new HashMap<>();
        this.weight = new HashMap<>();
        this.advance = new HashMap<>();
        this.activity = new HashMap<>();
    }

    public User(){
        this.routines = new HashMap<>();
        this.weight = new HashMap<>();
        this.advance = new HashMap<>();
        this.activity = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, Object> getRoutines() {
        return routines;
    }

    public void setRoutines(Map<String, Object> routines) {
        this.routines = routines;
    }

    public Map<String, Object> getWeight() {
        return weight;
    }

    public void setWeight(Map<String, Object> weight) {
        this.weight = weight;
    }

    public Map<String, Object> getAdvance() {
        return advance;
    }

    public void setAdvance(Map<String, Object> advance) {
        this.advance = advance;
    }

    public Map<String, Object> getActivity() {
        return activity;
    }

    public void setActivity(Map<String, Object> activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", user='" + user + '\'' +
                ", image='" + image + '\'' +
                ", routines=" + routines +
                ", weight=" + weight +
                ", advance=" + advance +
                ", activity=" + activity +
                '}';
    }
}