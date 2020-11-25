package application;

import java.sql.Date;
import dataTypes.TourneyQuery;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TourneyForm extends EntryForm{
	
	private Date date;
	
	private String name;
	
	private int entries;
	
	private double version;
	
	private TourneyQuery newTourney;
	
	private TourneyQuery oldTourney;
	
	TourneyForm() {
		initializeStage();
	}
	
	TourneyForm(TourneyQuery q) {
		oldTourney = q;
		initializeStage();
	}
	
	void initializeStage() {
		TextField nameField = new TextField();
		Label nameLabel = new Label("Tournament Name:", nameField);
		nameLabel.setContentDisplay(ContentDisplay.BOTTOM);
		
		DatePicker datePicker = new DatePicker();
		Label datePickerLabel = new Label("Tournament Date:", datePicker);
		datePickerLabel.setContentDisplay(ContentDisplay.BOTTOM);
		
		TextField entriesField = new TextField();
		Label entriesLabel = new Label("# of entrants:", entriesField);
		entriesLabel.setContentDisplay(ContentDisplay.BOTTOM);
		
		TextField versionField = new TextField();
		Label versionLabel = new Label("Game Version:", versionField);
		versionLabel.setContentDisplay(ContentDisplay.BOTTOM);
		
		
		
		Button submit = new Button("Submit");
		
		submit.setOnAction(e -> {
			name = nameField.getText();
			if (datePicker.getValue() != null) {
				date = Date.valueOf(datePicker.getValue());
			}
			if (entriesField.getText().length() > 0) {
				try {
					entries = Integer.parseInt(entriesField.getText());
				} catch (Exception ex) {
					entries = -1;
				}
			}
			if (versionField.getText().length() > 0) {
				try {
					version = Double.parseDouble(versionField.getText());
				} catch (Exception ex) {
					version = -1;
				}
			}
			
			
			if (1 <= name.length() && name.length() <= 35) {
				if (entries >= 4) {
					if (date != null) {
						if (version >= 1.0) {
							newTourney = new TourneyQuery(date, name, entries, version);
							window.close();
						} else {
							setErrorMessage("ERROR: Please enter a valid game version!");
						}
					} else {
						setErrorMessage("ERROR: Please enter a valid date!");
					}
				} else {
					setErrorMessage("ERROR: Please enter a valid number of entrants!");
				}
			} else {
				setErrorMessage("ERROR: Please enter a valid name under 30 characters!");
			}
			
		});
		
		VBox submitBox = new VBox(errorMessage, submit);
		
		if (oldTourney != null) {
			setTitle("Edit existing Tournament");
			nameField.setText(oldTourney.getTourney());
			datePicker.setValue(((Date) oldTourney.getDate()).toLocalDate());
			entriesField.setText(oldTourney.getEntries() + "");
			versionField.setText(oldTourney.getVersion() + "");
		} 
		else {
			setTitle("Add a new Tournament");
		}
		
		
		VBox mainBox = new VBox(nameLabel, entriesLabel, datePickerLabel, versionLabel);
		BorderPane bPane = new BorderPane();
		
		bPane.setCenter(mainBox);
		bPane.setBottom(submitBox);
		
		VBox.setMargin(nameLabel, new Insets(12, 12, 12, 12));
		VBox.setMargin(entriesLabel, new Insets(12, 12, 12, 12));
		VBox.setMargin(versionLabel, new Insets(12, 12, 12, 12));
		VBox.setMargin(datePickerLabel, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(submitBox, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(errorMessage, new Insets(12, 12, 12, 12));
		BorderPane.setAlignment(submitBox, Pos.CENTER);
		BorderPane.setAlignment(submit, Pos.CENTER);
		BorderPane.setAlignment(errorMessage, Pos.CENTER);
		BorderPane.setAlignment(nameLabel, Pos.CENTER);
		BorderPane.setAlignment(entriesLabel, Pos.CENTER);
		BorderPane.setAlignment(versionLabel, Pos.CENTER);
		BorderPane.setAlignment(datePickerLabel, Pos.CENTER);
		
		
		Scene scene = new Scene(bPane);
		
		window.setTitle(title);
		
		window.setScene(scene);
	}


	TourneyQuery getNewTourney() {
		return newTourney;
	}

	void showStage( ) {
		window.showAndWait();
	}
}
