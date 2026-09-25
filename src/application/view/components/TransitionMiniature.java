package application.view.components;

import application.model.Transition;
import application.model.Computation;
import application.model.UserType;
import application.view.ViewConstants;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;


public class TransitionMiniature extends HBox {

	private static final int HEIGHT = 120;
	private static final int SPACING = 10;
	private ImageView netPreview;
	private VBox imgColumn;
	private VBox column1;
    private Label transitionName;
    private Label transitionType;
	private VBox column2;
    private Label netName;
    private Label adminName;
    private Transition transition;
    private Computation computation;
    
    public TransitionMiniature (Transition t, Computation comp, Image netImage) {

    	this.transition = t;
    	this.computation = comp;
    	this.setStyle("-fx-background-color: "+ViewConstants.MINIATURE_BG_COLOR+"; "
    			+ "-fx-background-radius: "+ViewConstants.CORNER_RADIUS+"; "
    			+ "-fx-border-width: 2;"
    			+ " -fx-border-color: "+ViewConstants.MINIATURE_BORDER_COLOR+"; "
    			+ "-fx-border-radius: "+ViewConstants.CORNER_RADIUS+"; "
    			+ "-fx-font-size: 16px;");
    	this.setPrefHeight(HEIGHT);
    	this.setSpacing(SPACING*8);
    	this.setPadding(new Insets(SPACING*1.3));   	
    	this.setPrefHeight(HEIGHT);

    	netPreview = new ImageView();
		netPreview.setImage(netImage);
        netPreview.setFitHeight(HEIGHT*0.8); // Set your desired height
        netPreview.setPreserveRatio(true); // Maintain aspect ratio
        netPreview.setSmooth(true);
        netPreview.setFitWidth(HEIGHT * 1.5);
        imgColumn = new VBox();
        imgColumn.setPrefWidth(HEIGHT*1.5);
        imgColumn.setFillWidth(false);
        imgColumn.setMinHeight(HEIGHT);
        imgColumn.setMaxWidth(HEIGHT*1.5);
		imgColumn.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8px;");
        imgColumn.setAlignment(Pos.CENTER);
        imgColumn.getChildren().add(netPreview);
        
        this.getChildren().add(imgColumn);
        
        column1 = new VBox();
        column1.setSpacing(SPACING*2);
        column1.setStyle("-fx-font-weight: bold;");
        
        transitionName = new Label("Transition: "+t.getName());
        if (t.getUserType().equals(UserType.ADMIN)) {
        	transitionType = new Label("Type: ADMIN");
        	transitionType.setStyle("-fx-text-fill: "+ViewConstants.TEXT_ADMIN_COLOR+"; ");
        } else {
        	transitionType = new Label("Type: USER");
        	transitionType.setStyle("-fx-text-fill: "+ViewConstants.TEXT_USER_COLOR+";");
        }
        
        column1.getChildren().addAll(transitionName, transitionType);
        this.getChildren().add(column1);
        
        column2 = new VBox();
        column2.setSpacing(SPACING*2);
        netName = new Label ("Petrinet: "+comp.getPetriNet().getName());
        adminName = new Label("Administrator: "+comp.getPetriNet().getAdminName());
        column2.getChildren().addAll(netName, adminName);
        this.getChildren().add(column2);
    }
    
    public Transition getTransition() {
    	return transition;
    }
    
    public Computation getComputation() {
    	return computation;
    }
}
