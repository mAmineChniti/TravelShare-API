package tn.esprit.tests;

import tn.esprit.entities.Excursions;
import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceExcursion;
import tn.esprit.services.ServiceGuide;

import java.sql.Date;
import java.sql.SQLException;

public class TestGuidesExcursions {
    public static void main(String[] args) {
        ServiceGuide serviceGuide = new ServiceGuide();
        ServiceExcursion serviceExcursion = new ServiceExcursion();

        //ajout de 2 guides
        Guides guide1 = new Guides(2,"wafa","chabbi","wafachabbi","24587619","Francais");
        Guides guide2 = new Guides(2,"Amira","chabbi","amirachabbi","24887619","Francais");
        //2 guides a modifier
        Guides guide3 = new Guides(2,3,"Amina","chabbi","aminachabbi21@gmail.com","29854619","Francais");
        Guides guide4 = new Guides(5,5,"Wafa","chebbi","wafachaebbi22@gmail.com","98613243","Anglais");

        //ajout de 2 excursion
        Date dateExcursion1 = Date.valueOf("2024-10-20");
        Excursions excursion1 = new Excursions(17,"Randonnée au sommet de l'Atlas","Découvrez" +
                " les paysages époustouflants du massif de" +
                " l'Atlas lors d'une randonnée guidée. Profitez d'une vue panoramique" +
                " sur les vallées et les villages berbères, avec une pause déjeuner en pleine nature.",dateExcursion1,3);

        Date dateExcursion2 = Date.valueOf("2025-01-20");
        Excursions excursion2 = new Excursions(15,"Randonnée en montagne","Randonnée à travers les sentiers montagneux, avec un guide expérimenté qui vous expliquera la faune et la flore locales"
                ,dateExcursion1,5);

        //excursion a modifier
        Date dateExcursion3 = Date.valueOf("2025-01-10");
        Excursions excursion3 = new Excursions(1,10,"Randonnée en montagne","Randonnée à travers les sentiers montagneux, avec un guide expérimenté qui vous expliquera la faune et la flore locales"
                ,dateExcursion3,2);

        try {
            //ajout de 2 guide
            serviceGuide.add(guide1);
            serviceGuide.add(guide2);

            //modifer guide
            serviceGuide.update(guide3);
            serviceGuide.update(guide4);

            //supprimer guide d'id 4
            int idToDelete = 2;
            serviceGuide.delete(idToDelete);

            //afficher list de guide
            System.out.println(serviceGuide.ListAll());


            //ajout de 2 excursion
            serviceExcursion.add(excursion1);
            serviceExcursion.add(excursion2);

            //modifier excursion
            serviceExcursion.update(excursion3);

            //supprimer excursion d'id 2
            idToDelete = 2;
            serviceExcursion.delete(idToDelete);

            //afficher list d'excursion
            System.out.println(serviceExcursion.ListAll());



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}