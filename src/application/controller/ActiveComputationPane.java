package application.controller;

import application.model.PetriNet;
import application.model.Transition;
import application.model.UserType;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.awt.*;
import java.util.Optional;

import static javafx.scene.paint.Color.*;

public class ActiveComputationPane extends AbstractInteractivePane {

    private static final javafx.scene.paint.Color placeColor = BLUE;
    private static final javafx.scene.paint.Color adminTransitionColor = DARKRED;
    public static final Color userTransitionColor = GREEN;
    public static StackPane place;
    public static StackPane adminTransition;
    public static StackPane userTransition;
    private static Rectangle rectangle;


    @Override
    public StackPane makePlace(Pane pane, PetriNet net, int id, double x, double y) {
        Circle circle = new Circle(40, placeColor);

        Label placeName = new Label(net.getNode(id).getName());
        placeName.setMaxWidth(70);
        placeName.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");

        place.setLayoutX(x);
        place.setLayoutY(y);

        return place;
    }

    @Override
    public StackPane makeAdminTransition(Pane pane, PetriNet net, int id, double x, double y) {
        javafx.scene.shape.Rectangle rectangle = new Rectangle(45, 55, adminTransitionColor);
        Label adminTransitionName= new Label(net.getNode(id).getName());
        adminTransitionName.setMaxWidth(70);
        adminTransitionName.setMouseTransparent(true);
        adminTransitionName.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;  -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");

        StackPane adminTransition = new StackPane();

        adminTransition.setLayoutX(x);
        adminTransition.setLayoutY(y);

        adminTransition.getChildren().addAll(rectangle, adminTransitionName);
        return adminTransition;
    }
    @Override
    public StackPane makeUserTransition(Pane pane, PetriNet net, int id, double x, double y) {
        return null;
    }
    @Override
    public Group createArrow(Line line) {
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

            javafx.scene.shape.Polygon arrowHead = new Polygon();
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

    public static void markPlace(int Marking) {
        Label tokenNumber = new Label();
        tokenNumber.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
        tokenNumber.setText(Marking + "");
        place.getChildren().add(tokenNumber);
    }

    public static void makeTransitionFireable() {
            addGraphicMethods(adminTransition);
            rectangle.setStroke(DARKRED);
            rectangle.setStrokeWidth(5);
            adminTransition.setOnMousePressed(event -> {
       //         computation.fireTransition((Transition) net.getNode(id));
       //         computationStepRepository.saveComputationStep(computation.getLastStep());
        //        net.ObsNodesNumber.add(1);
            });
        }
   private static void addGraphicMethods(StackPane node) {
            node.setOnMouseClicked(event -> {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), rectangle);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    });
        node.setOnMouseEntered(event -> {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), rectangle);
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
}
