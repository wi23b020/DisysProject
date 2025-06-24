package org.example.energygui.controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.energygui.api.ApiClient;
import org.example.energygui.model.CurrentData;

import java.util.List;

public class HelloController {

    @FXML private TextField startField;
    @FXML private TextField endField;
    @FXML private TextArea outputArea;

    @FXML private TableView<CurrentData> tableView;
    @FXML private TableColumn<CurrentData, String> hourCol;
    @FXML private TableColumn<CurrentData, Number> communityDepletedCol;
    @FXML private TableColumn<CurrentData, Number> gridPortionCol;


    @FXML
    public void initialize() {
        hourCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getHour().toString()));
        communityDepletedCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCommunityDepleted()));
        gridPortionCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getGridPortion()));
    }

    @FXML
    protected void onGetCurrent() {
        try {
            CurrentData data = ApiClient.getCurrent();
            outputArea.setText(data.toString());
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }



    @FXML
    protected void onGetHistorical() {
        try {
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            List<CurrentData> dataList = ApiClient.getHistorical(start, end);
            ObservableList<CurrentData> observableList = FXCollections.observableArrayList(dataList);
            tableView.setItems(observableList);
        } catch (Exception e) {
            outputArea.setText("Error: please insert a valid date Format: example 2025-06-24T14:00:00");
            e.printStackTrace(); // Log the full error to console
        }

    }




}
