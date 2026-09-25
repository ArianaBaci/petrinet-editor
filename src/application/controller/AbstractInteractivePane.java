package application.controller;

import application.model.*;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;

public abstract class AbstractInteractivePane {
    public abstract StackPane makePlace(Pane pane, PetriNet net, int id, double x, double y);

    public abstract StackPane makeAdminTransition(Pane pane, PetriNet net, int id, double x, double y);

    public abstract StackPane makeUserTransition(Pane pane, PetriNet net, int id, double x, double y);

    public abstract Group createArrow(Line line);
}
