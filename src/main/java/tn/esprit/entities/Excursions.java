package tn.esprit.entities;

import java.util.Date;

public class Excursions {
    private int id_excursion, duration;
    private String title, description;
    private Date date_excursion;

    public Excursions() {

    }

    public Excursions(int id_excursion, int duration, String title, String description, Date date_excursion) {
        this.id_excursion = id_excursion;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.date_excursion = date_excursion;
    }

    public Excursions(int duration, String title, String description, Date date_excursion) {
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.date_excursion = date_excursion;
    }

    public int getId_excursion() {
        return id_excursion;
    }

    public void setId_excursion(int id_excursion) {
        this.id_excursion = id_excursion;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_excursion() {
        return date_excursion;
    }

    public void setDate_excursion(Date date_excursion) {
        this.date_excursion = date_excursion;
    }

    @Override
    public String toString() {
        return "Excursions{" +
                "id_excursion=" + id_excursion +
                ", duration=" + duration +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_excursion=" + date_excursion +
                '}';
    }
}
