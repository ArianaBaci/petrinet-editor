package application.view.components;

import application.model.PetriNet;
import application.view.ViewConstants;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NetMiniature extends VBox {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 200;
	private static final int INNER_SPACING = 10;
	private ImageView netPreview;
	private HBox imgBox;
    private Label netName;
    public static  Label adminName;
    private PetriNet petriNet;

    public NetMiniature(PetriNet net, Image netImage) {

    	this.setStyle("-fx-background-color: "+ViewConstants.MINIATURE_BG_COLOR+"; "
    			+ "-fx-background-radius: "+ViewConstants.CORNER_RADIUS+"; "
    			+ "-fx-border-width: 2;"
    			+ " -fx-border-color: "+ViewConstants.MINIATURE_BORDER_COLOR+"; "
    			+ "-fx-border-radius: "+ViewConstants.CORNER_RADIUS+"; ");
    	this.petriNet = net;
    	this.netPreview = new ImageView();
    	this.setPrefSize(WIDTH, HEIGHT);
    	this.setSpacing(INNER_SPACING);
    	this.setPadding(new Insets(WIDTH*0.05));
    	
		netPreview.setImage(netImage);
		
        netPreview.setFitWidth(WIDTH*0.8); 
        netPreview.setFitHeight(HEIGHT*0.66);
        netPreview.setPreserveRatio(true);
        netPreview.setSmooth(true);
        
        imgBox = new HBox();
		imgBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8px;");
		imgBox.setAlignment(Pos.CENTER);
		imgBox.setMinHeight(HEIGHT*0.66);
        imgBox.getChildren().add(netPreview);
		
		netName = new Label();
		netName.setText(net.getName());
		adminName = new Label();
		adminName.setText(net.getAdminName());

		this.getChildren().addAll(imgBox, netName, adminName);
		this.setAlignment(Pos.CENTER);
    }
    
    public PetriNet getNet() {
    	return petriNet;
    }
    
    public ImageView getNetPreview() {
    	return netPreview;
    }

    public HBox getImgBox() {
    	return imgBox;
    }

    public Label getNameLabel() {
    	return netName;
    }
    
    public Label getAdminLabel() {
    	return adminName;
    }
}