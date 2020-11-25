package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import dataTypes.CharacterQuery;
import dataTypes.Query;
import database.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class CharacterView extends View {
	// Create a column object for each column in the table with the appropriate data types
	TableColumn<Query, String> nameCol = new TableColumn<Query, String>("Name");
	TableColumn<Query, String> seriesCol = new TableColumn<Query, String>("Series");
	TableColumn<Query, Boolean> dlcCol = new TableColumn<Query, Boolean>("DLC");
	
	CharacterView() {
		// Create a new HBox using the initButtons method
		initButtons();
				
		// Call the initColumns method to create the columns for the table
		initColumns();
	}
	
	// CURRENTLY A PLACEHOLDER THAT MANUALLY ADDS DUMMY DATA
	// Queries the DB and returns an observable list with the results
	@Override
	public ObservableList<Query> getQuery() {
		data = FXCollections.observableArrayList();
		
		String sqlStatement = new String("SELECT * FROM game_character");
		PreparedStatement prepSqlStatement = null;
		ResultSet rsFindCharacters = null;
		
		conn = DbConnection.getConnection();
		
		try {
			prepSqlStatement = conn.prepareStatement(sqlStatement);
			rsFindCharacters = prepSqlStatement.executeQuery();
			while (rsFindCharacters.next()) {
				String tempName = rsFindCharacters.getString(3);
				String tempSeries = rsFindCharacters.getString(2);
				boolean tempDlc = rsFindCharacters.getBoolean(4);
				
				data.add(new CharacterQuery(tempName, tempSeries, tempDlc));
			}
			return data;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	// returns an HBox containing all the necessary buttons
	@Override
	void initButtons() {
		// BUTTON FUNCTIONALITY NEEDED
		Button newButton = new Button("New");
		Button editButton = new Button("Edit");
		Button deleteButton = new Button("Delete");
		
		// Adds event handler to the new button
		// Opens up a window that allows user to enter data for a new character
		// If the data was entered and the submit button pressed, insert the new character in the database
		newButton.setOnAction(e -> {
			newCharacterWindow();
		});
		
		editButton.setOnAction(e -> {
			editCharacterWindow();
		});
		
		deleteButton.setOnAction(e -> {
			deleteCharacter();
		});
		
		buttonBox.getChildren().addAll(newButton, editButton, deleteButton);
	}
	
	
	// Initialize the columns by linking the appropriate members in the data type and adding the columns to the table
	@Override
	void initColumns() {
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		seriesCol.setCellValueFactory(new PropertyValueFactory<>("series"));
		dlcCol.setCellValueFactory(new PropertyValueFactory<>("dlc"));
		
		updateColumns();
	}
	
	@SuppressWarnings("unchecked")
	void updateColumns( ) {
		table.setItems(getQuery());
		table.getColumns().setAll(nameCol, seriesCol, dlcCol);
	}
	
	void newCharacterWindow() {
		CharacterForm window = new CharacterForm(getSeriesList());
		
		if (window != null) {
			window.showStage();
			CharacterQuery query = window.getNewCharacter();
			
			if (query != null) {
				int dlcValue = query.isDlc() ? 0 : 1;
				
				String sqlInsert = "INSERT INTO game_character (CHARACTER_ID, SERIES, NAME, DLC) "
						+ "VALUES (NULL, '" + query.getSeries() + "', '" + query.getName() + "', '" + dlcValue + "')";
				
				PreparedStatement prepSqlStatement = null;
				
				conn = DbConnection.getConnection();
				
				try {
					prepSqlStatement = conn.prepareStatement(sqlInsert);
					prepSqlStatement.executeUpdate();
					
					updateColumns();
				}
				catch (SQLException ex) {
					ex.printStackTrace();
				}
				
			}
		}
	}
	
	void editCharacterWindow() {
		CharacterQuery selected = (CharacterQuery) table.getSelectionModel().getSelectedItem();
		
		if (selected != null) {
			CharacterForm window = new CharacterForm(getSeriesList(), selected);
			
			if (window != null) {
				window.showStage();
				CharacterQuery query = window.getNewCharacter();
				
				if (query != null) {
					if (!query.compareCharacter(selected)) {
							
						conn = DbConnection.getConnection();
						try {
							String sqlSelect = "SELECT * FROM game_character WHERE NAME LIKE '" + selected.getName().replace("'", "\\'") + "'";
							
							Statement selectStatement = conn.createStatement();
							
							ResultSet rs = selectStatement.executeQuery(sqlSelect);
							
							rs.next();
							
							int id = rs.getInt(1);							
							
							String editedFields = "";
							
							if (query.getName() != selected.getName()) {
								editedFields += "NAME = '" + query.getName().replace("'", "\\'") + "'";
							}
							
							if (query.getSeries() != selected.getSeries()) {
								if (editedFields != "") {
									editedFields += ", ";
								}
								editedFields += "SERIES = '" + query.getSeries() + "'";
							}
							
							if (query.isDlc() != selected.isDlc()) {
								if (editedFields != "") {
									editedFields += ", ";
								}
								int dlcValue = query.isDlc() ? 1 : 0;
								editedFields += "DLC = '" + dlcValue + "'";
							}
							
							String sqlUpdate = "UPDATE game_character SET " + editedFields + " WHERE CHARACTER_ID = " + id;
						
							PreparedStatement prepSqlStatement = null;
						
							prepSqlStatement = conn.prepareStatement(sqlUpdate);
							prepSqlStatement.executeUpdate();
							
							updateColumns();
						}
						catch (SQLException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	void deleteCharacter() {
		CharacterQuery selected = (CharacterQuery) table.getSelectionModel().getSelectedItem();
		
		if (selected != null) {
			Alert confirmation = new Alert(AlertType.CONFIRMATION);
			confirmation.setTitle("Delete Character");
			confirmation.setHeaderText("Are you sure you want to delete this character from the database? (This cannot be undone)");
			confirmation.setContentText(selected.getName());
			
			Optional<ButtonType> option = confirmation.showAndWait();
			
			if (option.get() == ButtonType.OK) {
				conn = DbConnection.getConnection();
				try {
					String sqlSelect = "SELECT * FROM game_character WHERE NAME LIKE '" + selected.getName().replace("'", "\\'") + "'";
					
					Statement selectStatement = conn.createStatement();
					
					ResultSet rs = selectStatement.executeQuery(sqlSelect);
					
					rs.next();
					
					int id = rs.getInt(1);
					
					String sqlDelete = "DELETE FROM game_character WHERE CHARACTER_ID = " + id;
					
					PreparedStatement prepSqlStatement = null;
					
					prepSqlStatement = conn.prepareStatement(sqlDelete);
					prepSqlStatement.executeUpdate();
					
					updateColumns();
				}
				catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	ObservableList<String> getSeriesList() {
		ObservableList<String> seriesList = FXCollections.observableArrayList();
		
		String sqlStatement = new String("SELECT TITLE FROM game_series");
		PreparedStatement prepSqlStatement = null;
		ResultSet rsFindCharacters = null;
		
		conn = DbConnection.getConnection();
		
		try {
			prepSqlStatement = conn.prepareStatement(sqlStatement);
			rsFindCharacters = prepSqlStatement.executeQuery();
			
			while (rsFindCharacters.next()) {
				String tempName = rsFindCharacters.getString(1);
				seriesList.add(tempName);
			}
			
			return seriesList;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
}
