package com.example.gymgameproject.classes;

import java.util.ArrayList;
import java.util.List;

public class Activity  {
    String name, price, description, vacancies, teacher, schedule, img1, img2, date;
    List<String> days;

    public Activity(){
        this.days = new ArrayList<>();
    }

    public Activity(String name, String price, String description, String vacancies, String teacher, String schedule) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.vacancies = vacancies;
        this.teacher = teacher;
        this.schedule = schedule;
        this.days = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVacancies() {
        return vacancies;
    }

    public void setVacancies(String vacancies) {
        this.vacancies = vacancies;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", vacancies='" + vacancies + '\'' +
                ", teacher='" + teacher + '\'' +
                ", schedule='" + schedule + '\'' +
                ", img1='" + img1 + '\'' +
                ", img2='" + img2 + '\'' +
                ", date='" + date + '\'' +
                ", days=" + days +
                '}';
    }
}
