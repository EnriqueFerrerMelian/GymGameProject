package com.example.gymgameproject.classes;

public class Exercise {
    //ATRIBUTOS
    int id;
    String name, muscle, description, category, weight, repetitionsAndSeries, image, time;


    //CONSTRUCTOR
    public Exercise(int id, String name, String muscle, String description, String category, String image) {
        this.id = id;
        this.name = name;
        this.muscle = muscle;
        this.description = description;
        this.category = category;
        this.image = image;
    }

    public Exercise(){ }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRepetitionsAndSeries() {
        return repetitionsAndSeries;
    }

    public void setRepetitionsAndSeries(String repetitionsAndSeries) {
        this.repetitionsAndSeries = repetitionsAndSeries;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", muscle='" + muscle + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", weight='" + weight + '\'' +
                ", repetitionsAndSeries='" + repetitionsAndSeries + '\'' +
                ", image='" + image + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
