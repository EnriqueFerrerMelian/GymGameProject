package com.example.gymgameproject.classes;

public class Exercise {
    //ATRIBUTOS
    int id;
    String nombre, musculos, descripcion, categoria, peso, repecitionesYseries, img, tiempo;


    //CONSTRUCTOR
    public Ejercicio(int id, String nombre, String musculos, String descripcion, String categoria, String img) {
        this.id = id;
        this.nombre = nombre;
        this.musculos = musculos;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.img = img;
    }

    public Ejercicio(){ }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getRepecitionesYseries() {
        return repecitionesYseries;
    }

    public void setRepecitionesYseries(String repecitionesYseries) {
        this.repecitionesYseries = repecitionesYseries;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMusculos() {
        return musculos;
    }

    public void setMusculos(String musculos) {
        this.musculos = musculos;
    }


    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Ejercicio{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", musculos='" + musculos + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
