package tn.esprit.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;


public class Excursions {
    private int excursion_id;
    private String title, description , image;
    private Date date_excursion , date_fin;
    private double prix;
    private int guide_id;

    public Excursions() {

    }

    public Excursions(int excursion_id, Date date_fin, String title,String description, Date date_excursion, int guide_id,String image, double prix) {
        this.excursion_id = excursion_id;
        this.date_fin = date_fin;
        this.title = title;
        this.description = description;
        this.date_excursion = date_excursion;
        this.guide_id = guide_id;
        this.image = image;
        this.prix = prix;
    }

    public Excursions(Date date_fin , String title,String description, Date date_excursion, int guide_id,String image, double prix) {
        this.date_fin = date_fin;
        this.title = title;
        this.description = description;
        this.date_excursion = date_excursion;
        this.guide_id = guide_id;
        this.image = image;
        this.prix = prix;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Date getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
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
                ", date_fin=" + date_fin +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_excursion=" + date_excursion +
                ", image='" + image + '\'' +
                ", prix='" + prix + '\'' +
                '}';
    }
}
