package com.example.app.utils;

import com.example.app.model.Building;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.ArrayList;

import static javafx.scene.input.KeyCode.*;

public class FilteredBuildingBox {
    private ObservableList<Building> oBuildings; //original list of buildings
    private ObservableList<Building> filteredList; //filtered list of buildings
    private TextField editor; //The textfeild inside buildingComboBox
    private Building lastNotNull; //Keeps track of the last building that was selected
    private ComboBox<Building> buildingComboBox; //just the comboBox

    //Listeners
    private ChangeListener<Boolean> focusListener;
    private ChangeListener<String> textListener;
    private ChangeListener<Building> valueChangeListener;
    private EventHandler<ActionEvent> onEnterListener;


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

        //The listener that tracks if the user types a letter into editor
        textListener = (obs, oldValue, newValue) -> {
            Platform.runLater(() -> filter());
        };
        editor.textProperty().addListener(textListener);

        //The listener that checks if the focus is changed from editor
        focusListener = (obs, wasFocused, isNowFocused) ->
                Platform.runLater(() -> lostFocus(isNowFocused));
        editor.focusedProperty().addListener(focusListener);

        //The listener that checks if a new value is selected
        valueChangeListener = (obs, oldValue, newValue) -> {
            Platform.runLater(() -> onChange());
        };
        buildingComboBox.valueProperty().addListener(valueChangeListener);

        //The listener that checks if the backspace key is pressed
        editor.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == BACK_SPACE){
                    onBackspace();
                }
                else if(event.getCode() == TAB){
                    onTab();
                }
            }
        });

        //The listener that checks if an action takes place in buildingComboBox
        onEnterListener = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onEnter();
            }
        };
        buildingComboBox.setOnAction(onEnterListener);
    }

    //Creates a list of buildings that are like the text inside editor
    public void filter() {
        int caretPosition = editor.getCaretPosition();
        String input = editor.getText();
        filteredList = FXCollections.observableArrayList();

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

    //If the user clicks off buildingComboBox put the last selected value into buildingComboBox otherwise the box gets wiped
    public void lostFocus(Boolean isNowFocused){
        if(buildingComboBox.getValue() == null && lastNotNull != null && !isNowFocused){
            buildingComboBox.setValue(lastNotNull);
            buildingComboBox.hide();
        }
    }

    public void onBackspace(){
        //Disable other listeners so there isn't overlap
        buildingComboBox.setOnAction(null);
        editor.textProperty().removeListener(textListener);
        editor.focusedProperty().removeListener(focusListener);

        //if there is a building selected, clear the input
        if(buildingComboBox.getValue() != null) {
            editor.setText("");
            buildingComboBox.setValue(null);
            lastNotNull = null;
        }

        //re-enable listeners
        buildingComboBox.setOnAction(onEnterListener);
        editor.textProperty().addListener(textListener);
        editor.focusedProperty().addListener(focusListener);
    }

    //Store the selected value
    public void onChange(){
        if(buildingComboBox.getValue() != null){
            lastNotNull = buildingComboBox.getValue();
        }
    }

    public void onEnter(){
        String value = buildingComboBox.getEditor().getText();
        //If the text in editor is a building store it in lastNotNull
        for(Building b: oBuildings){
            if(b.getName().equals(value)){
                lastNotNull = b;
            }
        }

        //If the enter key is pressed, set the value of buildingComboBox to the highlighted value
        if(value.isEmpty()){
            buildingComboBox.setValue(lastNotNull);
        }
    }

    public void onTab(){
        //Disable other listeners so there isn't overlap
        buildingComboBox.setOnAction(null);
        editor.textProperty().removeListener(textListener);
        editor.focusedProperty().removeListener(focusListener);

        //Set the value of buildingComboBox to highlighted value
        if(lastNotNull == null){
            buildingComboBox.setValue(filteredList.getFirst());
        }
        else{
            buildingComboBox.setValue(lastNotNull);
        }

        //re-enable listeners
        buildingComboBox.setOnAction(onEnterListener);
        editor.textProperty().addListener(textListener);
        editor.focusedProperty().addListener(focusListener);
    }

    public void setList(ArrayList<Building> buildings){
        oBuildings = FXCollections.observableArrayList(buildings);
    }

}
