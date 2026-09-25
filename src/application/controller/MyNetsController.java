package application.controller;

import application.Main;
import application.view.ViewNavigator;
import application.view.components.NetBrowser;
import application.view.components.NetMiniature;
import application.view.components.NetPopup;
import application.model.UserRepository;
import application.model.NetRepository;
import application.model.PetriNet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyNetsController {

	@FXML
	private Label showingLabel;
	@FXML
	private BorderPane mainContainer;

	private NetBrowser content;

	private UserRepository userRepository;

	private NetRepository netRepository;

	public void initialize() {
		userRepository = Main.getUserRepository();
		netRepository = Main.getNetRepository();
		showingLabel.setText("                                        Your Published Nets");
		//popola una lista di NetMiniature con le net che NON sono dell'utente
		List<NetMiniature> minis = new ArrayList<NetMiniature>();
		for (PetriNet n : netRepository.getPublishedNets()) {
			if (n.getAdminName().equals(userRepository.getLoggedUser().getName())) {
				NetMiniature nm = new NetMiniature(n, netRepository.getImages().get(n));
				minis.add(nm);
			}
		}
		content = new NetBrowser(minis);

		if (minis.size() > 0) {

			for (Node nd : content.getGrid().getChildren()) {
				if (nd instanceof NetMiniature) {
					NetMiniature nm = (NetMiniature) nd;
					setInteraction(nm);
				}
			}

		} else {
			showingLabel.setText("                                        You haven't made any nets yet :(");
			Button btn = new Button();
			btn.setText("Create a New Petrinet");
			btn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				PetriNetEditorController.petriNet = (new PetriNet(userRepository.getLoggedUser().getName()));
				ViewNavigator.navigateToPetriNetEditor();
			});
			content.getBox().getChildren().add(btn);
		}

		mainContainer.setCenter(content);
	}

	private void setInteraction(NetMiniature nm) {
		PetriNet n = nm.getNet();
		nm.setCursor(Cursor.HAND);
		nm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				createNetBox(n);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	private void createNetBox(PetriNet n) throws IOException {
		NetPopUpController.net = n;
		NetPopUpController.netRepo = netRepository;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/NetPopUpView.fxml"));
			Parent root = loader.load();
			Stage popupStage = new Stage();
			popupStage.setTitle("Manage Your Net");
			popupStage.setScene(new Scene(root));
			popupStage.initModality(Modality.APPLICATION_MODAL);
			root.getStylesheets().add(getClass().getResource("/resources/css/styles.css").toExternalForm());
			popupStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

