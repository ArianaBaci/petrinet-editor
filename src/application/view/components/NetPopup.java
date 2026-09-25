package application.view.components;

import javafx.stage.Popup;
import application.Main;
import application.model.PetriNet;
import application.view.ViewConstants;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class NetPopup extends Popup {

	private VBox mainBox;
	private HBox topBox;
	private Button closeButton;
	private HBox contentBox;
	private VBox buttonBox;
	private VBox textBox;
	private Label netName;
	private Label adminName;
	private Label dateLabel;
	private Label topText;
	private ImageView netPreview;
	private Button interactButton;
	private final static int SPACING = 10;
	
	public NetPopup(PetriNet net, Image netImage) {
		mainBox = new VBox();
		mainBox.setSpacing(SPACING);
		mainBox.setPadding(new Insets(SPACING*2));
    	mainBox.setStyle("-fx-background-color: "+ViewConstants.POPUP_BG_COLOR+"; "
    			+ "-fx-background-radius: "+ViewConstants.CORNER_RADIUS+"; "
    			+ "-fx-border-width: 2;"
    			+ " -fx-border-color: "+ViewConstants.POPUP_BORDER_COLOR+"; "
    			+ "-fx-border-radius: "+ViewConstants.CORNER_RADIUS+"; ");
		
		//close button layer
		topBox = new HBox();
		topBox.setAlignment(Pos.BASELINE_RIGHT);
		topBox.setPadding(new Insets(0, 0, SPACING, SPACING));
		closeButton = new Button();
		closeButton.setText("✕");
        closeButton.setOnAction(e -> {
        	this.hide();
        });
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);
        topText = new Label("");
		topBox.getChildren().addAll(topText, hSpacer, closeButton);
		mainBox.getChildren().add(topBox);
		
		
		contentBox = new HBox();
		contentBox.setSpacing(SPACING*2);
		textBox = new VBox();
		textBox.setSpacing(SPACING*2);
		textBox.setPadding(new Insets(SPACING));
		netName = new Label(net.getName());
		netName.setStyle("-fx-font-size: 24px;");
		adminName = new Label("administrator: "+net.getAdminName());
		adminName.setStyle("-fx-font-size: 18px;");
		dateLabel = new Label("created: "+ net.getDateCreated().toString());
		dateLabel.setStyle("-fx-font-size: 18px;");
		
		
		Region vSpacer = new Region();
		VBox.setVgrow(vSpacer, Priority.ALWAYS);
		
		interactButton = new Button("Button");
		buttonBox = new VBox();
		buttonBox.setSpacing(SPACING);
		addButton(interactButton);
		textBox.getChildren().addAll(netName, adminName, dateLabel, vSpacer, buttonBox);
		netPreview = new ImageView(netImage);
		netPreview.setFitWidth(300); // Set your desired width
        netPreview.setFitHeight(300); // Set your desired height
        netPreview.setPreserveRatio(true); // Maintain aspect ratio
        netPreview.setSmooth(false);
		contentBox.getChildren().addAll(textBox, netPreview);
		
		
		mainBox.getChildren().add(contentBox);
		this.getContent().add(mainBox);
		
		this.setAutoHide(true);
		this.show(Main.getPrimaryStage());
	}
	
	public Button getButton() {
		return interactButton;
	}
	
	public void setTopText(String txt) {
		topText.setText(txt);
	}
	
	public HBox getContentBox() {
		return contentBox;
	}

	public VBox getTextBox() {
		return textBox;
	}
	public void addButton(Button btn) {
		HBox buttonHbox = new HBox();
		buttonHbox.setAlignment(Pos.CENTER);
		buttonHbox.getChildren().add(btn);
		buttonBox.getChildren().add(buttonHbox);
	}
}
