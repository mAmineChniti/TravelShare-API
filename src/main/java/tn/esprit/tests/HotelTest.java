package tn.esprit.tests;

import tn.esprit.entities.Chambres;
import tn.esprit.entities.Hotels;
import tn.esprit.entities.ReservationHotel;
import tn.esprit.services.ServiceChambre;
import tn.esprit.services.ServiceHotels;
import tn.esprit.services.ServiceReservationHotel;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class HotelTest {
    public static void main(String[] args) {
        testHotelsService();
        testChambresService();
        testReservationHotelService();
    }

    private static void testHotelsService() {
        ServiceHotels service = new ServiceHotels();

        try {
            // Test Create (Insert)
            Hotels newHotel = new Hotels(0, "Hôtel Tunis", "Tunis Centre", "12345678", 100);
            service.add(newHotel);
            System.out.println("Hôtel ajouté avec succès !");

            // Test Read (Select)
            List<Hotels> hotels = service.ListAll();
            System.out.println("Liste des hôtels :");
            for (Hotels hotel : hotels) {
                System.out.println(hotel);
            }

            // Test Update
            if (!hotels.isEmpty()) {
                Hotels hotelToUpdate = hotels.get(0);
                hotelToUpdate.setNom("Hôtel Tunis Modifié");
                hotelToUpdate.setCapacite_totale(150);
                service.update(hotelToUpdate);
                System.out.println("Hôtel mis à jour avec succès !");
            }

            // Test Delete
            if (!hotels.isEmpty()) {
                int hotelIdToDelete = hotels.get(0).getHotel_id();
                service.delete(hotelIdToDelete);
                System.out.println("Hôtel supprimé avec succès !");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors des opérations CRUD pour Hotels : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testChambresService() {
        ServiceChambre service = new ServiceChambre();

        try {
            // Test Create (Insert)
            Chambres newChambre = new Chambres(0, 1, "101", "Simple", 150.0, true);
            service.add(newChambre);
            System.out.println("Chambre ajoutée avec succès !");

            // Test Read (Select)
            List<Chambres> chambres = service.ListAll();
            System.out.println("Liste des chambres :");
            for (Chambres chambre : chambres) {
                System.out.println(chambre);
            }

            // Test Update
            if (!chambres.isEmpty()) {
                Chambres chambreToUpdate = chambres.get(0);
                chambreToUpdate.setType_enu("Double");
                chambreToUpdate.setPrix_par_nuit(200.0);
                service.update(chambreToUpdate);
                System.out.println("Chambre mise à jour avec succès !");
            }

            // Test Delete
            if (!chambres.isEmpty()) {
                int chambreIdToDelete = chambres.get(0).getChambre_id();
                service.delete(chambreIdToDelete);
                System.out.println("Chambre supprimée avec succès !");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors des opérations CRUD pour Chambres : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testReservationHotelService() {
        ServiceReservationHotel service = new ServiceReservationHotel();

        try {
            // Test Create (Insert)
            ReservationHotel newReservation = new ReservationHotel(
                    0, // reservation_hotel_id (auto-incrémenté)
                    1, // client_id (doit exister dans la table clients)
                    1, // chambre_id (doit exister dans la table chambres)
                    new Date(), // date_debut (date actuelle)
                    new Date(System.currentTimeMillis() + 86400000), // date_fin (date actuelle + 1 jour)
                    "confirmer", // status_enu (doit être l'une des valeurs de l'ENUM)
                    200 // prix_totale
            );
            service.add(newReservation);
            System.out.println("Réservation ajoutée avec succès !");

            // Test Read (Select)
            List<ReservationHotel> reservations = service.ListAll();
            System.out.println("Liste des réservations :");
            for (ReservationHotel reservation : reservations) {
                System.out.println(reservation);
            }

            // Test Update
            if (!reservations.isEmpty()) {
                ReservationHotel reservationToUpdate = reservations.get(0);
                reservationToUpdate.setStatus_enu("annuler");
                reservationToUpdate.setPrix_totale(150);
                service.update(reservationToUpdate);
                System.out.println("Réservation mise à jour avec succès !");
            }

            // Test Delete
            if (!reservations.isEmpty()) {
                int reservationIdToDelete = reservations.get(0).getReservation_hotel_id();
                service.delete(reservationIdToDelete);
                System.out.println("Réservation supprimée avec succès !");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors des opérations CRUD pour ReservationHotel : " + e.getMessage());
            e.printStackTrace();
        }
    }
}