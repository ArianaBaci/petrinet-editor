package application.controller;

import application.Main;
import application.model.*;
import application.view.ViewNavigator;
import application.view.components.NetMiniature;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class NetSubscriptionController {
    @FXML
    public Button manageStatus;
    @FXML
    ListView<Computation> Subscriptions;
    @FXML
    Button manageComputation;
    @FXML
    ListView<ComputationStep> RecentActivities;
    @FXML
    public Label netName;
    @FXML
    private ImageView netImageView;
    @FXML
    private Label Status;
    public static NetMiniature netMiniature;
    public static PetriNet net;
    public static NetRepository netRepo;
    private ComputationRepository compRepo;
    private UserRepository userRepo;
    public static Computation computation;
    @FXML
    private Label statusLabel;
    @FXML
    private void initialize() {
        net.ObsNodesNumber.addListener((ListChangeListener<Integer>) c -> {
            Stage stage = (Stage) manageComputation.getScene().getWindow();
            stage.close();
        });
        Image netImage = netRepo.getImages().get(net);
        userRepo = Main.getUserRepository();
        netImageView.setImage(netImage);
        netName.setText("     PetriNet Name: " + net.getName() + "   Admin:  " + net.getAdminName());
        compRepo = Main.getComputationRepository();
        netMiniature = new NetMiniature(net, netRepo.getImages().get(net));
        statusLabel.setVisible(false);
        manageStatus.setVisible(true);
        if(computation==null ) {
            manageStatus.setDisable(true);
            statusLabel.setVisible(true);
        }
    }
@FXML
    public void unsubscribe() {
    if (computation != null){
        compRepo.deleteComputation(computation);
        net.ObsNodesNumber.add(1);
}
    }
   @FXML
    public void manageComputation() {
        if(computation==null || computation.isComplete()){
            CompletedComputationsController.computation=computation;
            ViewNavigator.navigateToCompletedComputations();
            Stage stage = (Stage) manageComputation.getScene().getWindow();
            stage.close();
            manageStatus.setDisable(true);
            statusLabel.setVisible(true);
        }
        if(!computation.isComplete()) {
            MyComputationsController.computation = computation;
            ViewNavigator.navigateToMyComputations();
            Stage stage = (Stage) manageComputation.getScene().getWindow();
            stage.close();
            statusLabel.setVisible(false);
    } if(computation.isComplete()) {
           CompletedComputationsController.computation = computation;
           ViewNavigator.navigateToMyComputations();
           Stage stage = (Stage) manageComputation.getScene().getWindow();
           stage.close();
           statusLabel.setVisible(false);
       }
       Stage stage = (Stage) manageComputation.getScene().getWindow();
       stage.close();
}
}
