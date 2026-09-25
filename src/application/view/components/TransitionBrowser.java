package application.view.components;

import java.util.List;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class TransitionBrowser extends ScrollPane {

	private List<TransitionMiniature> minis;
	private Label label;
	private VBox content;
	
	public TransitionBrowser() {
		super();
	}

	public TransitionBrowser(List<TransitionMiniature> minis) {
		this.minis = minis;
		this.setFitToWidth(true); // Makes the ScrollPane fit the width of the content
		this.setFitToHeight(true); // Makes the ScrollPane fit the height of the content       
		content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(20);
        content.setPadding(new Insets(20, 160, 20, 160));
		label = new Label();
		label.setStyle("-fx-font-size: 18px;");
		label.setPadding(new Insets(20, 0, 5, 0));
		label.setText("Showing transitions: ");
		content.getChildren().add(label);
		for (TransitionMiniature tm: minis) {
			content.getChildren().add(tm);
		}
		this.setContent(content);
	}
	
	public void setText(String txt) {
		label.setText(txt);
	}

	public VBox getBox() {
		return content;
	}

	public List<TransitionMiniature> getMiniatures() {
		return minis;
	}
}
