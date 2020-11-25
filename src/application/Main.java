package application;
	
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	static RankingView rankView = new RankingView();
	static TourneyView tourneyView = new TourneyView();
	static PlayerView playerView = new PlayerView();
	static CharacterView characterView = new CharacterView();
	
	// Create a TabPane to hold the tabs for switching the main view
	TabPane tabPane = new TabPane();
	
	// Create a Tab for each main view
	Tab rankingTab = new Tab("Ranking");
	Tab tournamentTab = new Tab("Tournaments");
	Tab playerTab = new Tab("Players");
	Tab characterTab = new Tab("Characters");
	
	// Create a VBox to hold everything in the scene
	VBox mainBox = new VBox(tabPane, rankView.mainBox);
	
	
	//BorderPane root = new BorderPane();
	
	// Create the main scene
	Scene scene = new Scene(mainBox,600,600);
				
	@Override
	public void start(Stage primaryStage) {
		try {
			// Add the Tabs to the TabPane
			tabPane.getTabs().add(rankingTab);
			tabPane.getTabs().add(tournamentTab);
			tabPane.getTabs().add(playerTab);
			tabPane.getTabs().add(characterTab);
			
			// Disable the option to close the tabs
			tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);		

			
			// Add a listener to the tab pane to call the changeView function when a new tab is selected
			tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
				public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
					changeView(newTab);
				}
			});
			
			// Load custom CSS file
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			// Load the primary stage with the scene and display it
			primaryStage.setScene(scene);
			primaryStage.setTitle("Power Ranking Calculator");
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	// Changes the main view to the appropriate Tab
	private void changeView(Tab tab) {
		
		if (tab == rankingTab) {
			mainBox.getChildren().set(1, rankView.mainBox);
		}
		
		if (tab == tournamentTab) {
			mainBox.getChildren().set(1, tourneyView.mainBox);
		}
		
		if (tab == playerTab) {
			mainBox.getChildren().set(1, playerView.mainBox);
		}
		
		if (tab == characterTab) {
			mainBox.getChildren().set(1, characterView.mainBox);
		}
	}
}
