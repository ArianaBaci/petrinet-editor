package application.controller;

import application.Main;
import application.model.*;
import application.view.ViewNavigator;
import application.view.components.NetMiniature;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class NetPopUpController {
    @FXML
    ListView<Computation> Subscriptions;
    @FXML
    Button manageComputation;
    @FXML
    ListView RecentActivities;
    @FXML
    public Label netName;
    @FXML
    private ImageView netImageView;
    public static NetMiniature netMiniature;
    public static PetriNet net;
    public static NetRepository netRepo;
    private ComputationRepository compRepo;

    @FXML
    private void initialize() {
        Subscriptions.setCellFactory(param -> new ListCell<Computation>() {
            @Override
            protected void updateItem(Computation c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText("USER SUBSCRIBED: " + c.getUser());
                }
            }
        });
        netRepo = Main.getNetRepository();
       Image netImage =netRepo.getImages().get(net);
       netImageView.setImage(netImage);
       netName.setText("     PetriNet Name: " +net.getName());
       compRepo= Main.getComputationRepository();
        netMiniature=new NetMiniature(net, netRepo.getImages().get(net));
            for(Computation computation:compRepo.getComputations()){
              if(computation.getPetriNet().getName().equals(net.getName())){
              Subscriptions.getItems().add(computation);
              if(computation.getLastStep()!=null) {
                  RecentActivities.getItems().add(computation.getLastStep());
              }else{
                  RecentActivities.getItems().add(computation.getUser() + " subscribed ");
              }
              }
            }
            manageComputation.setOnAction(event -> {
                if(Subscriptions.getSelectionModel().getSelectedItem().isComplete()) {
                    CompletedComputationsController.computation = Subscriptions.getSelectionModel().getSelectedItem();
                    ViewNavigator.navigateToCompletedComputations();
                }else if(!((Subscriptions.getSelectionModel().getSelectedItem().isComplete()))){
                    MyComputationsController.computation = Subscriptions.getSelectionModel().getSelectedItem();
                    ViewNavigator.navigateToMyComputations();
                }
                Stage stage = (Stage) manageComputation.getScene().getWindow();
                stage.close();
            });
    }

}
