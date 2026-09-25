package application.view.components;

import application.Main;
import application.model.DotLang;
import application.model.DotRenderer;
import application.model.PetriNet;
import javafx.stage.Popup;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import javax.swing.*;

public class NetDotImage extends Popup {

    private VBox mainBox;
    private HBox topBox;
    private Button closeButton;
    private HBox contentBox;
    private VBox textBox;
    private Label netName;
    private Label adminName;
    private Label dateLabel;
    private ImageView netPreview;
    private Button subscribeButton;
    private final static int SPACING = 20;

    public NetDotImage(PetriNet net) {
        mainBox = new VBox();
        mainBox.setSpacing(SPACING);
        mainBox.setPadding(new Insets(SPACING));
        mainBox.setStyle("-fx-background-color: #cce4ff; -fx-background-radius: 8px; -fx-border-width: 2;"
                + " -fx-border-color: #99c9ff; -fx-border-radius: 8px; ");

        //close button layer
        topBox = new HBox();
        topBox.setAlignment(Pos.BASELINE_RIGHT);
        closeButton = new Button();
        closeButton.setText("✕");
        closeButton.setOnAction(e -> {
            this.hide();
        });

        topBox.getChildren().addAll(closeButton);
        mainBox.getChildren().add(topBox);

        contentBox = new HBox();
        contentBox.setSpacing(SPACING);
        contentBox.setPadding(new Insets(SPACING));


        try {
            netPreview = new ImageView(DotRenderer.renderDotToImage(DotLang.translate(net)));
        } catch (Exception e) {
            System.out.println("error rendering net preview");
        }
        netPreview.setFitWidth(450); // Set your desired width
        netPreview.setFitHeight(450); // Set your desired height
        netPreview.setPreserveRatio(true); // Maintain aspect ratio
        netPreview.setSmooth(false);
        contentBox.getChildren().addAll(netPreview);

        mainBox.getChildren().add(contentBox);
        this.getContent().add(mainBox);

        this.setAutoHide(true);
        this.show(Main.getPrimaryStage());
    }
}
