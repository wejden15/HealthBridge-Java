package tn.esprit.service;

import tn.esprit.entity.Evenement;
import java.util.List;

public interface IEvenementService {
    void ajouterEvenement(Evenement evenement);
    void supprimerEvenement(int id);
    void modifierEvenement(Evenement evenement);
    List<Evenement> rechercherParCategorie(String categorie);
    List<Evenement> getAllEvenements();
}
