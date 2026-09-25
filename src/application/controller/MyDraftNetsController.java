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
import javafx.scene.control.Button;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.util.List;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;

public class MyDraftNetsController {
    @FXML
    private Label showingLabel;
    @FXML
    private BorderPane mainContainer;

    private NetBrowser content;

    private UserRepository userRepository;

    private NetRepository netRepository;

    @FXML
    private Label confirmLabel;
    @FXML
    private Label errorLabel;

    public void initialize() {
        userRepository = Main.getUserRepository();
        netRepository = Main.getNetRepository();

        //popola una lista di NetMiniature con le net che NON sono dell'utente
        List<NetMiniature>  minis = new ArrayList<NetMiniature>();
        for (PetriNet n: netRepository.getDraftNets()) {
            if (n.getAdminName().equals(userRepository.getLoggedUser().getName())) {
                NetMiniature nm = new NetMiniature(n, netRepository.getImages().get(n));
                minis.add(nm);
            }
        }
        content = new NetBrowser(minis);

        if (minis.size()>0) {

            showingLabel.setText("                                        Your Draft Nets");
            for (Node nd : content.getGrid().getChildren()) {
                if (nd instanceof NetMiniature) {
                    NetMiniature nm = (NetMiniature) nd;
                    setInteraction(nm);
                }
            }

        } else {
            showingLabel.setText("                                        There are no Draft Nets");
            Button btn = new Button();
            btn.setText("Create a new Petrinet");
            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                PetriNetEditorController.petriNet=(new PetriNet(userRepository.getLoggedUser().getName()));
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
            createNetBox(n);
        });
    }

    private void createNetBox(PetriNet n) {
        NetPopup sb = new NetPopup(n, netRepository.getImages().get(n));
        sb.setTopText("");
        sb.getButton().setText("Edit this PetriNet");
        sb.getButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            PetriNetEditorController.petriNet=n;
            ViewNavigator.navigateToPetriNetEditor();
            sb.hide();
        });
    }
    private void showSuccess(){
        confirmLabel.setVisible(true);
    }
    private void showError(){
        errorLabel.setVisible(true);
    }
}