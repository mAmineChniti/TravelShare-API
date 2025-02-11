package tn.esprit.entities;

public class Hotels {
    private int hotel_id;
    private String nom;
    private String adress;
    private String telephone;
    private int capacite_totale;

    // Constructeur par défaut
    public Hotels() {}

    // Constructeur paramétré
    public Hotels(int hotel_id, String nom, String adress, String telephone, int capacite_totale) {
        this.hotel_id = hotel_id;
        this.nom = nom;
        this.adress = adress;
        this.telephone = telephone;
        this.capacite_totale = capacite_totale;
    }

    // Getters et Setters
    public int getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getCapacite_totale() {
        return capacite_totale;
    }

    public void setCapacite_totale(int capacite_totale) {
        this.capacite_totale = capacite_totale;
    }
    @Override
    public String toString() {
        return "Hotels{" +
                "hotel_id=" + hotel_id +
                ", nom='" + nom + '\'' +
                ", adress='" + adress + '\'' +
                ", telephone='" + telephone + '\'' +
                ", capacite_totale=" + capacite_totale +
                '}';
    }
}
