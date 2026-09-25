package application.controller;


import application.Main;
import application.view.ViewNavigator;
import application.view.components.TransitionBrowser;
import application.view.components.TransitionMiniature;
import application.model.UserRepository;
import application.model.NetRepository;
import application.model.Computation;
import application.model.CompTransition;
import application.model.ComputationRepository;
import application.model.PetriNet;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.scene.input.MouseEvent;

public class InboxController {

	@FXML
	private BorderPane mainContainer;
	@FXML
	private TransitionBrowser content;

    private UserRepository userRepository;

    private NetRepository netRepository;

    private ComputationRepository computationRepository;
	@FXML
	private Label showingLabel;
    
	public void initialize() {
        userRepository = Main.getUserRepository();
        netRepository = Main.getNetRepository();
        computationRepository = Main.getComputationRepository();


        List<CompTransition> ctList = getFilteredCTList();
        
        List<TransitionMiniature> minis = new ArrayList<TransitionMiniature>();
        for (CompTransition ct : ctList) {
        	PetriNet n = netRepository.getNet(ct.getComputation().getPetriNet().getID());
        	TransitionMiniature tm = new TransitionMiniature(ct.getTransition(), ct.getComputation(), 
        			netRepository.getImages().get(n));
        	minis.add(tm);
        }
        
        content = new TransitionBrowser(minis);
        mainContainer.setCenter(content);
	    
		if (minis.size()>0) {
			showingLabel.setText("                                        Your available transitions");
			for (TransitionMiniature tm : content.getMiniatures()) {
					setInteraction(tm);
			}
		} else {
			showingLabel.setText("                                                    No available transitions :(");
		}
		
	}

	private void setInteraction(TransitionMiniature tm) {
		tm.setCursor(Cursor.HAND);
		tm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			MyComputationsController.computation = tm.getComputation();
			ViewNavigator.navigateToMyComputations();
		});
	}
	
	private List<CompTransition> getFilteredCTList() {
        List<CompTransition> ctList =computationRepository.getEnabledUserTransitions(userRepository.getLoggedUser().getName());
        ctList.addAll(computationRepository.getEnabledAdminTransitions(userRepository.getLoggedUser().getName()));
        List<CompTransition> toStart = new ArrayList<>();
        for (CompTransition ct : ctList) {
        	if (ct.getComputation().getSteps().size()<2) {
        		toStart.add(ct);
        	}
        }
        for (CompTransition ct : toStart) {
        	ctList.remove(ct);
        }
        ctList.addAll(toStart);
        return ctList;
	}

}
	
	
