package application.controller;

import application.model.*;
import application.model.Arc;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.Optional;

import static javafx.scene.paint.Color.*;

public class InteractiveNodes {
    private static final Color placeColor = BLUE;
    private static final Color adminTransitionColor = DARKRED;
    public static final Color userTransitionColor = GREEN;

    public static StackPane makePlace(int isMarked, int whichView, Pane pane, PetriNet net, int id, double x, double y) {

        Circle circle = new Circle(40, placeColor);
        Label placeName = new Label(net.getNode(id).getName());
        Label tokenNumber = new Label();
        placeName.setMouseTransparent(true);
        placeName.setMaxWidth(70);
        placeName.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
        tokenNumber.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
        StackPane place = new StackPane();

        //Se sono nella View per le computations marco eventualmente il place col token
        if(whichView==2){
            markPlace(isMarked,circle,place,placeName,tokenNumber, net);
        }

        //Se sono nella View per il controller aggiungo il menu al tasto destro

        if(whichView == 1) {
            place.getChildren().addAll(circle, placeName);
            ContextMenu contextMenu = new ContextMenu();
            int transitionCount = net.getTransitions().size() + 1;
            MenuItem rename = new MenuItem("Rename Node");
            MenuItem addUserTransitionItem = new MenuItem("Add Target User Transition");
            MenuItem addAdminTransitionItem = new MenuItem("Add Admin Transition");
            MenuItem deleteItem = new MenuItem("Delete Place");
            contextMenu.getItems().addAll(rename, addUserTransitionItem, addAdminTransitionItem, deleteItem);


            rename.setOnAction(event -> {
                TextInputDialog dialog = new TextInputDialog(net.getNode(id).getName());
                dialog.setTitle("Rename Node");
                dialog.setHeaderText("Edit name");
                dialog.setContentText("New Name:");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(newName -> {
                    // Rimuove tutti i caratteri non ammessi
                    newName = newName.replaceAll("[^a-zA-Z0-9]", "");

                    // Limita la lunghezza a 10 caratteri
                    if (newName.length() > 10) {
                        newName = newName.substring(0, 10);
                    }

                    // Controlla che il risultato non sia vuoto
                    if (newName.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Name");
                        alert.setHeaderText(null);
                        alert.setContentText("Name must contain at least one valid character (letters or numbers).");
                        alert.showAndWait();
                    } else {
                    	safeRename(net, id, newName);
                    }
                });
            });

            // Crea Transitions

            addUserTransitionItem.setOnAction(e -> {
                net.addTransition("Tr" + transitionCount + 1, net.getNode(id).getName(), UserType.ENDUSER);
                net.ObsNodesNumber.add(1);
            });
            addAdminTransitionItem.setOnAction(e -> {
                net.addTransition("Tr" + transitionCount + 1, net.getNode(id).getName(), UserType.ADMIN);
                net.ObsNodesNumber.add(1);
            });

            //mostra opzione elimina solo se il nodo è eliminabile

            if (net.isRemovable(net.getNode(id))) {
                deleteItem.setOnAction(e -> {
                    net.removeNode(id);
                    net.ObsNodesNumber.add(1);
                });
            } else {
                deleteItem.setVisible(false);
            }

            //Mostra il menu al click destro

            circle.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown()) {
                    contextMenu.show(circle, event.getScreenX(), event.getScreenY());
                } else {
                    contextMenu.hide();
                }
            });

        // aggiungi rimbalzino e cose inutili carine
            addGraphicMethods(place, circle);
        }

        //Imposto le coordinate passate come parametro dai controller, calcolate da Dot ed estratte da DotLayoutExtractor
        place.setLayoutX(x);
        place.setLayoutY(y);

        return place;
    }

    public static StackPane makeAdminTransition(ComputationRepository computationRepository, ComputationStepRepository computationStepRepository, Computation computation, boolean isFireable, int whichView,Pane pane, PetriNet net, int id, double x, double y) {
        Rectangle rectangle = new Rectangle(45, 55, adminTransitionColor);
        Label adminTransitionName= new Label(net.getNode(id).getName());
        adminTransitionName.setMaxWidth(70);
        adminTransitionName.setMouseTransparent(true);
        adminTransitionName.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;  -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");

        StackPane adminTransition = new StackPane();

        adminTransition.setLayoutX(x);
        adminTransition.setLayoutY(y);

        //se whichView==1 è per il controller, quindi aggiungo menù al click dx,

        if(whichView == 1) {
            ContextMenu contextMenu = makeTransitionMenu(adminTransitionName, net, id);

            adminTransition.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown()) {
                    contextMenu.show(rectangle, event.getScreenX(), event.getScreenY());
                } else {
                    contextMenu.hide();
                }
            });

            addGraphicMethods(adminTransition, rectangle);
         //  setAsArcSource(2, adminTransition, id, net, pane);
         //   setAsArcTarget(2, adminTransitionName, net, adminTransition, pane, id);

        }

        //se è una delle available transitions metre sono nella view computation, aggiungo i metodi grafici e il bordo per identificarla e
        //sw ci premo fa il fire

        if(isFireable) {
            addGraphicMethods(adminTransition, rectangle);
                rectangle.setStroke(DARKRED);
                rectangle.setStrokeWidth(5);
                adminTransition.setOnMousePressed(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm firing transition");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to fire Transition " +  net.getNode(id).getName() + "?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK)
                        computation.fireTransition((Transition) net.getNode(id));
                    //computationStepRepository.saveComputationStep(computation.getLastStep());
                    computationRepository.saveComputation(computation,computationStepRepository);
                    net.ObsNodesNumber.add(1);
                });
            }
        adminTransition.getChildren().addAll(rectangle, adminTransitionName);
        return adminTransition;
    }

    public static StackPane makeUserTransition(ComputationStepRepository computationStepRepository, Computation computation, boolean isFireable,int whichView,Pane pane, PetriNet net, int id, double x, double y) {
        //commenti analoghi a makeAdminTransition

        Rectangle rectangle = new Rectangle(45, 55, userTransitionColor);
        Label userTransitionName = new Label(net.getNode(id).getName());
        userTransitionName.setMaxWidth(70);
        userTransitionName.setMouseTransparent(true);
        userTransitionName.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");

        StackPane userTransition = new StackPane();

        userTransition.getChildren().addAll(rectangle, userTransitionName);

        userTransition.setLayoutX(x);
        userTransition.setLayoutY(y);

        //se è per il controller aggiungo menù e metodi grafini

        if(whichView == 1) {
            addGraphicMethods(userTransition, rectangle);
            ContextMenu contextMenu = makeTransitionMenu(userTransitionName, net, id);
            userTransition.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown()) {
                    contextMenu.show(rectangle, event.getScreenX(), event.getScreenY());
                } else {
                    contextMenu.hide();
                }
            });
        }

        //se è fireable associo metodo relativo per fireare la transition al click, + accortezze grafiche per identificarlo

        if(isFireable) {
            addGraphicMethods(userTransition, rectangle);
            rectangle.setStroke(DARKGREEN);
            rectangle.setStrokeWidth(5);
            userTransition.setOnMousePressed(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm firing transition");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to fire Transition " +  net.getNode(id).getName() + "?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    computation.fireTransition((Transition) net.getNode(id));
                    computationStepRepository.saveComputationStep(computation.getLastStep());
                    net.ObsNodesNumber.add(1);
                } else {
                }
            });

        }
        return userTransition;
    }
    //metodi puramente grafici

    public static void addGraphicMethods(StackPane node, Shape shape) {
        node.setOnMouseClicked(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), shape);
            st.setToX(1.2);
            st.setToY(1.2);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        });
        node.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), shape);
            st.setToX(1.2);
            st.setToY(1.2);
            st.play();
            node.setOnMouseExited(e -> {
                st.setToX(1);
                st.setToY(1);
                st.play();
            });
        });
    }

    //menu al click dx per le transitions (quello per il place è in makeplace)

    public static ContextMenu makeTransitionMenu(Label transitionName, PetriNet net, int id) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename node");
        MenuItem addPlaceItem = new MenuItem("Add Target Place");
        MenuItem deleteItem = new MenuItem("Delete Transition");
        contextMenu.getItems().addAll(rename, addPlaceItem, deleteItem);

        rename.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(net.getNode(id).getName());
            dialog.setTitle("Rename Node");
            dialog.setHeaderText("Edit name");
            dialog.setContentText("New Name:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(newName -> {
                // Rimuove tutti i caratteri non ammessi
                newName = newName.replaceAll("[^a-zA-Z0-9]", "");

                // Limita la lunghezza a 10 caratteri
                if (newName.length() > 10) {
                    newName = newName.substring(0, 10);
                }

                // Controlla che il risultato non sia vuoto
                if (newName.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Name");
                    alert.setHeaderText(null);
                    alert.setContentText("Name must contain at least one valid character (letters or numbers).");
                    alert.showAndWait();
                } else {
					safeRename(net, id, newName);
                }
            });
        });

        addPlaceItem.setOnAction(e -> {
            String placeName = "Place" + net.getPlaces().size();
            net.addPlace(placeName, (Transition)net.getNode(id));
            net.ObsNodesNumber.add(1);
        });

        if (net.isRemovable(net.getNode(id))) {
            deleteItem.setOnAction(e -> {
                net.removeNode(id);
                net.ObsNodesNumber.add(1);
            });
        } else {
            deleteItem.setVisible(false);
        }
        return contextMenu;
    }

    //crea l'arco con linea e triangolo

    public static Group createArrow(Line line) {
        // Calcola l'angolo
        double ex = line.getEndX();
        double ey = line.getEndY() ;
        double sx = line.getStartX();
        double sy = line.getStartY();
        if (ex > sx) {
            ex = ex - (ex - sx) / 5.235;
        } else if (sx > ex) {
            ex = ex + (sx - ex) /5.235;
        }
        if (ey > sy) {
            ey = ey - (ey - sy) / 5.235;
        } else if (sy > ey) {
            ey = ey + (sy - ey) /5.235;
        }

        double arrowLength = 15; // lunghezza lato freccia
        double arrowWidth = 10;   // larghezza base freccia

        double dx = ex - sx;
        double dy = ey - sy;
        double angle = Math.atan2(dy, dx);

        double x1 = ex - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = ey - arrowLength * Math.sin(angle - Math.PI / 6);

        double x2 = ex - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = ey - arrowLength * Math.sin(angle + Math.PI / 6);

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                ex, ey,
                x1, y1,
                x2, y2
        );

        arrowHead.setFill(line.getStroke());

        Group group = new Group();
        group.getChildren().addAll(line, arrowHead);
        return group;
    }

    //Aggiunge se marcato il marking al place con relativo numero di token

    public static void markPlace(int isMarked, Circle circle, StackPane place, Label placeName, Label tokenNumber, PetriNet net) {
        if(isMarked == 0) {
            place.getChildren().addAll(circle, placeName);
        }
        else if(isMarked!=0){
            StackPane.setAlignment(placeName, Pos.TOP_CENTER);
            Circle circle2 = new Circle(15, BLACK);
            circle2.setStroke(LIGHTGRAY);
            tokenNumber.setText(isMarked+"");
            place.getChildren().addAll(circle, placeName, circle2, tokenNumber);
        }
    }
    
    
    private static void safeRename(PetriNet net, int nodeId, String newName ) {
		try {
			net.setNodeName(net.getNode(nodeId).getName(), newName);
			net.ObsNodesNumber.add(1);
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid Name");
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		} 	
    }
}
    