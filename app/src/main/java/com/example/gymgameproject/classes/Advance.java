package com.example.gymgameproject.classes;

import java.util.ArrayList;
import java.util.List;

public class Advance {
    //ATRIBUTOS
    List<String> exercisesName;
    List<String> weights;
//CONSTRUCTOR

    public Advance() {
        this.exercisesName = new ArrayList<>();
        this.weights = new ArrayList<>();
    }

    public Advance(List<String> exercisesName, List<String> weights) {
        this.exercisesName = exercisesName;
        this.weights = weights;
    }


//GETTERS Y SETTERS

    public List<String> getExercisesName() {
        return exercisesName;
    }

    public void setExercisesName(List<String> exercisesName) {
        this.exercisesName = exercisesName;
    }

    public List<String> getWeights() {
        return weights;
    }

    public void setWeights(List<String> weights) {
        this.weights = weights;
    }

    @Override
    public String toString() {
        return "Advance{" +
                "exercisesName=" + exercisesName +
                ", weights=" + weights +
                '}';
    }
}
