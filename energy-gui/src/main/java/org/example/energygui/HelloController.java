package org.example.energygui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class HelloController {

    @FXML private TextField startField;
    @FXML private TextField endField;
    @FXML private TextArea outputArea;

    @FXML private TableView<EnergyData> tableView;
    @FXML private TableColumn<EnergyData, String> dayCol;
    @FXML private TableColumn<EnergyData, Number> producedCol;
    @FXML private TableColumn<EnergyData, Number> usedCol;
    @FXML private TableColumn<EnergyData, Number> gridCol;


    @FXML
    public void initialize() {
        dayCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDay()));
        producedCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCommunityProduced()));
        usedCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCommunityUsed()));
        gridCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getGridUsed()));
    }

    @FXML
    protected void onGetCurrent() {
        try {
            EnergyData data = ApiClient.getCurrent();
            outputArea.setText(data.toString());

            tableView.getItems().clear();
            tableView.getItems().add(data);
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    protected void onGetHistorical() {
        try {
            String start = startField.getText().trim();
            String end = endField.getText().trim();

            if (start.isEmpty() || end.isEmpty()) {
                outputArea.setText("Please enter both start and end times in the format yyyy-MM-dd");
                tableView.setItems(FXCollections.emptyObservableList());
                return;
            }

            List<EnergyData> dataList = ApiClient.getHistorical(start, end);

            // Update TextArea
            StringBuilder text = new StringBuilder();
            for (EnergyData d : dataList) {
                text.append(d.toString()).append("\n\n");
            }
            outputArea.setText(text.toString());

            // Update TableView
            ObservableList<EnergyData> observableData = FXCollections.observableArrayList(dataList);
            tableView.setItems(observableData);

        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
            tableView.setItems(FXCollections.emptyObservableList());
        }
    }



}
