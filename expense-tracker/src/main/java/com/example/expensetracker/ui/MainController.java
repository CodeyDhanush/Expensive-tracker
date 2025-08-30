package com.example.expensetracker.ui;

import java.time.LocalDate;

import com.example.expensetracker.dao.TransactionDao;
import com.example.expensetracker.model.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.embed.swing.SwingNode;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.SwingUtilities;
import java.util.Map;
import java.util.HashMap;

public class MainController {

    // ==== Table ====
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;

    // ==== Form ====
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private ComboBox<String> typeBox;
    @FXML private TextField amountField;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    // ==== Chart Pane ====
    @FXML private BorderPane chartPane;

    private final TransactionDao dao = new TransactionDao();
    private final ObservableList<Transaction> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Populate dropdowns
        categoryBox.setItems(FXCollections.observableArrayList(
                "Food", "Transport", "Entertainment", "Bills", "Shopping", "Other"
        ));
        typeBox.setItems(FXCollections.observableArrayList("INCOME", "EXPENSE"));

        // Bind table columns
        dateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDate()));
        descriptionColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescription()));
        categoryColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategory()));
        typeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));
        amountColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getAmount()));

        transactionTable.setItems(data);

        refreshTable();
    }

    // ==== Add Transaction ====
    @FXML
    private void handleAddTransaction() {
        try {
            LocalDate date = datePicker.getValue();
            String desc = descriptionField.getText();
            String cat = categoryBox.getValue();
            String type = typeBox.getValue();
            String amountText = amountField.getText();

            if (date == null || desc.isBlank() || cat == null || type == null || amountText.isBlank()) {
                showAlert("Validation Error", "Please fill all fields.");
                return;
            }

            double amt = Double.parseDouble(amountText);

            Transaction t = new Transaction(0, date, desc, cat, type, amt);
            dao.insert(t);
            refreshTable();
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Amount must be a valid number.");
        }
    }

    // ==== Delete Transaction ====
    @FXML
    private void handleDeleteTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteById(selected.getId());
            refreshTable();
        } else {
            showAlert("No selection", "Please select a transaction to delete.");
        }
    }

    // ==== Chart Buttons (Stub for now) ====
    @FXML
    private void showPie() {
        createPieChart();
    }

    @FXML
    private void showBar() {
        createBarChart();
    }

    private void createPieChart() {
        // Get category totals for expenses only
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Transaction t : dao.findAll()) {
            if ("EXPENSE".equals(t.getType())) {
                categoryTotals.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }

        if (categoryTotals.isEmpty()) {
            showAlert("No Data", "No expense data available for pie chart.");
            return;
        }

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        categoryTotals.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expenses by Category", dataset, true, true, false);

        displayChart(chart);
    }

    private void createBarChart() {
        // Get monthly income vs expense data
        var monthlyData = dao.monthlyIncomeVsExpense();
        
        if (monthlyData.isEmpty()) {
            showAlert("No Data", "No data available for bar chart.");
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map<String, Object> row : monthlyData) {
            String month = (String) row.get("ym");
            Double income = (Double) row.get("income");
            Double expense = (Double) row.get("expense");
            
            dataset.addValue(income, "Income", month);
            dataset.addValue(expense, "Expense", month);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Income vs Expenses", "Month", "Amount", dataset);

        displayChart(chart);
    }

    private void displayChart(JFreeChart chart) {
        SwingNode swingNode = new SwingNode();
        
        SwingUtilities.invokeLater(() -> {
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(380, 300));
            swingNode.setContent(chartPanel);
        });
        
        chartPane.setCenter(swingNode);
    }

    // ==== Helpers ====
    private void refreshTable() {
        data.setAll(dao.findAll());
    }

    private void clearForm() {
        datePicker.setValue(null);
        descriptionField.clear();
        categoryBox.setValue(null);
        typeBox.setValue(null);
        amountField.clear();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
