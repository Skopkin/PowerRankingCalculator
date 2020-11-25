package application;

import java.sql.Connection;

import dataTypes.Query;
//import database.DbConnection;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



public abstract class View{
	
	// An HBox to store the table's functionality buttons (add/edit/delete etc.)
	HBox buttonBox;
	
	// A TableView using the generic Query type so that any view may create its own custom Queries
	TableView<Query> table;
	
	// The VBox that will contain the Button Box, Table, and any other necessary elements
	VBox mainBox;
	
	// An observable list that will store any Query types.
	// (Used to store data from the DB and load into the table
	ObservableList<Query> data;
	
	// A connection variable for the database
	Connection conn = null;
	
	// Constructor
	View() {
		mainBox = new VBox();
		buttonBox = new HBox();
		table = new TableView<Query>();
		
		mainBox.getChildren().addAll(buttonBox, table);}
	
	// Method for initializing custom buttons for the view
	void initButtons() {}
	
	// Method for initializing the columns of the table
	void initColumns() {}
	
	// Query database and store the query in the data list
	void updateColumns() {}
	
	
	public ObservableList<Query> getQuery() {
		return data;
	}
	
}
