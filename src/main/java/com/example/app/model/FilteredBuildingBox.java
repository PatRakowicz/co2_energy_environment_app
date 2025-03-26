package com.example.app.model;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.ArrayList;

import static javafx.scene.input.KeyCode.BACK_SPACE;

public class FilteredBuildingBox {
    ObservableList<Building> oBuildings;
    private TextField editor;
    public Building lastNotNull;
    ComboBox<Building> buildingComboBox;
    private ChangeListener<Boolean> focusListener;
    private ChangeListener<String> textListener;


    public FilteredBuildingBox(ArrayList<Building> buildings, ComboBox<Building> buildingComboBox){
        oBuildings = FXCollections.observableArrayList(buildings);
        this.buildingComboBox = buildingComboBox;
        buildingComboBox.setItems(oBuildings);

        buildingComboBox.setConverter(new StringConverter<Building>() {
            @Override
            public String toString(Building building) {
                if (building == null) {
                    return "";
                }
                return building.getName();
            }

            @Override
            public Building fromString(String s) {
                return null;
            }
        });

        editor = buildingComboBox.getEditor();

        textListener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> filter());
        };
        editor.textProperty().addListener(textListener);

        focusListener = (obs, wasFocused, isNowFocused) ->
                Platform.runLater(() -> lostFocus(isNowFocused));

        editor.focusedProperty().addListener(focusListener);

        editor.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == BACK_SPACE){
                    editor.textProperty().removeListener(textListener);
                    editor.focusedProperty().removeListener(focusListener);
                    onBackspace();
                    editor.textProperty().addListener(textListener);
                    editor.focusedProperty().addListener(focusListener);
                }
            }
        });
    }

    public void filter() {
        int caretPosition = editor.getCaretPosition();
        String input = editor.getText();
        ObservableList<Building> filteredList = FXCollections.observableArrayList();

        boolean buildingSelected = false;
        for (Building b : oBuildings) {
            if (b.getName().equals(input)) {
                buildingSelected = true;
            }
        }

        if (!buildingSelected) {
            buildingComboBox.show();
            if (input.isEmpty()) {
                buildingComboBox.setItems(oBuildings);
            } else {
                for (Building item : oBuildings) {
                    if (item.getName().toLowerCase().contains(input.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                buildingComboBox.setItems(filteredList);
            }
        }
        editor.positionCaret(caretPosition);
    }

    public void lostFocus(Boolean isNowFocused){
        if(buildingComboBox.getValue() == null && lastNotNull != null && !isNowFocused){
            buildingComboBox.setValue(lastNotNull);
            buildingComboBox.hide();
        }
    }

    public void onBackspace(){
        editor.setText("");
        buildingComboBox.setValue(null);
    }
}
