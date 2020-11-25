package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Optional;
import dataTypes.Query;
import dataTypes.TourneyQuery;
import database.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

public class TourneyView extends View{

	// Create a column object for each column in the table with the appropriate data types
	TableColumn<Query, Date> dateCol = new TableColumn<Query, Date>("Date");
	TableColumn<Query, String> tourneyCol = new TableColumn<Query, String>("Tournament");
	TableColumn<Query, Integer> entriesCol = new TableColumn<Query, Integer>("Entries");
	TableColumn<Query, Double> versionCol = new TableColumn<Query, Double>("Version");
	
	TourneyView() {
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
		
		String sqlStatement = new String("SELECT * FROM tournament");
		PreparedStatement prepSqlStatement = null;
		ResultSet rsFindTourneys = null;
		
		conn = DbConnection.getConnection();
		
		try {
			prepSqlStatement = conn.prepareStatement(sqlStatement);
			rsFindTourneys = prepSqlStatement.executeQuery();
			while (rsFindTourneys.next()) {
				String tempName = rsFindTourneys.getString(2);
				java.sql.Date tempDate = rsFindTourneys.getDate(3);
				int tempEntries = rsFindTourneys.getInt(4);
				double tempVersion = rsFindTourneys.getDouble(5);
				
				data.add(new TourneyQuery(tempDate, tempName, tempEntries, tempVersion));
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
		Button resultsButton = new Button("Results");
		Button deleteButton = new Button("Delete");
		newButton.setOnAction(e -> {
			newTourneyWindow();
		});
		
		editButton.setOnAction(e -> {
			editTourneyWindow();
		});
		
		deleteButton.setOnAction(e -> {
			deleteTourney();
		});
		
		buttonBox.getChildren().addAll(newButton, editButton, resultsButton, deleteButton);
	}
	
	
	// Initialize the columns by linking the appropriate members in the data type and adding the columns to the table
	@Override
	void initColumns() {
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		tourneyCol.setCellValueFactory(new PropertyValueFactory<>("tourney"));
		entriesCol.setCellValueFactory(new PropertyValueFactory<>("entries"));
		versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
		
		updateColumns();
	}
	
	@SuppressWarnings("unchecked")
	void updateColumns() {
		table.setItems(getQuery());
		table.getColumns().setAll(dateCol, tourneyCol, entriesCol, versionCol);
	}
	
	void newTourneyWindow() {
		TourneyForm window = new TourneyForm();
		
		if (window != null) {
			window.showStage();
			
			TourneyQuery query = window.getNewTourney();
			
			if (query != null) {
				
				String sqlInsert = "INSERT INTO tournament (NAME, DATE, TOTAL_ENTRANTS, GAME_VERSION) "
						+ "VALUES ('" + query.getTourney() + "', '" + query.getDate() + "', " + query.getEntries() + ", "  + query.getVersion() + ")";
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
	
	void editTourneyWindow() {
		TourneyQuery selected = (TourneyQuery) table.getSelectionModel().getSelectedItem();
		
		if (selected != null) {
			TourneyForm window = new TourneyForm(selected);
			
			if (window != null) {
				window.showStage();
				
				TourneyQuery query = window.getNewTourney();
				
				if (query != null) {
					if (!query.compareQuery(selected)) {
						
						conn = DbConnection.getConnection();
						try {
							String sqlSelect = "SELECT * FROM tournament WHERE NAME LIKE '" + selected.getTourney().replace("'", "\\'") + "'";
							
							Statement selectStatement = conn.createStatement();
							
							ResultSet rs = selectStatement.executeQuery(sqlSelect);
							
							rs.next();
							
							int id = rs.getInt(1);							
							
							String editedFields = "";
							
							if (query.getTourney() != selected.getTourney()) {
								editedFields += "NAME = '" + query.getTourney().replace("'", "\\'") + "'";
							}
							
							if (query.getDate() != selected.getDate()) {
								if (editedFields != "") {
									editedFields += ", ";
								}
								editedFields += "DATE = '" + query.getDate() + "'";
							}
							
							if (query.getEntries() != selected.getEntries()) {
								if (editedFields != "") {
									editedFields += ", ";
								}
								editedFields += "TOTAL_ENTRANTS = '" + query.getEntries() + "'";
							}
							
							if (query.getVersion() != selected.getVersion()) {
								if (editedFields != "") {
									editedFields += ", ";
								}
								editedFields += "GAME_VERSION = '" + query.getVersion() + "'";
							}
							
							String sqlUpdate = "UPDATE tournament SET " + editedFields + " WHERE TOURNEY_ID = " + id;
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
	
	void deleteTourney() {
		TourneyQuery selected = (TourneyQuery) table.getSelectionModel().getSelectedItem();
		
		if (selected != null) {
			Alert confirmation = new Alert(AlertType.CONFIRMATION);
			confirmation.setTitle("Delete Tournament");
			confirmation.setHeaderText("Are you sure you want to delete this tournament from the database? (This cannot be undone)"
					+ "\n ANY RESULTS FOR THIS TOURNAMENT WILL ALSO BE DELETED!");
			confirmation.setContentText(selected.getTourney());
			
			Optional<ButtonType> option = confirmation.showAndWait();
			
			if (option.get() == ButtonType.OK) {
				conn = DbConnection.getConnection();
				try {
					String sqlSelect = "SELECT * FROM tournament WHERE NAME LIKE '" + selected.getTourney().replace("'", "\\'") + "'";
					
					Statement selectStatement = conn.createStatement();
					
					ResultSet rs = selectStatement.executeQuery(sqlSelect);
					
					rs.next();
					
					int id = rs.getInt(1);
					
					String sqlDelete = "DELETE FROM tournament WHERE TOURNEY_ID = " + id;
					
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
}
