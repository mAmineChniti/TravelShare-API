package tn.esprit.tests;

import tn.esprit.entities.ReservationPack;
import tn.esprit.entities.statusReservationPack;
import tn.esprit.services.ReservationPackService;

import java.sql.Date;
import java.util.List;

public class ReservationPackTest {
    public static void main(String[] args) {
        ReservationPackService service = new ReservationPackService();

        // Test for add()
        try {
            ReservationPack newReservation = new ReservationPack();
            newReservation.setClient_id(1);
            newReservation.setPack_id(2);
            newReservation.setDate_reservation(Date.valueOf("2025-02-14"));
            newReservation.setStatus(statusReservationPack.ATTENTE);
            newReservation.setPrix_total(100.50);
            service.add(newReservation);
            System.out.println("Add test passed.");
        } catch (Exception e) {
            System.err.println("Add test failed: " + e.getMessage());
        }

        // Test for update()
        try {
            ReservationPack updatedReservation = new ReservationPack();
            updatedReservation.setReservation_pack_id(1); // Assuming ID 1 exists
            updatedReservation.setClient_id(3);
            updatedReservation.setPack_id(4);
            updatedReservation.setDate_reservation(Date.valueOf("2025-03-01"));
            updatedReservation.setStatus(statusReservationPack.CONFIRMEE);
            updatedReservation.setPrix_total(150.75);
            service.update(updatedReservation);
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
            List<ReservationPack> reservations = service.ListAll();
            if (!reservations.isEmpty()) {
                System.out.println("ListAll test passed. Found " + reservations.size() + " reservations.");
                for (ReservationPack reservation : reservations) {
                    System.out.println(reservation);
                }
            } else {
                System.out.println("ListAll test passed but found no reservations.");
            }
        } catch (Exception e) {
            System.err.println("ListAll test failed: " + e.getMessage());
        }
    }
}
