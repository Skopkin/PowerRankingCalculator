package application;

import java.util.ArrayList;

import dataTypes.PlayerQuery;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PlayerForm extends EntryForm {
	
	private int id;
	
	private String firstName;
	
	private String lastName;
	
	private ArrayList<String> characters;
	
	private boolean active;
	
	PlayerQuery newPlayer;
	
	PlayerQuery oldPlayer;
	
	ObservableList<String> characterList;
	
	PlayerForm(ObservableList<String> list) {
		characterList = list;
		initializeStage();
	}
	
	PlayerForm(ObservableList<String> list, PlayerQuery q) {
		characterList = list;
		oldPlayer = q;
		initializeStage();
	}
	
	void initializeStage() {
		TextField firstNameField = new TextField();
		Label firstNameLabel = new Label("First Name:", firstNameField);
		firstNameLabel.setContentDisplay(ContentDisplay.RIGHT);
		
		TextField lastNameField = new TextField();
		Label lastNameLabel = new Label("Last Name (Optional):", lastNameField);
		lastNameLabel.setContentDisplay(ContentDisplay.RIGHT);
		
		ListView<String> characterSelectionList = new ListView<String>(characterList);
		characterSelectionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Label characterSelectLabel = new Label("Characters (Select up to 5):", characterSelectionList);
		characterSelectLabel.setContentDisplay(ContentDisplay.RIGHT);
		
		characterSelectionList.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
	        Node node = evt.getPickResult().getIntersectedNode();

	        // go up from the target node until a list cell is found or it's clear
	        // it was not a cell that was clicked
	        while (node != null && node != characterSelectionList && !(node instanceof ListCell)) {
	            node = node.getParent();
	        }

	        // if is part of a cell or the cell,
	        // handle event instead of using standard handling
	        if (node instanceof ListCell) {
	            // prevent further handling
	            evt.consume();

	            ListCell<?> cell = (ListCell<?>) node;
	            ListView<?> lv = cell.getListView();

	            // focus the listview
	            lv.requestFocus();

	            if (!cell.isEmpty()) {
	                // handle selection for non-empty cells
	                int index = cell.getIndex();
	                if (cell.isSelected()) {
	                    lv.getSelectionModel().clearSelection(index);
	                } else {
	                    lv.getSelectionModel().select(index);
	                }
	            }
	        }
	    });
		
		CheckBox activeCheckBox = new CheckBox("Currently active?");
		
		HBox nameBox = new HBox(firstNameLabel, lastNameLabel);
		
		
		Button submit = new Button("Submit");
		
		submit.setOnAction(e -> {
			firstName = firstNameField.getText();
			lastName = lastNameField.getText();
			characters = new ArrayList<String>(characterSelectionList.getSelectionModel().getSelectedItems());
			active = activeCheckBox.isSelected();
			
			if (1 <= firstName.length() && firstName.length() <= 30) {
				if (lastName.length() <= 30) {
					if (characters.size() <= 5) {
						newPlayer = new PlayerQuery(id, firstName, lastName, characters, active);
						window.close();
					} else {
						setErrorMessage("ERROR: Too many characters selected!");
					}
				} else {
					setErrorMessage("ERROR: Please enter a valid last name under 30 characters!");
				}
			} else {
				setErrorMessage("ERROR: Please enter a valid first name under 30 characters!");
			}
		});
		
		VBox submitBox = new VBox(errorMessage, submit);
		
		if (oldPlayer != null) {
			this.setTitle("Edit existing Player");
			id = oldPlayer.getId();
			firstNameField.setText(oldPlayer.getFirstName());
			lastNameField.setText(oldPlayer.getLastName());
			for (String c: oldPlayer.getCharacters()) {
				characterSelectionList.getSelectionModel().select(c);
			}
			if (oldPlayer.isActive()) {
				activeCheckBox.setSelected(true);
			}
		} 
		else {
			this.setTitle("Add a new Player");
		}
		
		BorderPane bPane = new BorderPane();
		
		bPane.setTop(nameBox);
		bPane.setLeft(activeCheckBox);
		bPane.setRight(characterSelectLabel);
		bPane.setBottom(submitBox);
		
		BorderPane.setMargin(firstNameLabel, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(lastNameField, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(activeCheckBox, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(characterSelectLabel, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(submitBox, new Insets(12, 12, 12, 12));
		BorderPane.setMargin(errorMessage, new Insets(12, 12, 12, 12));
		BorderPane.setAlignment(submitBox, Pos.CENTER);
		BorderPane.setAlignment(submit, Pos.CENTER);
		BorderPane.setAlignment(errorMessage, Pos.CENTER);
		BorderPane.setAlignment(nameBox, Pos.CENTER);
		
		
		Scene scene = new Scene(bPane);
		
		window.setTitle(title);
		window.setScene(scene);
	}
	
	PlayerQuery getNewPlayer() {
		return newPlayer;
	}
	
	void showStage( ) {
		window.showAndWait();
	}

}
