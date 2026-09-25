package application.model;

import javafx.scene.image.*;

public class NetImage {

	public static Image createImage(PetriNet net) {
		try {
			return DotRenderer.renderDotToImage(DotLang.translate(net));
		} catch (Exception e) {
            e.printStackTrace();
            return null;
		}
	}
}
