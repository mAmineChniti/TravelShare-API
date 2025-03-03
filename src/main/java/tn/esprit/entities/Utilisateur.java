package tn.esprit.entities;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class Utilisateur {
    private int user_id, phone_num;
    private String name, last_name, email, password, address;
    private byte role;
    private byte[] photo;

    // Relation OneToMany avec Reclamation
    // Relation bidirectionnelle avec Reclamation
    private List<Reclamation> reclamations; // Un utilisateur peut avoir plusieurs réclamations

    // Constructeur par défaut
    public Utilisateur() {
        this.role = 0;
        this.photo = loadDefaultPhoto(); // Charger l'image par défaut au lieu de null
    }

    // Constructeur paramétré (y compris l'ID)
    public Utilisateur(int user_id, String name, String last_name, String email, String password, int phone_num, String address) {
        this.user_id = user_id;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone_num = phone_num;
        this.address = address;
        this.role = 0;
        this.photo = loadDefaultPhoto(); // Charger l'image par défaut au lieu de null
    }

    // Constructeur paramétré (sans l'ID)
    public Utilisateur(String name, String last_name, String email, String password, int phone_num, String address) {
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone_num = phone_num;
        this.address = address;
        this.role = 0;
        this.photo = loadDefaultPhoto();
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

    public void setLast_name(String last_name) { this.last_name = last_name; }

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

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo; // Permet de stocker `null` si aucune photo n'est définie
    }


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
                ", photo=" + photo +
                '}';
    }

    // Charger l'image par défaut en byte[]
    private byte[] loadDefaultPhoto() {
        // Utiliser un chemin relatif basé sur les ressources du classpath
        String defaultPhotoPath = "images/default_photo.png"; // Répertoire de l'image dans resources

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(defaultPhotoPath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                System.err.println("Erreur : l'image par défaut n'a pas été trouvée !");
                return null;
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray(); // Retourner l'image en byte[]
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
            return null;
        }
    }

}

