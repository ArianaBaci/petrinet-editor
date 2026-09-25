package application.view.components;

import application.model.PetriNet;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;

public class NetGrid extends GridPane {

	private static final int DEFAULT_COLS = 3;
	private static final int DEFAULT_SPACING = 20;

    public NetGrid(int cols, List<NetMiniature> minis) {
    	setDefaults();
    	int x = 0;
    	int y = 0;
    	for (NetMiniature nm : minis) {
    		this.add(nm, x, y);
    		x++;
    		if (x>cols - 1) {
    			x = 0;
    			y++;
    		}
    	}
    	
    }

    
    public NetGrid(List<NetMiniature> minis) {
    	this(DEFAULT_COLS, minis);
    }
    
    private void setDefaults() {
    	this.setHgap(DEFAULT_SPACING);
    	this.setVgap(DEFAULT_SPACING);
    	this.setPadding(new Insets(DEFAULT_SPACING));
    	this.setAlignment(Pos.CENTER);
    }
}