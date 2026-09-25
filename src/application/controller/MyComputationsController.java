package application.controller;

import application.Main;
import application.model.*;
import application.view.ViewNavigator;
import application.view.components.MessageBox;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Optional;

public class MyComputationsController {
    @FXML
    public ListView<Computation> UserComputations;
    @FXML
    public ListView<Computation> AdminComputations;
    @FXML
    public ListView computationHistory;
    @FXML
    public Button undoButton;
    public ListView<Computation> ComputationsToStart;
    @FXML
    private Label computationTypeLabel;
    @FXML
    private ListView<String> AvailableTransitions;

    private PetriNet petriNet;
    @FXML
    private ScrollPane graphScrollPane;
    @FXML
    private Pane graphContainer;
    @FXML
    private Label confirmLabel;
    @FXML
    private Label errorLabel;

    public DotLayoutExtractor.DotLayoutResult coordinate;

    private double Yf, Xf, Xi, Yi;

    private Image currentImage;
    private UserRepository userRepo;
    private ComputationRepository computationRepo;
    private Marking marking;
    private ComputationStep computationStep;
    private int isMarked;
    private boolean isFireable;
    private ComputationStepRepository computationStepRepository;
    public static Computation computation;
    //1=admin, 2=user
    private static int type;
    @FXML
    private Label computationUserLabel;


    public void initialize() {
        if (petriNet != null) {
            petriNet.ObsNodesNumber.clear();
        }

        graphContainer.getChildren().clear();
        userRepo = Main.getUserRepository();
        computationRepo = Main.getComputationRepository();
        computationStepRepository = Main.getComputationStepRepository();
        errorLabel.setVisible(false);
        confirmLabel.setVisible(false);
        computationUserLabel.setVisible(false);
        fillComputationsToStart();
        for(Computation c : computationRepo.getComputations()){
            System.out.println( "COMP ID) : " + c.getID());
            for(ComputationStep cs : computationStepRepository.getComputationSteps()){
                System.out.println( "COMP STEP ID) : " + cs.getID());
                System.out.println(cs.getMarkingData().getMarking());
            }
        }
        if (computation == null) {
            computationTypeLabel.setText("Select a computation from the lists");
            fillComputationLists();
            graphScrollPane.setPannable(true);
            graphScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            graphScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        } else {
            fillComputationHistory();
            fillComputationLists();
            fillAvailableTransitions();
            petriNet = computation.getPetriNet();
            graphScrollPane.setPannable(true);
            graphScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            graphScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            computationTypeLabel.setText("Computation Net: " + computation.getPetriNet().getName() + ",");
            petriNet.ObsNodesNumber.addListener((ListChangeListener<Integer>) c -> {
                simulate();
            });


            if (computation.getPetriNet().getAdminName().equals(userRepo.getLoggedUser().getName())) {
                type = 1;
                computationUserLabel.setText("  User: " + computation.getUser());
                computationUserLabel.setVisible(true);
            } else {
                type = 2;
                computationUserLabel.setText("  Admin: " + computation.getPetriNet().getAdminName());
                computationUserLabel.setVisible(false);
            }

            computationStep = computationStepRepository.getComputationSteps().getLast();
            confirmLabel.setVisible(false);
            confirmLabel.getStyleClass().add("alert-success");
            simulate();
        }
    }

    @FXML
    private void simulate() {
        AvailableTransitions.getItems().clear();
        computationHistory.getItems().clear();
        fillComputationLists();
        fillAvailableTransitions();
        fillComputationHistory();
        if (type == 1) {
            computationUserLabel.setText("  User: " + computation.getUser());
            computationUserLabel.setVisible(true);
        }else if(type== 2){
            computationUserLabel.setText("  Admin: " + computation.getPetriNet().getAdminName());
            computationUserLabel.setVisible(true);
        }
        marking=computation.getLastStep().getMarkingData();
            for (Place p : petriNet.getPlaces()) {
                if(marking.getMarking().get(p.getID())==null){
                marking.getMarking().put(p.getID(), 0);
                marking.getMarking().put(petriNet.getInitialPlace(), 1);
            }
            }
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
                    if (arc.getTargetID() == nodeInfo.id) {
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
                Node node = petriNet.getNode(nodeInfo.id);

                if (node instanceof Place) {
                        isMarked = marking.getMarking().get(nodeInfo.id);
                        interactiveNode = InteractiveNodes.makePlace(isMarked, 2, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                 }
               else  if (node instanceof Transition) {
                    if (computation.enabledTransitions().contains((Transition) node)) {
                        isFireable = true;
                    } else {
                        isFireable = false;
                    }
                    if (((Transition) node).getUserType().equals(UserType.ADMIN)) {
                        if (type == 2) {
                            isFireable = false;
                        }
                        interactiveNode = InteractiveNodes.makeAdminTransition(computationRepo,computationStepRepository, computation, isFireable, 2, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                    } else {
                        if (type == 1) {
                            isFireable = false;
                        }
                        interactiveNode = InteractiveNodes.makeUserTransition(computationStepRepository, computation, isFireable, 2, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                    }
                } else {
                    continue;
                }
                graphContainer.getChildren().add(interactiveNode);
            }
        } catch (Exception e) {
            showError("Error rendering graph: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null); // nessun header, solo messaggio
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void openUserComputation(ActionEvent actionEvent) {
        MyComputationsController.computation = UserComputations.getSelectionModel().getSelectedItem();
        initialize();
    }
    @FXML
    public void StartComputation(ActionEvent actionEvent) {
        MyComputationsController.computation = ComputationsToStart.getSelectionModel().getSelectedItem();
        initialize();
    }

    @FXML
    public void openAdminComputation(ActionEvent actionEvent) {
        MyComputationsController.computation = AdminComputations.getSelectionModel().getSelectedItem();
        initialize();
    }

    public void fillComputationLists() {
        UserComputations.getItems().clear();
        AdminComputations.getItems().clear();
        for (Computation computation : computationRepo.getComputations()) {
            if (!(computation.isComplete()) && computation.getSteps().size()>1) {
                if (computation.getUser().equals(userRepo.getLoggedUser().getName())) {
                    UserComputations.getItems().add(computation);
                }
            }
        }
        for (Computation computation : computationRepo.getComputations()) {
            if (computation.getPetriNet().getAdminName().equals(userRepo.getLoggedUser().getName()) && !(computation.isComplete()) && computation.getSteps().size()>1) {
                AdminComputations.getItems().add(computation);
            }
        }
    }

    public void fillAvailableTransitions() {
        AvailableTransitions.getItems().clear();
        if (type == 1) {
            for (Transition transition : computation.adminEnabledTransitions()) {
                AvailableTransitions.getItems().add(transition.getName());
            }
        } else if (type == 2) {
            for (Transition transition : computation.enduserEnabledTransitions()) {
                AvailableTransitions.getItems().add(transition.getName());
            }
        }
    }

    public void fillComputationHistory() {
        computationHistory.getItems().clear();

        computationHistory.getItems().add(computation.getStart_date() + " " + computation.getUser() + " Subscribed");
        for (ComputationStep step : computationStepRepository.getComputationSteps()) {
            if (step.getComputationID() == computation.getID()) {
                computationHistory.getItems().add(step.toString());
            }
        }
    }

    @FXML
    public void undo() {
        computation.enabledTransitions().add((Transition) petriNet.getNode(computationStep.getTransitionID()));
        computationStepRepository.deleteComputationStep(computationStep);
        marking = computationStepRepository.getComputationSteps().getLast().getMarkingData();

        simulate();
    }
    @FXML
    public void fillComputationsToStart(){
        ComputationsToStart.getItems().clear();
        for(Computation computation : computationRepo.getComputations()){
            if(computation.getUser().equals(userRepo.getLoggedUser().getName())){
                if(computation.getSteps().size()==1){
                    ComputationsToStart.getItems().add(computation);
                }
            }
        }
    }
    @FXML
    public void deleteComputation() {
        if (computationRepo.deleteComputation(computation) && computationRepo.subscribe(computation.getPetriNet(),computation.getUser())) {
            confirmLabel.setVisible(true);
            petriNet.ObsNodesNumber.add(1);
            confirmLabel.setText("Computation successfully deleted");
        } else {
            errorLabel.setVisible(true);
            errorLabel.setText("Errore durante la computazione");
        }
      computation=null;
       initialize();
    }
}