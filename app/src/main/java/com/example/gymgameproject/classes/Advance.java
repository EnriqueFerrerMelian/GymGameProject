package com.example.gymgameproject.classes;

import java.util.ArrayList;
import java.util.List;

public class Advance {
    //ATRIBUTOS
    List<String> ejerciciosNombres;
    List<String> pesos;
//CONSTRUCTOR

    public Avance() {
        this.ejerciciosNombres = new ArrayList<>();
        this.pesos = new ArrayList<>();
    }

    public Avance(List<String> ejerciciosNombres, List<String> pesos) {
        this.ejerciciosNombres = ejerciciosNombres;
        this.pesos = pesos;
    }


//GETTERS Y SETTERS


    public List<String> getEjerciciosNombres() {
        return ejerciciosNombres;
    }

    public void setEjerciciosNombres(List<String> ejerciciosNombres) {
        this.ejerciciosNombres = ejerciciosNombres;
    }

    public List<String> getPesos() {
        return pesos;
    }

    public void setPesos(List<String> pesos) {
        this.pesos = pesos;
    }

    @Override
    public String toString() {
        return "Avance{" +
                "ejerciciosNombres=" + ejerciciosNombres +
                ", pesos=" + pesos +
                '}';
    }
}
