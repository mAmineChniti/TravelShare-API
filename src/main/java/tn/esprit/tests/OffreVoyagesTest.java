package tn.esprit.tests;

import tn.esprit.entities.OffreVoyages;
import tn.esprit.services.OffreVoyageService;

import java.sql.Date;
import java.util.List;

public class OffreVoyagesTest {
    public static void main(String[] args) {
        OffreVoyageService service = new OffreVoyageService();

        // Test for add()
        try {
            OffreVoyages newOffre = new OffreVoyages();
            newOffre.setTitre("Summer Escape");
            newOffre.setDestination("Maldives");
            newOffre.setDescription("A relaxing vacation in the Maldives with all-inclusive services.");
            newOffre.setDate_depart(Date.valueOf("2025-06-01"));
            newOffre.setDate_retour(Date.valueOf("2025-06-10"));
            newOffre.setPrix(2500.75);
            newOffre.setPlaces_disponibles(20);
            service.add(newOffre);
            System.out.println("Add test passed.");
        } catch (Exception e) {
            System.err.println("Add test failed: " + e.getMessage());
        }

        // Test for update()
        try {
            OffreVoyages updatedOffre = new OffreVoyages();
            updatedOffre.setOffres_voyage_id(1); // Assuming ID 1 exists
            updatedOffre.setTitre("Winter Wonderland");
            updatedOffre.setDestination("Switzerland");
            updatedOffre.setDescription("A snowy adventure in Switzerland with guided tours.");
            updatedOffre.setDate_depart(Date.valueOf("2025-12-15"));
            updatedOffre.setDate_retour(Date.valueOf("2025-12-25"));
            updatedOffre.setPrix(3000.50);
            updatedOffre.setPlaces_disponibles(15);
            service.update(updatedOffre);
            System.out.println("Update test passed.");
        } catch (Exception e) {
            System.err.println("Update test failed: " + e.getMessage());
        }

        // Test for delete()
        try {
            int deleteId = 2; // Assuming ID 2 exists
            service.delete(deleteId);
            System.out.println("Delete test passed.");
        } catch (Exception e) {
            System.err.println("Delete test failed: " + e.getMessage());
        }

        // Test for ListAll()
        try {
            List<OffreVoyages> voyages = service.ListAll();
            if (!voyages.isEmpty()) {
                System.out.println("ListAll test passed. Found " + voyages.size() + " voyages.");
                for (OffreVoyages voyage : voyages) {
                    System.out.println(voyage);
                }
            } else {
                System.out.println("ListAll test passed but found no voyages.");
            }
        } catch (Exception e) {
            System.err.println("ListAll test failed: " + e.getMessage());
        }
    }
}