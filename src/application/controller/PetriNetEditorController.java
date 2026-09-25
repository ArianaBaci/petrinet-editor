//implementare metodo showerror e showsuccess

package application.controller;
import application.Main;
import application.model.*;

import application.view.ViewNavigator;
import application.view.components.NetDotImage;
import application.model.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import application.view.components.MessageBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import net.sourceforge.plantuml.klimt.sprite.SpriteImage;

import java.util.Optional;

public class PetriNetEditorController {
    @FXML
    public ListView<Arc> arcList;
    @FXML
    public Button saveDraft;
    @FXML
    public ListView<Node> FinalPlaces;
    @FXML
    public Label finalPlacesLabel;
    @FXML
    public Label finalTransitionsLabel;
    @FXML
    public ComboBox<Node> arcTargetField;
    @FXML
    public ComboBox<Node> arcSourcesField;
    @FXML
    private Button loadArcs;
    @FXML
    public Button SetDraftButton;
    @FXML
    public Button Save;
    @FXML
    public Button PublishButton;
    @FXML
    private Label NetName;
    @FXML
    private ScrollPane graphScrollPane;
    @FXML
    private Pane graphContainer;
    @FXML
    private ComboBox<Node> sourcesBox;
    @FXML
    private CheckBox filterCheck;
    @FXML
    private Button imageSimulation;

    private final String PANECOLOR = "LIGHTGREY";

    private Image currentImage;

    public DotLayoutExtractor.DotLayoutResult coordinate;

    private double Yf, Xf, Xi, Yi;

    private UserRepository userRepo;

    private NetRepository netRepo;

    public static PetriNet petriNet;
    @FXML
    private Label confirmDraftLabel;
    @FXML
    private Label errorDraftLabel;

    @FXML
    public void initialize() {
        arcList.setCellFactory(param -> new ListCell<Arc>() {
            @Override
            protected void updateItem(Arc arc, boolean empty) {
                super.updateItem(arc, empty);
                if (empty || arc == null) {
                    setText(null);
                } else {
                    setText(petriNet.getNode(arc.getSourceID()).getName() + " -> " + petriNet.getNode(arc.getTargetID()).getName());
                }
            }
        });


        userRepo = Main.getUserRepository();
        netRepo = Main.getNetRepository();
        NetName.setText(petriNet.getName());
        graphScrollPane.setPannable(true);
        graphScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        graphScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        petriNet.ObsNodesNumber.addListener((ListChangeListener<Integer>) c -> {
            simulate();
        });
        simulate();
    }

    @FXML
    private void simulate() {
        confirmDraftLabel.setVisible(false);
        errorDraftLabel.setVisible(false);
        arcTargetField.getItems().clear();
        arcSourcesField.getItems().clear();
        arcList.getItems().clear();
        sourcesBox.getItems().clear();
        finalPlacesLabel.setVisible(false);
        finalTransitionsLabel.setVisible(false);
        getGraphLayout();
        setComboboxes();
        getFinalPlaces();
    }

    //estrae e adatta le coordinate dei nodi dell'immagine generata da Dot, le assegna ai nodi interattivi e popola il graphContainer
    private void getGraphLayout() {
        try {
            //coordinate è uno struct con lista coordinate dot di nodi e archi e le dim del grafo dot
            coordinate = DotLayoutExtractor.extractLayout(DotLang.translate(petriNet), petriNet.getNodes());
        } catch (Exception e) {
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
            //dimensione grafo dot
            double graphWidth = maxX - minX;
            double graphHeight = maxY - minY;

            System.out.println("Graph width: " + graphWidth);
            System.out.println("Graph height: " + graphHeight);

            graphContainer.getChildren().clear();

            currentImage = DotRenderer.renderDotToImage(DotLang.translate(petriNet));
            double w = currentImage.getWidth();
            double h = currentImage.getHeight();

            double scaleX = w / graphWidth;
            double scaleY = h / graphHeight;
            int countArcs = 0;

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
                countArcs++;
                sourcesBox.getItems().add(petriNet.getNode(arc.getSourceID()));
                Node selectedSource = sourcesBox.getSelectionModel().getSelectedItem();
                if (filterCheck.isSelected() && selectedSource != null) {
                    if (petriNet.getNode(arc.getSourceID()).equals(selectedSource)) {
                        arcList.getItems().add(arc);
                    }
                } else if (!filterCheck.isSelected()) {
                    arcList.getItems().add(arc);
                }
                graphContainer.getChildren().add(arrow);
            }

            for (DotLayoutExtractor.NodeInfo nodeInfo : coordinate.nodes) {
                double fxX = (nodeInfo.x - minX) * scaleX;
                double fxY = +(maxY + 0.1 - nodeInfo.y) * scaleY;

                StackPane interactiveNode;
                Node node = petriNet.getNode(nodeInfo.id);

                if (node instanceof Place) {
                    interactiveNode = InteractiveNodes.makePlace(0, 1, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                } else if (node instanceof Transition) {
                    if (((Transition) node).getUserType().equals(UserType.ADMIN)) {
                        interactiveNode = InteractiveNodes.makeAdminTransition(null,null, null, false, 1, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
                    } else {
                        interactiveNode = InteractiveNodes.makeUserTransition(null, null, false, 1, graphContainer, petriNet, petriNet.getNode(nodeInfo.name).getID(), fxX, fxY);
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

    //inizializza e aggiorna (usando un listener) le due combobox per gli archi (la seconda si popola a seconda dell'elemento selezionato nella prima
    private void setComboboxes() {
        for (Node node : petriNet.getNodes()) {
            arcSourcesField.getItems().add(node);
        }
        arcSourcesField.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                arcTargetField.setDisable(false);
                arcTargetField.getItems().clear();

                if (newValue instanceof Transition) {
                    for (Place place : petriNet.getPlaces()) {
                        if (petriNet.getArc(place, newValue) == null && petriNet.getArc(newValue, place) == null) {
                            if (petriNet.getInitialPlace() != place.getID())
                                arcTargetField.getItems().add(place);
                        }
                    }
                } else {
                    for (Transition transition : petriNet.getTransitions()) {
                        if (petriNet.getArc(transition, newValue) == null && petriNet.getArc(newValue, transition) == null)
                            arcTargetField.getItems().add(transition);
                    }
                }
            } else {
                arcTargetField.setDisable(true);
                arcTargetField.getItems().clear();
            }
        });
    }

    //filtra la lista di archi in base alla source selezionata
    @FXML
    private void arcFilter() {
        int i = 1;

        arcList.getItems().clear();
        for (Arc arc : petriNet.getArcs()) {
            if (filterCheck.isSelected()) {
                Node selectedSource = sourcesBox.getSelectionModel().getSelectedItem();
                if (petriNet.getNode(arc.getSourceID()).equals(selectedSource)) {
                    arcList.getItems().add(arc);
                }
            } else if (!filterCheck.isSelected()) {
                arcList.getItems().add(arc);
            }
        }
    }

    //elimina arco
    @FXML
    private void removeArc() {
        Arc selected = arcList.getSelectionModel().getSelectedItem();
        petriNet.removeArc(petriNet.getNode(selected.getSourceID()), petriNet.getNode(selected.getTargetID()));
        simulate();
    }

    //Rinomina la petrinet
    @FXML
    public void renameNet(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog(petriNet.getName());
        dialog.setTitle("Rename Net");
        dialog.setHeaderText("Edit name");
        dialog.setContentText("New Name:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newName -> {
            petriNet.setName(newName);
            NetName.setText(newName);
        });
    }

    @FXML
    private void showNetImage() {
        NetDotImage netDotImage = new NetDotImage(petriNet);
    }

    //Popola la listview finalPlaces
    private void getFinalPlaces() {
        FinalPlaces.getItems().clear();
        for (Place place : petriNet.getPlaces()) {
            if (petriNet.checkFinalPlace(place)) {
                FinalPlaces.getItems().add(place);
            }
        }
        for (Transition transition : petriNet.getTransitions()) {
            if (petriNet.checkFinalTransiitiom(transition)) {
                FinalPlaces.getItems().add(transition);
            }
        }
    }

    //crea arco
    public void createArc(ActionEvent actionEvent) {
        petriNet.addArc(arcSourcesField.getSelectionModel().getSelectedItem(), arcTargetField.getSelectionModel().getSelectedItem());
        simulate();
    }

    //Salva bozza
    @FXML
    public void SaveDraft() {
		boolean success = netRepo.saveNet(petriNet);
		if (success) {
			showDraftSuccess();
		}
		if (!success)
			showDraftError();
    }

    @FXML
    public void saveAndPublish() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Name");
        ButtonType confirmButtonType = new ButtonType("Confirm and Publish", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/resources/css/styles.css").toExternalForm());
        TextField nameField = new TextField();
        nameField.setText(petriNet.getName());

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("New name:"), nameField);
        dialog.getDialogPane().setContent(content);

        Platform.runLater(nameField::requestFocus);

        // Converte il risultato quando premuto Confirm
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return nameField.getText();
            }
            return null;
        });

        // Mostra e attende il risultato
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            petriNet.setName(result.get());
            try {
                if (petriNet.checkPetriNet()) {
                    petriNet.setPublished(true);
                    boolean success = netRepo.saveNet(petriNet);
                    if (success) {
                        finalPlacesLabel.getStyleClass().removeAll("alert-danger", "alert-success");
                        finalPlacesLabel.getStyleClass().add("alert-success");
                        finalPlacesLabel.setStyle("-fx-font-size: 10");
                        finalPlacesLabel.setText("Petri Net has been published successfully");
                        finalPlacesLabel.setVisible(true);
                        ViewNavigator.navigateToMyNets();
                    }
                    if (!success)
                        showError("Error Saving Net");
                }
            } catch (NodeNotFoundException e) {
                showError("initial place missing");
                finalPlacesLabel.getStyleClass().removeAll("alert-danger", "alert-success");
                finalPlacesLabel.getStyleClass().add("alert-danger");
                finalPlacesLabel.setText("initial place missing");
                finalPlacesLabel.setVisible(true);
            } catch (OrphanTransitionException e) {
                finalTransitionsLabel.setText("Orphan Transitions are not allowed");
                finalTransitionsLabel.setVisible(true);
            } catch (MoreThanOneFInalPlaceException e) {
                finalPlacesLabel.getStyleClass().add("alert-danger");
                if (FinalPlaces.getItems().size() == 0) {
                    finalPlacesLabel.setText("Final Place Is Missing!");
                } else {
                    finalPlacesLabel.setText("Only one Final Place is allowed");
                }
                finalPlacesLabel.setVisible(true);
            } catch (Exception e) {
                showError("Error saving Draft Petri Net");
            }
        }
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/resources/css/styles.css").toExternalForm());
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showDraftSuccess() {
        confirmDraftLabel.setVisible(true);
    }

    private void showDraftError() {
        errorDraftLabel.setVisible(true);
    }
}