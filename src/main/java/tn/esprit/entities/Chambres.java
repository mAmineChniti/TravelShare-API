package tn.esprit.entities;

public class Chambres {
    private int chambre_id;
    private int hotel_id;
    private String numero_chambre;
    private String type_enu;
    private double prix_par_nuit;
    private boolean disponible;
    private String address;

    // Constructeur par défaut
    public Chambres() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Constructeur paramétré
    public Chambres(int chambre_id, int hotel_id, String numero_chambre, String type_enu, double prix_par_nuit, boolean disponible) {
        this.chambre_id = chambre_id;
        this.hotel_id = hotel_id;
        this.numero_chambre = numero_chambre;
        this.type_enu = type_enu;
        this.prix_par_nuit = prix_par_nuit;
        this.disponible = disponible;
    }

    // Getters et Setters
    public int getChambre_id() {
        return chambre_id;
    }

    public void setChambre_id(int chambre_id) {
        this.chambre_id = chambre_id;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getNumero_chambre() {
        return numero_chambre;
    }

    public void setNumero_chambre(String numero_chambre) {
        this.numero_chambre = numero_chambre;
    }

    public String getType_enu() {
        return type_enu;
    }

    public void setType_enu(String type_enu) {
        this.type_enu = type_enu;
    }

    public double getPrix_par_nuit() {
        return prix_par_nuit;
    }

    public void setPrix_par_nuit(double prix_par_nuit) {
        this.prix_par_nuit = prix_par_nuit;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    @Override
    public String toString() {
        return "Chambres{" +
                "chambre_id=" + chambre_id +
                ", hotel_id=" + hotel_id +
                ", numero_chambre='" + numero_chambre + '\'' +
                ", type_enu='" + type_enu + '\'' +
                ", prix_par_nuit=" + prix_par_nuit +
                ", disponible=" + disponible +
                '}';
    }
}
