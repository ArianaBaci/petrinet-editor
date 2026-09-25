package application.controller;

import application.view.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

public class MainController {
    @FXML
    private BorderPane mainContainer;
    @FXML
    public void initialize() {
    	ViewNavigator.setMainController(this);
    }

    public void setContent(Node content) {
        mainContainer.setCenter(content);
    }
    @FXML
    public void goToPetriNetEditor() {
        ViewNavigator.navigateToPetriNetEditor();
    }
}


