package application.controller;


import application.Main;
import application.model.*;
import application.view.components.NetBrowser;
import application.view.components.NetMiniature;
import application.view.components.NetPopup;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.scene.input.MouseEvent;

public class ExploreController {

	@FXML
	private BorderPane mainContainer;
	@FXML
	private Label showingLabel;
	@FXML
	private NetBrowser content;

    private UserRepository userRepository;

    private NetRepository netRepository;

    private ComputationRepository computationRepository;

	private ComputationStepRepository computationStepRepository;
    
	public void initialize() {
        userRepository = Main.getUserRepository();
        netRepository = Main.getNetRepository();
        computationRepository = Main.getComputationRepository();
		computationStepRepository = Main.getComputationStepRepository();

        //popola una lista di NetMiniature con le net che NON sono dell'utente
        List<NetMiniature>  minis = new ArrayList<NetMiniature>();
        for (PetriNet n: getAvailableNets()) {
        		NetMiniature nm = new NetMiniature(n, netRepository.getImages().get(n));
        		minis.add(nm);
        	}
        
        content = new NetBrowser(minis);
	    
		if (minis.size()>0) {
			showingLabel.setText("                                        Available Nets");

			for (Node nd : content.getGrid().getChildren()) {
				if (nd instanceof NetMiniature) {
					NetMiniature nm = (NetMiniature) nd;
					setInteraction(nm);
				}
			}

		} else {
			showingLabel.setText("                                        No available nets :(");
		}
		
		mainContainer.setCenter(content);
	}

	private void setInteraction(NetMiniature nm) {
		PetriNet n = nm.getNet();
		Label adminLabel = nm.getAdminLabel();

		adminLabel.setCursor(Cursor.HAND);
		adminLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			filterByUserName(adminLabel.getText());
		});
		
		HBox imgbox = nm.getImgBox();
		imgbox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			createSubscribeBox(n);
		});
		imgbox.setCursor(Cursor.HAND);
		Label netName = nm.getNameLabel();
		netName.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			createSubscribeBox(n);
		});
		netName.setCursor(Cursor.HAND);		        
	}
	
	private void filterByUserName(String userName) {
		ObservableList<Node> children = content.getGrid().getChildren();
		List<Node> nodesToRemove = new ArrayList<>();

		for (Node node : children) {
			if (node instanceof NetMiniature) {
				NetMiniature nm = (NetMiniature) node;
				if (!nm.getNet().getAdminName().equals(userName)) {
					nodesToRemove.add(node);
				}
			}
		}

		for (Node node : nodesToRemove) {
			content.getGrid().getChildren().remove(node);
		}

		int x = 0;
		int y = 0;

		for (Node node : content.getGrid().getChildren()) {
			if (node instanceof NetMiniature) {
				GridPane.setColumnIndex(node, x);
				GridPane.setRowIndex(node, y);
				x++;
				if (x >= content.getGrid().getColumnCount()) {
					x = 0;
					y++;
				}
			}
		}
		showingLabel.setText("Showing available nets by user: "+userName);
		//porta automanticamente lo scroll in cima
		content.setVvalue(0);
	}

	private void createSubscribeBox(PetriNet n) {
		NetPopup sb = new NetPopup(n, netRepository.getImages().get(n));
		sb.setTopText("new subscription");
		sb.getButton().setText("Subscribe");
		sb.getButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if(computationRepository.subscribe(n,userRepository.getLoggedUser().getName())){
				initialize();
				showSuccess("Subscribed to "+n.getName());
			}
			else{
				System.out.println("NOT subscribed to "+n.getName());
			showError("Subscription to "+n.getName()+" failed");}
			});
	}
	
	private Set<PetriNet> getAvailableNets() {
		Set<PetriNet> res = new HashSet<>(netRepository.getPublishedNets());
		for (PetriNet n: netRepository.getPublishedNets()) {
			if (n.getAdminName().equals(userRepository.getLoggedUser().getName())){
					res.remove(n);
				}
	
			for (Computation c: computationRepository.getComputations()) {
				if (c.getPetriNet().getID()==n.getID() && c.getUser().equals(userRepository.getLoggedUser().getName())) {
					res.remove(n);
				}
			}
		}
		return res;
	}

	public void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.getDialogPane().getStylesheets().add(
				getClass().getResource("/resources/css/styles.css").toExternalForm());
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	public void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(
				getClass().getResource("/resources/css/styles.css").toExternalForm());
		alert.setTitle("Success");
		alert.setTitle("Errore");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
	
	
