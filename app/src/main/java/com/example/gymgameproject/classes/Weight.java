package com.example.gymgameproject.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Weight {
    //ATRIBUTOS
    List<Map<String, String>> weightData;
    List<String> date;
    String target;
    //CONSTRUCTOR
    public Weight(){
        this.weightData = new ArrayList<>();
        this.date = new ArrayList<>();
    }
    public Weight(List<Map<String, String>>  weightData, List<String> date, String target){
        this.weightData = weightData;
        this.date = date;
        this.target = target;
    }

//GETTERS Y SETTERS

    public List<Map<String, String>> getWeightData() {
        return weightData;
    }

    public void setWeightData(List<Map<String, String>> weightData) {
        this.weightData = weightData;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "weightData=" + weightData +
                ", date=" + date +
                ", target='" + target + '\'' +
                '}';
    }
}
