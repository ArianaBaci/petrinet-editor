package application.controller;

import application.Main;
import application.model.PetriNet;
import application.view.ViewNavigator;
import application.model.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class LoginController {
	@FXML
	private Label message;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button loginButton;

	@FXML
	private Button registerButton;

	@FXML
	private Label feedback;

	private UserRepository userRepository;
	public LoginController() {
		System.out.println("LoginController constructor called");
	}

	@FXML
	public void initialize() {

		userRepository = Main.getUserRepository();
		loginButton.setDisable(true);
		registerButton.setDisable(true);
		loginButton.setDefaultButton(true);

		ChangeListener<String> listener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	buttonDisableCheck();
            }
        };
		usernameField.textProperty().addListener(listener);
		passwordField.textProperty().addListener(listener);
	}


	@FXML
	private void handleLogin() {

		String un = usernameField.getText();
		String pw = passwordField.getText();

		String res = userRepository.loginUser(un, pw);
		if (userRepository.getLoggedUser() != null) {
			goToLogged();
			return;
		}

		if (!res.equals("")) {
			feedback.setText(res);
		}
		usernameField.setText("");
		passwordField.setText("");
		return;
	}

	@FXML
	private void handleRegister() {
		String un = usernameField.getText();
		String pw = passwordField.getText();
		String res = userRepository.registerUser(un, pw);
		if (!res.equals("")) {
			feedback.setText(res);
		}
		usernameField.setText("");
		passwordField.setText("");
	}

	public void goToLogged() {
		ViewNavigator.navigateToHome();
	}

	public void buttonDisableCheck() {
		if (usernameField.getText().equals("")||passwordField.getText().equals("")) {
			loginButton.setDisable(true);
			registerButton.setDisable(true);
		} else {
			loginButton.setDisable(false);
			registerButton.setDisable(false);
		}
	}
}

