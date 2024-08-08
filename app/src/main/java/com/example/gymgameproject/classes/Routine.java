package com.example.gymgameproject.classes;

import java.util.ArrayList;
import java.util.List;

public class Routine {
    //ATRIBUTOS
    String name, id, image, reference;
    List<String> days;
    List<Exercise> exercises;

//CONSTRUCTOR

    public Routine(String id, String name, String image, List<Exercise> exercises, List<String> days) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.days = new ArrayList<>();
        this.exercises = new ArrayList<>();
        reference = null;
    }

    public Routine(){
        this.days = new ArrayList<>();
        this.exercises = new ArrayList<>();
    }
//GETTERS Y SETTERS


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @Override
    public String toString() {
        return "Routine{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", reference='" + reference + '\'' +
                ", days=" + days +
                ", exercises=" + exercises +
                '}';
    }
}
