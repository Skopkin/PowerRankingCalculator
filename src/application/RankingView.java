package application;

import dataTypes.Query;
import dataTypes.RankQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;


// The Rankings View
public class RankingView extends View{
	
	// Create a column object for each column in the table with the appropriate data types
	TableColumn<Query, Integer> rankCol = new TableColumn<Query, Integer>("Rank");
	TableColumn<Query, String> playerCol = new TableColumn<Query, String>("Player");
	TableColumn<Query, String> characterCol = new TableColumn<Query, String>("Character(s)");
	
	// Constructor
	RankingView() {
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
		data.add(new RankQuery(1, "Sam", "Inkling"));
		data.add(new RankQuery(2, "Nathan", "PT"));
		
		return data;
	}
	
	// returns an HBox containing all the necessary buttons
	@Override
	void initButtons() {
		// BUTTON FUNCTIONALITY NEEDED
		Button rankingFilterButton = new Button("Filter");
		
		buttonBox.getChildren().add(rankingFilterButton);
	}
	
	// Initialize the columns by linking the appropriate members in the data type and adding the columns to the table
	@Override
	void initColumns() {
		rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
		playerCol.setCellValueFactory(new PropertyValueFactory<>("player"));
		characterCol.setCellValueFactory(new PropertyValueFactory<>("characters"));
		
		updateColumns();
	}
	
	@SuppressWarnings("unchecked")
	void updateColumns() {
		table.setItems(getQuery());
		table.getColumns().setAll(rankCol, playerCol, characterCol);
	}
	
}