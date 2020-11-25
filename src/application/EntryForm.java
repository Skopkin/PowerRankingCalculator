package application;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class EntryForm {

	Stage window = new Stage();
	
	String title;
	
	Label errorMessage = new Label();
	
	void setTitle(String s) {
		this.title = s;
	}
	
	void setErrorMessage(String s) {
		errorMessage.setTextFill(Color.RED);
		errorMessage.setText(s);
	}
	
	void showError() {
		errorMessage.setVisible(true);
	}
	
	void hideError() {
		errorMessage.setVisible(false);
	}
}
