package com.example.gymgameproject.classes;

import java.util.HashMap;
import java.util.Map;

public class User {
    String id;
    String nombre, clave, usuario, imagen;
    Map<String, Object> rutinas;
    Map<String, Object> peso;
    Map<String, Object> avance;
    Map<String, Object> actividad;

    public Usuario(String id, String nombre,String usuario, String clave,String imagen, Map<String, Object> rutinas,  Map<String, Object>  peso, Map<String, Object>  actividad) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.imagen = imagen;
        this.rutinas = rutinas;
        this.peso = peso;
        this.usuario = usuario;
        this.actividad = actividad;
    }
    public Usuario(){
        this.rutinas = new HashMap<>();
        this.peso = new HashMap<>();
        this.avance = new HashMap<>();
        this.actividad = new HashMap<>();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getActividad() {
        return actividad;
    }

    public void setActividad(Map<String, Object> actividad) {
        this.actividad = actividad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUsuario() {
        return usuario;
    }

    public Map<String, Object> getAvance() {
        return avance;
    }

    public void setAvance(Map<String, Object> avance) {
        this.avance = avance;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Map<String, Object> getPeso() {
        return peso;
    }

    public void setPeso(Map<String, Object> peso) {
        this.peso = peso;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Map<String, Object> getRutinas() {
        return rutinas;
    }

    public void setRutinas(Map<String, Object> rutinas) {
        this.rutinas = rutinas;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", clave='" + clave + '\'' +
                ", usuario='" + usuario + '\'' +
                ", imagen='" + imagen + '\'' +
                ", rutinas=" + rutinas +
                ", peso=" + peso +
                ", avance=" + avance +
                ", actividad=" + actividad +
                '}';
    }
}