package com.example.gymgameproject.classes;

import java.util.ArrayList;
import java.util.List;

public class Event {
    String nombre, precio, descripcion, vacantes, profesor, horario, img1, img2, fecha;
    List<String> dias;

    public Evento(){
        this.dias = new ArrayList<>();
    }

    public Evento(String nombre, String precio, String descripcion, String vacantes, String profesor, String horario) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.vacantes = vacantes;
        this.profesor = profesor;
        this.horario = horario;
        this.dias = new ArrayList<>();
    }

    public List<String> getDias() {
        return dias;
    }

    public void setDias(List<String> dias) {
        this.dias = dias;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVacantes() {
        return vacantes;
    }

    public void setVacantes(String vacantes) {
        this.vacantes = vacantes;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "nombre='" + nombre + '\'' +
                ", precio='" + precio + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", vacantes='" + vacantes + '\'' +
                ", profesor='" + profesor + '\'' +
                ", horario='" + horario + '\'' +
                ", img1='" + img1 + '\'' +
                ", img2='" + img2 + '\'' +
                ", fecha='" + fecha + '\'' +
                ", dias=" + dias +
                '}';
    }
}
