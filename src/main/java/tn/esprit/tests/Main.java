package tn.esprit.tests;

import tn.esprit.entities.Guides;
import tn.esprit.services.ServiceGuide;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServiceGuide serviceGuide = new ServiceGuide();

        Guides guide1 = new Guides(2,"wafa","chabbi","wafachabbi","24587619","Francais");
        Guides guide2 = new Guides(2,"Amira","chabbi","amirachabbi","24887619","Francais");
      Guides guide3 = new Guides(2,3,"Amina","chabbi","aminachabbi","29854619","Francais");

        try {
          //  serviceGuide.add(guide1);
            //serviceGuide.add(guide2);
            // serviceGuide.update(guide3);
            int idToDelete = 4;
            serviceGuide.delete(idToDelete);
            System.out.println(serviceGuide.ListAll());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}