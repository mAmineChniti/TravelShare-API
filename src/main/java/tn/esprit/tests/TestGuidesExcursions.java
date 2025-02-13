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
        Guides guide1 = new Guides(2,"Wafa","Chabbi","Wafachabbi@gmail.com","24587619","Francais");
        Guides guide2 = new Guides(3,"Amira","Chabbi","amirachabbi@gmail.com","24887619","Anglais");
        Guides guide3 = new Guides(1,"Firas","Chabbi","firaschabbi@gmail.com","52001486","Anglais");

        //2 guides a modifier
        Guides guide4 = new Guides(2,5,"Amina","Chabbi","aminachabbi@gmail.com","29854619","Anglais");

        //ajout de 2 excursion
        Date dateExcursion1 = Date.valueOf("2025-04-26");
        Excursions excursion1 = new Excursions(17,"Randonnée au sommet de l'Atlas","Découvrez" +
                " les paysages époustouflants du massif de" +
                " l'Atlas lors d'une randonnée guidée. Profitez d'une vue panoramique" +
                " sur les vallées et les villages berbères, avec une pause déjeuner en pleine nature.",dateExcursion1,3);

        Date dateExcursion2 = Date.valueOf("2025-01-20");
        Excursions excursion2 = new Excursions(19,"Randonnée en montagne","Randonnée à travers les sentiers montagneux, avec un guide expérimenté qui vous expliquera la faune et la flore locales"
                ,dateExcursion2,1);

        Date dateExcursion4 = Date.valueOf("2025-01-20");
       Excursions excursion4 = new Excursions(5,"Randonnée en montagne","Randonnée à travers les sentiers montagneux, avec un guide expérimenté qui vous expliquera la faune et la flore locales"
                ,dateExcursion4,5);

        //excursion a modifier
        Date dateExcursion3 = Date.valueOf("2025-01-11");
        Excursions excursion3 = new Excursions(3,11,"Randonnée en montagne","Randonnée à travers les sentiers montagneux, avec un guide expérimenté qui vous expliquera la faune et la flore locales"
                ,dateExcursion3,8);


        //test
       // Guides guide5 = new Guides(4, "Sami", "Trabelsi", "samitrabelsi45@gmail.com", "24789653", "Italien");
        // Guides guide6 = new Guides(3, "Ahmed", "Ben Ali", "ahmedbenali23@gmail.com", "29874562", "Anglais");

        //Guides guide8 = new Guides(5, "Nour", "Haddad", "nourhaddad30@gmail.com", "25987412", "Espagnol");

        /*
        Date dateExcursion5 = Date.valueOf("2025-03-15");
        Excursions excursion5 = new Excursions(6, "Visite historique de la médina",
                "Découvrez l'histoire fascinante de la médina avec un guide local, en explorant les ruelles anciennes et les monuments emblématiques.",
                dateExcursion5, 7);

         Date dateExcursion6 = Date.valueOf("2025-04-10");
         Excursions excursion6 = new Excursions(7, "Safari dans le désert",
          "Explorez les dunes du désert en 4x4 avec un guide expert, suivi d'un dîner traditionnel sous les étoiles.",
              dateExcursion6, 8);
                  */

        try {
            //ajout de 2 guide
            serviceGuide.add(guide1);
            serviceGuide.add(guide2);
            serviceGuide.add(guide3);

            //modifer guide
            serviceGuide.update(guide4);

            //supprimer guide d'id 4
           int idToDelete = 1;
           serviceGuide.delete(idToDelete);

            //afficher list de guide
            System.out.println(serviceGuide.ListAll());



            //ajout de 2 excursion
              serviceExcursion.add(excursion1);
              serviceExcursion.add(excursion2);
              serviceExcursion.add(excursion4);

            //modifier excursion
            serviceExcursion.update(excursion3);

            //supprimer excursion d'id 2
          //  idToDelete = 2;
           // serviceExcursion.delete(idToDelete);

            //afficher list d'excursion
            System.out.println(serviceExcursion.ListAll());



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}