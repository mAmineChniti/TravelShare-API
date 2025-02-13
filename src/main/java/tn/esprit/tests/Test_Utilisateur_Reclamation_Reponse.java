package tn.esprit.tests;

import tn.esprit.entities.Reclamation;
import tn.esprit.entities.Reponse;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceReclamation;
import tn.esprit.services.ServiceReponse;
import tn.esprit.services.ServiceUtilisateur;

import java.sql.SQLException;
import java.util.List;

    public class Test_Utilisateur_Reclamation_Reponse {
    public static void main(String[] args) {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        ServiceReclamation serviceReclamation = new ServiceReclamation();
        ServiceReponse serviceReponse = new ServiceReponse();

        // Créer des objets Utilisateur avec le rôle de simple utilisateur (role = 0)
        Utilisateur u1 = new Utilisateur(1, "wiem", "ben msahel", "wiem@example.com", "password123@", 23456789, "123 Main St", (byte) 0);
        Utilisateur u2 = new Utilisateur(2, "tttt", "test", "test@example.com", "password456@", 98456123, "456 Elm St", (byte) 0);
        Utilisateur u3 = new Utilisateur(5, "tttt", "tttt", "tttt@example.com", "password456@", 58963741, "456 Elm St", (byte) 0);
        Utilisateur u4 = new Utilisateur(6, "test2", "test2", "tttt2@example.com", "passworddf4@", 58963741, "456 Elm St", (byte) 0);


        // Créer des objets Reclamation
        Reclamation r1 = new Reclamation(1, 1, "pffff", "Le vol réservé a été annulé sans préavis, et aucune alternative n'a été proposée.", java.sql.Date.valueOf("2025-02-12"));
        Reclamation r2 = new Reclamation(2, 2, "Problème d'hôtel", "L'hôtel réservé ne correspondait pas aux photos en ligne, la chambre était sale et le service déplorable.", java.sql.Date.valueOf("2025-02-12"));
        Reclamation r3 = new Reclamation(6, 5, "Excursion annulée à la dernière minute", "L'excursion que j'avais réservée a été annulée à la dernière minute sans explication valable.", java.sql.Date.valueOf("2025-02-12"));

        // Créer des objets Reponse
        Reponse rep1 = new Reponse(1, 1, "Nous avons pris en compte votre réclamation et travaillons à une solution.", java.sql.Date.valueOf("2025-02-12"));
        Reponse rep2 = new Reponse(2, 2, "Un remboursement vous sera accordé dans les 3 jours ouvrables.", java.sql.Date.valueOf("2025-02-13"));
        Reponse rep3 = new Reponse(4, 6, "test", java.sql.Date.valueOf("2025-02-13"));

        try {
            // Ajouter un utilisateur
            //serviceUtilisateur.add(u1);
            //serviceUtilisateur.add(u2);
            //serviceUtilisateur.add(u3);
            serviceUtilisateur.add(u4);

            // Ajouter une réclamation
            //serviceReclamation.add(r1);
            //serviceReclamation.add(r2);
            //serviceReclamation.add(r3);

            // Ajouter une réponse
            //serviceReponse.add(rep1);
            //serviceReponse.add(rep2);
            //serviceReponse.add(rep3);

            // Modifier un utilisateur existant
            //serviceUtilisateur.update(u1);
            //serviceUtilisateur.update(u2);
            //serviceUtilisateur.update(u3);

            // Modifier une réclamation
            //serviceReclamation.update(r1);
            //serviceReclamation.update(r3);

            // Modifier une réponse
            //serviceReponse.update(rep1);
            //serviceReponse.update(rep3);

            // Supprimer un utilisateur par ID
            //int idToDelete = 4; // Remplacez par l'ID à supprimer
            //serviceUtilisateur.delete(idToDelete);

            // Supprimer une reclamation par ID
            //int reclamationIdToDelete = 6;  // ID de la réclamation à supprimer
            //serviceReclamation.delete(reclamationIdToDelete);

            // Supprimer une reponse par ID
            //int reponseIdToDelete = 3;  // ID de la réponse à supprimer
            //serviceReponse.delete(reponseIdToDelete);

            // Afficher tous les utilisateurs
            /*List<Utilisateur> utilisateurs = serviceUtilisateur.ListAll();
            for (Utilisateur utilisateur : utilisateurs) {
                System.out.println(utilisateur);
            }*/

            // Afficher toutes les réclamations
            /*List<Reclamation> reclamations = serviceReclamation.ListAll();
            for (Reclamation reclamation : reclamations) {
                System.out.println(reclamation);
            }*/

            // Afficher toutes les réponses
            /*List<Reponse> reponses = serviceReponse.ListAll();
            for (Reponse reponse : reponses) {
                System.out.println(reponse);
            }*/

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}

