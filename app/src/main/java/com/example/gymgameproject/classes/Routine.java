package com.example.gymgameproject.classes;

import java.util.List;

public class Routine {
    //ATRIBUTOS
    String nombre, id, img, referencia;
    List<String> dias;
    List<Ejercicio> ejercicios;

//CONSTRUCTOR

    public Rutina(String id, String nombre, String img, List<Ejercicio> ejercicios, List<String> dias) {
        this.id = id;
        this.nombre = nombre;
        this.img = img;
        this.ejercicios = ejercicios;
        this.dias = dias;
        referencia = null;
    }

    public Rutina(){}
//GETTERS Y SETTERS


    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public List<String> getDias() {
        return dias;
    }

    public void setDias(List<String> dias) {
        this.dias = dias;
    }

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Rutina{" +
                "nombre='" + nombre + '\'' +
                ", id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", referencia='" + referencia + '\'' +
                ", dias=" + dias +
                ", ejercicios=" + ejercicios +
                '}';
    }
}
