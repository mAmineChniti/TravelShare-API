package tn.esprit.entities;

public class Guide {
    private int id_guide, experience;
    private String name, lastname, email, phone_num, language;

    public  Guide(){
    }

    public Guide(int experience, String name, String lastname, String email, String phone_num, String language) {
        this.experience = experience;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone_num = phone_num;
        this.language = language;
    }

    public Guide(int id_guide, int experience, String name, String lastname, String email, String phone_num, String language) {
        this.id_guide = id_guide;
        this.experience = experience;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone_num = phone_num;
        this.language = language;
    }

    public int getId_guide() {
        return id_guide;
    }

    public void setId_guide(int id_guide) {
        this.id_guide = id_guide;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Guide{" +
                "id_guide=" + id_guide +
                ", experience=" + experience +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone_num='" + phone_num + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
