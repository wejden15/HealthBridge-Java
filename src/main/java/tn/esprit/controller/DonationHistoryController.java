package tn.esprit.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.entity.Donation;
import tn.esprit.entity.Evenement;
import tn.esprit.service.DonationService;
import tn.esprit.service.EvenementService;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DonationHistoryController {

    @FXML private ComboBox<String> userComboBox;
    @FXML private TableView<Donation> donationTable;
    @FXML private TableColumn<Donation, String> eventColumn;
    @FXML private TableColumn<Donation, Double> amountColumn;
    @FXML private TableColumn<Donation, String> commentColumn;
    @FXML private TableColumn<Donation, String> dateColumn;

    private final DonationService donationService = new DonationService();
    private final EvenementService evenementService = new EvenementService();

    private final Map<Integer, String> eventMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialize columns
        eventColumn.setCellValueFactory(cellData -> {
            int eventId = cellData.getValue().getEvenementId();
            return new ReadOnlyStringWrapper(eventMap.getOrDefault(eventId, "Inconnu"));
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        dateColumn.setCellValueFactory(cellData -> {
            return new ReadOnlyStringWrapper(
                    cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
        });

        // Load all events
        for (Evenement e : evenementService.getAllEvenements()) {
            eventMap.put(e.getId(), e.getNom());
        }

        // Load all users (donor names)
        Set<String> donorNames = donationService.getAllDonations().stream()
                .map(Donation::getDonorName)
                .collect(Collectors.toSet());

        userComboBox.setItems(FXCollections.observableArrayList(donorNames));
    }

    @FXML
    public void loadDonationsForUser() {
        String selectedDonor = userComboBox.getValue();
        if (selectedDonor == null) return;

        List<Donation> filtered = donationService.getAllDonations().stream()
                .filter(d -> d.getDonorName().equals(selectedDonor))
                .collect(Collectors.toList());

        donationTable.setItems(FXCollections.observableArrayList(filtered));
    }
}
