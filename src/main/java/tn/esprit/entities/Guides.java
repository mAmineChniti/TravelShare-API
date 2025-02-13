package tn.esprit.entities;

public class Guides {
    private int guide_id, experience;
    private String name, lastname, email, phone_num, language;
    private Excursions excursions;

    public Guides(){
    }

    public Guides(int experience, String name, String lastname, String email, String phone_num, String language) {
        this.experience = experience;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone_num = phone_num;
        this.language = language;
    }

    public Guides(int guide_id, int experience, String name, String lastname, String email, String phone_num, String language) {
        this.guide_id = guide_id;
        this.experience = experience;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone_num = phone_num;
        this.language = language;
    }

    public int getGuide_id() {
        return guide_id;
    }

    public void setGuide_id(int guide_id) {
        this.guide_id = guide_id;
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
                "guide_id=" + guide_id +
                ", experience=" + experience +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone_num='" + phone_num + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
