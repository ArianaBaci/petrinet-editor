package application.controller;

import application.Main;
import application.model.*;
import application.view.components.MessageBox;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

public class CompletedComputationsController{
    @FXML
    public ListView computationHistory;
    @FXML
    private Label confirmLabel;
    @FXML
    private Label errorLabel;
    @FXML
    public ListView<Computation> enduserComputations;
    @FXML
    public ListView<Computation> adminComputations;
    @FXML
    private Label computationTypeLabel;
    @FXML
    private NetRepository netRepo;
    private PetriNet petriNet;
    @FXML
    private ScrollPane graphScrollPane;
    @FXML
    private Pane graphContainer;

    private final String PANECOLOR = "LIGHTGREY";

    public DotLayoutExtractor.DotLayoutResult coordinate;

    private double Yf, Xf, Xi, Yi;

    private Image currentImage;
    private static int i;
    private MessageBox messageBox;
    private DatabaseManager dbManager;
    private UserRepository userRepo;
    private ComputationRepository computationRepo;
    private Marking marking;
    private ComputationStep computationStep;
    private int isMarked;
    private int isCompleted;
    private boolean isFireable;
    private ComputationStepRepository computationStepRepository;
    public static Computation computation;
    //1=admin, 2=user
    private static int type;


    public void initialize() {
        graphContainer.getChildren().clear();
        userRepo = application.Main.getUserRepository();
        netRepo = application.Main.getNetRepository();
        computationRepo = Main.getComputationRepository();
        computationStepRepository = Main.getComputationStepRepository();
        errorLabel.setVisible(false);
        confirmLabel.setVisible(false);

        if (computation == null) {
            computationTypeLabel.setText("Select a computation from the lists");
            fillComputationCompleted();
            graphScrollPane.setPannable(true);
            graphScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            graphScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        } else {

            petriNet = computation.getPetriNet();
            graphScrollPane.setPannable(true);
            graphScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            graphScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            computationTypeLabel.setText("Computation Net: " + computation.getPetriNet().getName());
            petriNet.ObsNodesNumber.addListener((ListChangeListener<Integer>) c -> {
                simulate();
            });
            fillComputationCompleted();
            fillComputationHistory();
            simulate();
        }
    }

    @FXML
    private void simulate() {

       fillComputationCompleted();
       fillComputationHistory();
        try {
            coordinate = DotLayoutExtractor.extractLayout(DotLang.translate(petriNet), petriNet.getNodes());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errore nel metodo extractnodeposition");
            return;
        }
        try {
            double minX = 0;
            double minY = 0;
            double maxX = 0;
            double maxY = 0;

            for (DotLayoutExtractor.NodeInfo node : coordinate.nodes) {
                if (node.x < minX) minX = node.x;
                if (node.y < minY) minY = node.y;
                if (node.x > maxX) maxX = node.x;
                if (node.y > maxY) maxY = node.y;
            }
            double graphWidth = maxX - minX;
            double graphHeight = maxY - minY;

            graphContainer.getChildren().clear();

            currentImage = DotRenderer.renderDotToImage(DotLang.translate(petriNet));
            double w = currentImage.getWidth();
            double h = currentImage.getHeight();

            double scaleX = w / graphWidth;
            double scaleY = h / graphHeight;

            for (Arc arc : petriNet.getArcs()) {
                Line line = new Line();
                for (DotLayoutExtractor.NodeInfo nodeInfo : coordinate.nodes) {
                    if (arc.getSourceID() == nodeInfo.id) {
                        Xi = 30 + (nodeInfo.x - minX) * scaleX;
                        Yi = (maxY + 0.3 - nodeInfo.y) * scaleY;
                        line.setStartX(Xi);
                        line.setStartY(Yi);

                    }
                    if (arc.getTargetID() ==nodeInfo.id) {
                        Xf = 30 + (nodeInfo.x - minX) * scaleX;
                        Yf = (maxY + 0.3 - nodeInfo.y) * scaleY;
                        line.setEndX(Xf);
                        line.setEndY(Yf);
                    }
                }

                Group arrow = InteractiveNodes.createArrow(line);
                graphContainer.getChildren().add(arrow);
            }
            for (DotLayoutExtractor.NodeInfo nodeInfo : coordinate.nodes) {
                double fxX = (nodeInfo.x - minX) * scaleX;
                double fxY = +(maxY + 0.1 - nodeInfo.y) * scaleY;

                StackPane interactiveNode;
                Node node = petriNet.getNode(nodeInfo.name);

                if (node instanceof Place) {
                    interactiveNode = InteractiveNodes.makePlace(0, 2, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                } else if (node instanceof Transition && ((Transition) node).getUserType().equals(UserType.ADMIN)) {
                        interactiveNode = InteractiveNodes.makeAdminTransition(computationRepo,computationStepRepository, computation, false, 2, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                    } else if (node instanceof Transition && ((Transition) node).getUserType().equals(UserType.ENDUSER)) {
                    interactiveNode = InteractiveNodes.makeUserTransition(computationStepRepository, computation, false, 2, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                } else{ continue;
                        }
                graphContainer.getChildren().add(interactiveNode);
            }
        } catch (Exception e) {
            showError("Error rendering graph: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void confirm(ActionEvent actionEvent) {
    }
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null); // nessun header, solo messaggio
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void fillComputationCompleted(){
        enduserComputations.getItems().clear();
        adminComputations.getItems().clear();
        for (Computation computation : computationRepo.getComputations()) {
            if(computation.isComplete()){
            if (computation.getUser().equals(userRepo.getLoggedUser().getName())) {
                enduserComputations.getItems().add(computation);
            }
            else if(computation.getPetriNet().getAdminName().equals(userRepo.getLoggedUser().getName())) {
            adminComputations.getItems().add(computation);}
            }
        }
    }
    @FXML
    public void openAdminComputation() {
        CompletedComputationsController.computation=adminComputations.getSelectionModel().getSelectedItem();
        initialize();
    }
    @FXML
    public void openEnduserComputation() {
        CompletedComputationsController.computation=enduserComputations.getSelectionModel().getSelectedItem();
        initialize();
    }
    public void fillComputationHistory() {
        computationHistory.getItems().clear();
        computationHistory.getItems().add(computation.getUser() + " Subscribed in date: " + computation.getStart_date());
        for (ComputationStep step : computation.getSteps()){
            computationHistory.getItems().add(step);
    }
        computationHistory.getItems().add("Completed in date: " + computation.getEnd_date());
    }
    @FXML

    public void deleteComputation() {
        if (computationRepo.deleteComputation(computation) && computationRepo.subscribe(computation.getPetriNet(),computation.getUser())) {
            confirmLabel.setVisible(true);
            confirmLabel.setText("Computation successfully deleted");
            petriNet.ObsNodesNumber.add(1);
        } else {
            errorLabel.setVisible(true);
            errorLabel.setText("Error occurred while deleting computation");
        }
        computation=null;
        initialize();
    }
}
