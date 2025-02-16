package tn.esprit.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;


public class Excursions {
    private int excursion_id, duration;
    private String title, description;
    private Date date_excursion;
    private int guide_id;

    public Excursions() {

    }

    public Excursions(int excursion_id, int duration, String title, String description, Date date_excursion, int guide_id) {
        this.excursion_id = excursion_id;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.date_excursion = date_excursion;
        this.guide_id = guide_id;
    }

    public Excursions(int duration, String title, String description, Date date_excursion, int guide_id) {
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.date_excursion = date_excursion;
        this.guide_id = guide_id;
    }

    public int getGuide_id() {
        return guide_id;
    }

    public void setGuide_id(int guide_id) {
        this.guide_id = guide_id;
    }

    public int getExcursion_id() {
        return excursion_id;
    }

    public void setExcursion_id(int id_excursion) {
        this.excursion_id = id_excursion;
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
    public static LocalDate convertStringToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateStr, formatter); // Conversion String -> LocalDate
    }

    @Override
    public String toString() {
        return "Excursions{" +
                "excursion_id=" + excursion_id +
                "id_guide=" + guide_id +
                ", duration=" + duration +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_excursion=" + date_excursion +
                '}';
    }
}
