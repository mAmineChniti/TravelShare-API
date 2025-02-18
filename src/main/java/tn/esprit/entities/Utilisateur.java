package tn.esprit.entities;

import java.util.List;

public class Utilisateur {
    private int user_id, phone_num;
    private String name, last_name, email, password, address;
    private byte role;

    // Relation OneToMany avec Reclamation
    // Relation bidirectionnelle avec Reclamation
    private List<Reclamation> reclamations; // Un utilisateur peut avoir plusieurs réclamations

    // Constructeur par défaut
    public Utilisateur() {}

    // Constructeur paramétré (y compris l'ID)
    public Utilisateur(int user_id, String name, String last_name, String email, String password, int phone_num, String address, byte role) {
        this.user_id = user_id;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone_num = phone_num;
        this.address = address;
        this.role = role;
    }

    // Constructeur paramétré (sans l'ID)
    public Utilisateur(String name, String last_name, String email, String password, int phone_num, String address, byte role) {
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone_num = phone_num;
        this.address = address;
        this.role = role;
    }

    // Getters et Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getLast_name() { return last_name; }

    public void setLast_name(String lastname) { this.last_name = lastname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public int getPhone_num() { return phone_num; }

    public void setPhone_num(int phone_num) { this.phone_num = phone_num; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public byte getRole() { return role; }

    public void setRole(byte role) { this.role = role; }

    // Constructeurs, getters et setters (inclure reclamations)
    public List<Reclamation> getReclamations() { return reclamations; }

    public void setReclamations(List<Reclamation> reclamations) { this.reclamations = reclamations; }

    // Méthode toString pour afficher les informations de l'utilisateur
    @Override
    public String toString() {
        return "Utilisateur{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone_num=" + phone_num + '\'' +
                ", address='" + address + '\'' +
                ", role=" + role +
                '}';
    }
}

