package gui;

import entities.Produit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import services.ServiceProduit;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class statistiquesController implements Initializable {

    @FXML
    private LineChart<String, Integer> lineChartProduits;

    @FXML
    private AnchorPane statPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            statistique();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void statistique() throws SQLException {
        ServiceProduit sp = new ServiceProduit();

        List<Produit> prods = sp.Show();

        // Créer les axes pour le graphique
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Nom Produit");
        yAxis.setLabel("Prix Produit");

        // Créer la série de données à afficher
        XYChart.Series series = new XYChart.Series();
        series.setName("Statistiques des produits selon leurs prix");
        for (Produit p : prods) {
            series.getData().add(new XYChart.Data<>(p.getNom(), p.getPrix()));
        }

        // Créer le graphique et ajouter la série de données
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Statistiques des Produits");
        lineChart.getData().add(series);

        // Afficher le graphique dans votre scène
        lineChartProduits.setCreateSymbols(false);
        lineChartProduits.getData().add(series);
    }
}
