package application;

import application.model.*;
import application.view.ViewNavigator;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main extends Application {

	static DatabaseManager dbManager;
	static UserRepository userRepository;
	static NetRepository netRepository;
	static ComputationRepository computationRepository;
	static ComputationStepRepository computationStepRepository;
	static Stage pStage;


	public static final String pathPrefix = "/resources";

	@Override
	public void start(Stage primaryStage) throws Exception {
		pStage = primaryStage;
		primaryStage.setMaximized(true);
		URL mainViewUrl = getClass().getResource(pathPrefix + "/fxml/MainView.fxml");
		FXMLLoader loader = new FXMLLoader(mainViewUrl);
		Parent root = loader.load();

		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		URL cssUrl = getClass().getResource(pathPrefix + "/css/styles.css");
		scene.getStylesheets().add(cssUrl.toExternalForm());

		primaryStage.setTitle("PetriFire");
		primaryStage.setScene(scene);
		primaryStage.show();

		/*
		 * Definisco il task ce carica in RAM il contenuto del database.
		 * Quando il task finisce passo dalla schermata di attesa al login.
		 */
		Task<Void> loadDatabaseTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				dbManager = new DatabaseManager();
				userRepository = new UserRepository(dbManager);
				netRepository = new NetRepository(dbManager);
				computationRepository = new ComputationRepository(dbManager, netRepository);
				computationStepRepository = new ComputationStepRepository(dbManager);
				return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				ViewNavigator.navigateToLogin();
			}
		};

		//Faccio partire il task che ho definito in un nuovo thread.
		//Nel frattempo abbiamo già caricato la LoadingView.
		new Thread(loadDatabaseTask).start();
	}

	public static void main(String[] args) {
		launch(args);

	}

	public static UserRepository getUserRepository() {
		return userRepository;
	}

	public static NetRepository getNetRepository() {
		return netRepository;
	}

	public static ComputationRepository getComputationRepository() {
		return computationRepository;
	}

	public static ComputationStepRepository getComputationStepRepository() {
		return computationStepRepository;
	}

	public static Stage getPrimaryStage() {
		return pStage;
	}
}