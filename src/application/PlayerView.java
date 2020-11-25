package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import dataTypes.PlayerQuery;
import dataTypes.Query;
import database.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

public class PlayerView extends View {

	// Create a column object for each column in the table with the appropriate data types
	TableColumn<Query, String> nameCol = new TableColumn<Query, String>("Name");
	TableColumn<Query, String> firstNameCol = new TableColumn<Query, String>("First Name");
	TableColumn<Query, String> lastNameCol = new TableColumn<Query, String>("Last Name");
	TableColumn<Query, String> charactersCol = new TableColumn<Query, String>("Character(s)");
	TableColumn<Query, Boolean> activeCol = new TableColumn<Query, Boolean>("Active");
	
	PlayerView() {
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
		
		String sqlStatement = new String("SELECT\r\n"
				+ "    p.PLAYER_ID,\r\n"
				+ "    p.FIRST_NAME,\r\n"
				+ "    p.LAST_NAME,\r\n"
				+ "    c.NAME,\r\n"
				+ "    p.ACTIVE\r\n"
				+ "FROM\r\n"
				+ "    player p\r\n"
				+ "JOIN player_character pc ON\r\n"
				+ "    (p.PLAYER_ID = pc.PLAYER_ID)\r\n"
				+ "JOIN game_character c ON\r\n"
				+ "    (\r\n"
				+ "        pc.CHARACTER_ID = c.CHARACTER_ID\r\n"
				+ "    )\r\n"
				+ "ORDER BY\r\n"
				+ "    p.FIRST_NAME,\r\n"
				+ "    p.LAST_NAME");
		PreparedStatement prepSqlStatement = null;
		ResultSet rsFindPlayers = null;		
		conn = DbConnection.getConnection();
		
		try {
			prepSqlStatement = conn.prepareStatement(sqlStatement, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rsFindPlayers = prepSqlStatement.executeQuery();
			while (rsFindPlayers.next()) {
				int tempID = rsFindPlayers.getInt(1);
				String tempFirst = rsFindPlayers.getString(2);
				String tempLast = rsFindPlayers.getString(3);
				ArrayList<String> tempCharacters = new ArrayList<String>();
				tempCharacters.add(rsFindPlayers.getString(4));
				boolean tempActive = rsFindPlayers.getBoolean(5);
				while (rsFindPlayers.next()) {
					if (tempID == rsFindPlayers.getInt(1)) {
						tempCharacters.add(rsFindPlayers.getString(4));
					} else {
						rsFindPlayers.previous();
						break;
					}
				}
				
				data.add(new PlayerQuery(tempID, tempFirst, tempLast, tempCharacters, tempActive));
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
		
		newButton.setOnAction(e -> {
			newPlayerWindow();
		});
		
		editButton.setOnAction(e -> {
			editPlayerWindow();
		});
		
		deleteButton.setOnAction(e -> {
			deletePlayer();
		});
		
		buttonBox.getChildren().addAll(newButton, editButton, deleteButton);
	}
	
	
	// Initialize the columns by linking the appropriate members in the data type and adding the columns to the table
	@SuppressWarnings("unchecked")
	@Override
	void initColumns() {
		nameCol.getColumns().addAll(firstNameCol, lastNameCol);
		
		firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		charactersCol.setCellValueFactory(new PropertyValueFactory<>("characters"));
		activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
		
		updateColumns();
	}
	
	@SuppressWarnings("unchecked")
	void updateColumns() {
		table.setItems(getQuery());
		table.getColumns().setAll(nameCol, charactersCol, activeCol);
	}
	
	void newPlayerWindow() {
		PlayerForm window = new PlayerForm(getCharacterList());
		
		if (window != null) {
			window.showStage();
			PlayerQuery query = window.getNewPlayer();
			
			if (query != null) {
				int activeValue = query.isActive() ? 0 : 1;
				
				String sqlInsert = "INSERT INTO player (PLAYER_ID, FIRST_NAME, LAST_NAME, ACTIVE) "
						+ "VALUES (NULL, '" + query.getFirstName() + "', '" + query.getLastName() + "', '" + activeValue + "')";
				
				PreparedStatement prepSqlStatement = null;
				
				conn = DbConnection.getConnection();
				
				try {
					prepSqlStatement = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
					prepSqlStatement.executeUpdate();
					
					ResultSet rs = prepSqlStatement.getGeneratedKeys();
					
					if (rs.next()) {
						int newPlayerID = rs.getInt(1);
						sqlInsert = "INSERT INTO player_character (PLAYER_ID, CHARACTER_ID, COLOR) VALUES ";
						
						ArrayList<Integer> charIds = getCharacterIDs(query.getCharacters());
						
						if (charIds != null) {
							for (int i: charIds) {
								sqlInsert += "('" + newPlayerID + "', '" + i + "', '" + 1 + "'),";
							}
							
							sqlInsert = sqlInsert.substring(0, sqlInsert.length()-1);
							
							prepSqlStatement = conn.prepareStatement(sqlInsert);
							prepSqlStatement.executeUpdate();
							
						}
						
						updateColumns();
					}
				}
				catch (SQLException ex) {
					ex.printStackTrace();
				}
			}	
		}
	}
	
	void editPlayerWindow() {
		PlayerQuery selected = (PlayerQuery) table.getSelectionModel().getSelectedItem();
		
		if (selected != null) {
			PlayerForm window = new PlayerForm(getCharacterList(), selected);
			
			if (window != null) {
				window.showStage();
				PlayerQuery query = window.getNewPlayer();
				
				if (query != null) {							
					conn = DbConnection.getConnection();
					try {						
						
						String editedFields = "";
						
						if (query.getFirstName() != selected.getFirstName()) {
							editedFields += "FIRST_NAME = '" + query.getFirstName().replace("'", "\\'") + "'";
						}
						
						if (query.getLastName() != selected.getLastName()) {
							if (editedFields != "") {
								editedFields += ", ";
							}
							editedFields += "LAST_NAME = '" + query.getLastName() + "'";
						}
						
						if (query.isActive() != selected.isActive()) {
							if (editedFields != "") {
								editedFields += ", ";
							}
							int dlcValue = query.isActive() ? 1 : 0;
							editedFields += "ACTIVE = '" + dlcValue + "'";
						}
						
						String sqlUpdate = "UPDATE player SET " + editedFields + " WHERE PLAYER_ID = " + query.getId();
					
						PreparedStatement prepSqlStatement = null;
					
						prepSqlStatement = conn.prepareStatement(sqlUpdate);
						prepSqlStatement.executeUpdate();
						
						
						String sqlDelete = "DELETE FROM player_character WHERE PLAYER_ID = " + query.getId();
						
						prepSqlStatement = conn.prepareStatement(sqlDelete);
						prepSqlStatement.executeUpdate();
						
						String sqlInsert = "INSERT INTO player_character (PLAYER_ID, CHARACTER_ID, COLOR) VALUES ";
						
						ArrayList<Integer> charIds = getCharacterIDs(query.getCharacters());
						
						if (charIds != null) {
							for (int i: charIds) {
								sqlInsert += "('" + query.getId() + "', '" + i + "', '" + 1 + "'),";
							}
							
							sqlInsert = sqlInsert.substring(0, sqlInsert.length()-1);
							
							prepSqlStatement = conn.prepareStatement(sqlInsert);
							prepSqlStatement.executeUpdate();
							
						}
						
						updateColumns();
					}
					catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

	}
	
	void deletePlayer() {
		PlayerQuery selected = (PlayerQuery) table.getSelectionModel().getSelectedItem();
		
		if (selected != null) {
			Alert confirmation = new Alert(AlertType.CONFIRMATION);
			confirmation.setTitle("Delete Player");
			confirmation.setHeaderText("Are you sure you want to delete this player from the database? (All tournament results for this player will also be deleted. This cannot be undone.)");
			confirmation.setContentText(selected.getFirstName() + selected.getLastName());
			
			Optional<ButtonType> option = confirmation.showAndWait();
			
			if (option.get() == ButtonType.OK) {
				conn = DbConnection.getConnection();
				try {
					String sqlDelete = "DELETE FROM player_character WHERE PLAYER_ID = " + selected.getId();
					
					PreparedStatement prepSqlStatement = null;
					
					prepSqlStatement = conn.prepareStatement(sqlDelete);
					prepSqlStatement.executeUpdate();
					
					sqlDelete = "DELETE FROM player WHERE PLAYER_ID = " + selected.getId();
			
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
	
	ObservableList<String> getCharacterList() {
		ObservableList<String> seriesList = FXCollections.observableArrayList();
		
		String sqlStatement = new String("SELECT NAME FROM game_character");
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
	
	ArrayList<Integer> getCharacterIDs(ArrayList<String> names) {
		if (names != null) {
			ArrayList<Integer> idList = new ArrayList<Integer>();
			String sqlStatement = new String("SELECT CHARACTER_ID FROM game_character WHERE NAME IN (");
			
			for (String s: names) {
				sqlStatement += "\"" + s + "\",";
			}
			
			sqlStatement = sqlStatement.substring(0, sqlStatement.length()-1);
			
			sqlStatement += ")";
			
			PreparedStatement prepSqlStatement = null;
			ResultSet rsFindCharacters = null;
			
			conn = DbConnection.getConnection();
			
			try {
				prepSqlStatement = conn.prepareStatement(sqlStatement);
				rsFindCharacters = prepSqlStatement.executeQuery();
				
				while (rsFindCharacters.next()) {
					int tempID = rsFindCharacters.getInt(1);
					idList.add(tempID);
				}
				
				return idList;
			}
			catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
}
