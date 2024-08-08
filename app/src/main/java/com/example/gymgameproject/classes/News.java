package com.example.gymgameproject.classes;

public class News {
    String imagen, titulo, subtitulo, contenido;
    String[] caracteresEspeciales = {"Á","É","Í","Ó","Ú","Ñ","á","é","í","ñ","ó","ú"};
    String[] caracteresNoEspeciales = {"A","E","I","O","U","N","a","e","i","n","o","u"};

    public News(String imagen, String titulo, String subtitulo, String contenido) {
        this.imagen = imagen;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.contenido = contenido;
    }

    public News() { }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }
    public String getTituloTrans(){
        return reemplazar(titulo);
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    @Override
    public String toString() {
        return "Noticia{" +
                "imagen='" + imagen + '\'' +
                ", titulo='" + titulo + '\'' +
                ", subtitulo='" + subtitulo + '\'' +
                ", contenido='" + contenido + '\'' +
                '}';
    }

    /**
     * transforma el título para que elimine algunos caracteres especiales y lo transforme a
     * minúsculas
     * @param secuencia Texto a transformar
     * @return secuencia Texto transformado
     */
    public String reemplazar(String secuencia){
        for (int i = 0; i < caracteresNoEspeciales.length; i++) {
            secuencia.replace(caracteresNoEspeciales[i], caracteresEspeciales[i]);
        }

        return secuencia.toLowerCase();
    }
}
