package application.view.components;

import java.util.List;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class NetBrowser extends ScrollPane {

	private NetGrid grid;
	private Label label;
	private VBox content;
	
	public NetBrowser() {
		super();
	}

	public NetBrowser(List<NetMiniature> minis) {
		this.setFitToWidth(true); // Makes the ScrollPane fit the width of the content
		this.setFitToHeight(true); // Makes the ScrollPane fit the height of the content       
		content = new VBox();
        content.setAlignment(Pos.CENTER);
		label = new Label();
		label.setStyle("-fx-font-size: 18px;");
		label.setPadding(new Insets(20, 0, 5, 0));
		content.getChildren().add(label);
		grid = new NetGrid(minis);
		grid.setPrefWidth(Region.USE_COMPUTED_SIZE); // or a specific width
		grid.setPrefHeight(Region.USE_COMPUTED_SIZE); // or a specific height
		content.getChildren().add(grid);
		this.setContent(content);
	}
	
	public void setText(String txt) {
		label.setText(txt);
	}

	public VBox getBox() {
		return content;
	}

	public NetGrid getGrid() {
		return grid;
	}
}
