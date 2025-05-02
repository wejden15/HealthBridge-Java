package tn.esprit.controller;

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entity.Donation;
import tn.esprit.entity.Evenement;
import tn.esprit.service.DonationService;
import tn.esprit.service.EvenementService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.awt.Color;
import com.lowagie.text.Image;
import com.lowagie.text.Font;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;


public class AdminDonationController {

    @FXML
    private TableView<DonationView> donationTable;

    @FXML
    private BarChart<String, Number> donationBarChart;

    @FXML
    private TableColumn<DonationView, String> eventNameColumn;

    @FXML private TextArea totalByEventArea;

    @FXML
    private PieChart donationPieChart;

    @FXML
    private TableColumn<DonationView, String> donorNameColumn;

    @FXML
    private TableColumn<DonationView, Double> amountColumn;

    @FXML
    private TableColumn<DonationView, String> commentColumn;

    @FXML
    private TableColumn<DonationView, String> dateColumn;

    private final DonationService donationService = new DonationService();
    private final EvenementService evenementService = new EvenementService();

    @FXML
    public void initialize() {
        // 1️⃣ Setup Table columns
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        donorNameColumn.setCellValueFactory(new PropertyValueFactory<>("donorName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAtFormatted"));

        // 2️⃣ Load donations from database
        Map<Integer, String> eventNameMap = new HashMap<>();
        for (Evenement e : evenementService.getAllEvenements()) {
            eventNameMap.put(e.getId(), e.getNom());
        }

        List<Donation> allDonations = donationService.getAllDonations();
        ObservableList<DonationView> donationViews = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Donation d : allDonations) {
            String eventName = eventNameMap.getOrDefault(d.getEvenementId(), "Inconnu");
            donationViews.add(new DonationView(
                    eventName,
                    d.getDonorName(),
                    d.getAmount(),
                    d.getComment(),
                    d.getCreatedAt().format(formatter)
            ));
        }

        // 3️⃣ Fill TableView
        donationTable.setItems(donationViews);

        // 4️⃣ Fill PieChart based on loaded donationViews
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Map<String, Double> eventTotals = new HashMap<>();

        for (DonationView view : donationViews) {
            eventTotals.merge(view.getEventName(), view.getAmount(), Double::sum);
        }

        for (Map.Entry<String, Double> entry : eventTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        donationPieChart.setData(pieChartData);


        donationTable.setItems(donationViews);
        // Calculate total donations per event
        Map<String, Double> totals = new HashMap<>();

        for (DonationView view : donationViews) {
            String name = view.getEventName();
            double amount = view.getAmount();
            totals.put(name, totals.getOrDefault(name, 0.0) + amount);
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Montants par Événement");

        Map<String, Double> barData = new HashMap<>();
        for (DonationView view : donationViews) {
            barData.merge(view.getEventName(), view.getAmount(), Double::sum);
        }

        for (Map.Entry<String, Double> entry : barData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        donationBarChart.getData().add(series);

// Show nicely in TextArea
        StringBuilder sb = new StringBuilder();
        totals.forEach((name, total) -> {
            sb.append("• ").append(name).append(" → ").append(String.format("%.2f", total)).append(" TND\n");

        });
        totalByEventArea.setText(sb.toString());

    }
    @FXML
    public void exportToPDF() {
        com.lowagie.text.Document document = new com.lowagie.text.Document();

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer PDF des Donations");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // ✅ Logo
                InputStream is = getClass().getResourceAsStream("/img/healthbridge_logo.png");
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    Image logo = Image.getInstance(bytes);
                    logo.scaleToFit(100, 100);
                    logo.setAlignment(Image.ALIGN_CENTER);
                    document.add(logo);
                    document.add(new Paragraph(" "));
                }

                // ✅ Title
                Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
                Paragraph title = new Paragraph("💰 Rapport de Donations par Événement", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // ✅ Table
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                String[] headers = {"Événement", "Donateur", "Montant (TND)", "Commentaire", "Date"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setBackgroundColor(new Color(240, 240, 240));
                    cell.setPadding(8);
                    table.addCell(cell);
                }

                for (DonationView view : donationTable.getItems()) {
                    table.addCell(view.getEventName());
                    table.addCell(view.getDonorName());
                    table.addCell(String.format("%.2f", view.getAmount()));
                    table.addCell(view.getComment());
                    table.addCell(view.getCreatedAtFormatted());
                }

                document.add(table);

                // ✅ Totaux par événement
                Map<String, Double> totalByEvent = new LinkedHashMap<>();
                for (DonationView view : donationTable.getItems()) {
                    totalByEvent.merge(view.getEventName(), view.getAmount(), Double::sum);
                }

                Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
                document.add(new Paragraph("💡 Totaux par Événement :", sectionFont));

                Font rowFont = new Font(Font.HELVETICA, 11);
                for (Map.Entry<String, Double> entry : totalByEvent.entrySet()) {
                    Paragraph p = new Paragraph("• " + entry.getKey() + " : " + String.format("%.2f", entry.getValue()) + " TND", rowFont);
                    p.setIndentationLeft(15f);
                    document.add(p);
                }

                // ✅ Total général
                LineSeparator separator = new LineSeparator();
                separator.setLineColor(Color.GRAY);
                separator.setLineWidth(1f);
                document.add(separator);

                double total = donationTable.getItems().stream().mapToDouble(DonationView::getAmount).sum();
                Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLUE);
                Paragraph totalSummary = new Paragraph("📊 Total général : " + String.format("%.2f", total) + " TND", summaryFont);
                totalSummary.setAlignment(Element.ALIGN_RIGHT);
                totalSummary.setSpacingBefore(10f);
                document.add(totalSummary);

                // ✅ Footer note
                Paragraph note = new Paragraph(
                        "Généré par HealthBridge • " +
                                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        new Font(Font.HELVETICA, 10, Font.ITALIC)
                );
                note.setAlignment(Element.ALIGN_RIGHT);
                document.add(note);

                document.close();
                new Alert(Alert.AlertType.INFORMATION, "✅ PDF généré avec succès !").showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            document.close();
            new Alert(Alert.AlertType.ERROR, "❌ Erreur lors de la génération du PDF.").showAndWait();
        }
    }


    // Optional back button logic
    @FXML
    public void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/template.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene(); // Get current scene
            double width = currentScene.getWidth();
            double height = currentScene.getHeight();

            Scene newScene = new Scene(root, width, height); // Keep previous size
            stage.setScene(newScene);
            stage.setTitle("HealthBridge - Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Helper class to display in TableView
    public static class DonationView {
        private final String eventName;
        private final String donorName;
        private final double amount;
        private final String comment;
        private final String createdAtFormatted;

        public DonationView(String eventName, String donorName, double amount, String comment, String createdAtFormatted) {
            this.eventName = eventName;
            this.donorName = donorName;
            this.amount = amount;
            this.comment = comment;
            this.createdAtFormatted = createdAtFormatted;
        }

        public String getEventName() { return eventName; }
        public String getDonorName() { return donorName; }
        public double getAmount() { return amount; }
        public String getComment() { return comment; }
        public String getCreatedAtFormatted() { return createdAtFormatted; }
    }
}
