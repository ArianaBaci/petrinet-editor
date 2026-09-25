package application.controller;


import application.Main;
import application.view.ViewConstants;
import application.model.ComputationRepository;
import application.model.PetriNet;
import application.view.ViewNavigator;
import application.model.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBarController {
	
	@FXML
	private HBox navBar;
	
	@FXML
	private MenuButton computationButton;
	@FXML
	private MenuItem active;
	@FXML
	private MenuItem completed;
	@FXML
	private  MenuItem published;
	@FXML
	private MenuItem drafts;
	@FXML
	private MenuButton myNetsButton;
	@FXML
	private Label welcomeMsg;
	@FXML
	private Button logoutButton;
	@FXML
	private Region spacer;

	private UserRepository userRepository;

	private ComputationRepository computationRepository;

	public void initialize() {
        navBar.setStyle("-fx-background-color: " + ViewConstants.POPUP_BG_COLOR+ ";" +
                "-fx-border-style: hidden hidden solid hidden;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: "+ViewConstants.POPUP_BORDER_COLOR+"; ");
		computationButton.getStyleClass().add("menu-button");
		computationButton.getItems().addAll(active,completed);
		computationButton.setText("My Computations");

		myNetsButton.getStyleClass().add("menu-button");
		myNetsButton.getItems().addAll(published,drafts);
		myNetsButton.setText("My Nets");

		HBox.setHgrow(spacer, Priority.ALWAYS);
		userRepository = Main.getUserRepository();
		computationRepository = Main.getComputationRepository();
		if (userRepository.getLoggedUser() != null)
			welcomeMsg.setText(welcomeMsg.getText() + " " + userRepository.getLoggedUser().getName() + "! ");
		else
			welcomeMsg.setText(welcomeMsg.getText() + " guest");
	}

	@FXML
	public void handleHome() {
		ViewNavigator.navigateToHome();
	}

	@FXML
	public void handleMyNets() {
		ViewNavigator.navigateToMyNets();
	}

	@FXML
	public void handleExplore() {
		ViewNavigator.navigateToExplore();
	}

	@FXML
	public void handleEditor() {
		PetriNetEditorController.petriNet=new PetriNet(userRepository.getLoggedUser().getName());
		ViewNavigator.navigateToPetriNetEditor();
	}


	@FXML
	public void handleInbox() {
		ViewNavigator.navigateToInbox();
	}

	@FXML
	public void handleLogout() {
		userRepository.logout();
		ViewNavigator.navigateToLogin();
	}

	@FXML
	public void handleButton() {
		System.out.println("placeholder button");
	}
	@FXML
	public void handleActiveComputations(ActionEvent actionEvent) {
		MyComputationsController.computation=null;
		ViewNavigator.navigateToMyComputations();
	}
	@FXML
	public void handleCompletedComputations(ActionEvent actionEvent) {
		CompletedComputationsController.computation=null;
		ViewNavigator.navigateToCompletedComputations();
	}

	public void handleMyDraftNets(ActionEvent actionEvent) {
		ViewNavigator.navigateToMyDraftNets();
	}
}