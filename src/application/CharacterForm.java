package application;

import dataTypes.CharacterQuery;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// This class defines a window for a new character form
class CharacterForm extends EntryForm{
	
	String name;
	
	String series;
	
	boolean dlc;
	
	CharacterQuery newCharacter;
	
	CharacterQuery oldCharacter;
	
	ObservableList<String> seriesList;
	
	CharacterForm(ObservableList<String> list) {
		seriesList = list;
		initializeStage();
	}
	
	CharacterForm(ObservableList<String> list, CharacterQuery q) {
		seriesList = list;
		oldCharacter = q;
		initializeStage();
	}
	
	void initializeStage() {
		TextField nameField = new TextField();
		Label nameLabel = new Label("Character Name:", nameField);
		nameLabel.setContentDisplay(ContentDisplay.RIGHT);
		
		ComboBox<String> seriesComboBox = new ComboBox<String>(seriesList);
		Label seriesLabel = new Label("Game Series", seriesComboBox);
		seriesLabel.setContentDisplay(ContentDisplay.RIGHT);
		
		RadioButton rbTrue = new RadioButton("Yes");
		RadioButton rbFalse = new RadioButton("No");
		
		ToggleGroup dlcGroup = new ToggleGroup();
		
		rbTrue.setToggleGroup(dlcGroup);
		rbFalse.setToggleGroup(dlcGroup);
		
		rbTrue.setSelected(true);
		
		rbTrue.setOnAction(e -> {
			if (rbTrue.isSelected()) {
				dlc = true;
			}
		});
		
		rbFalse.setOnAction(e -> {
			if (rbFalse.isSelected()) {
				dlc = false;
			}
		});
		
		VBox rbBox = new VBox(rbTrue, rbFalse);
		Label rbLabel = new Label("DLC", rbBox);
		rbLabel.setContentDisplay(ContentDisplay.BOTTOM);
		
		Button submit = new Button("Submit");
		
		submit.setOnAction(e -> {
			name = nameField.getText();
			series = seriesComboBox.getValue();
			if (1 <= name.length() && name.length() <= 30) {
				if (series != null) {
					newCharacter = new CharacterQuery(name, series, dlc);
					window.close();
				} else {
					setErrorMessage("ERROR: Please select a game series!");
				}
			} else {
				setErrorMessage("ERROR: Please enter a valid name under 30 characters!");
			}
			
		});
		
		VBox submitBox = new VBox(errorMessage, submit);
		
		if (oldCharacter != null) {
			this.setTitle("Edit existing Character");
			nameField.setText(oldCharacter.getName());
			seriesComboBox.setValue(oldCharacter.getSeries());
			if (oldCharacter.isDlc()) {
				rbTrue.setSelected(true);
			}
			else {
				rbFalse.setSelected(true);
			}
		} 
		else {
			this.setTitle("Add a new Character");
		}
		
		BorderPane bPane = new BorderPane();
		
		bPane.setTop(nameLabel);
		bPane.setLeft(rbLabel);
		bPane.setRight(seriesLabel);
		bPane.setBottom(submitBox);
		
		BorderPane.setMargin(nameLabel, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(rbLabel, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(seriesLabel, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(submit, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(submitBox, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(errorMessage, new Insets(12, 12, 12, 12));
		BorderPane.setAlignment(submitBox, Pos.CENTER);
		BorderPane.setAlignment(submit, Pos.CENTER);
		BorderPane.setAlignment(errorMessage, Pos.CENTER);
		BorderPane.setAlignment(nameLabel, Pos.CENTER);
		
		
		Scene scene = new Scene(bPane);
		
		window.setTitle(title);
		window.setScene(scene);
	}
	
	CharacterQuery getNewCharacter() {
		return newCharacter;
	}
	
	void showStage( ) {
		window.showAndWait();
	}

}
