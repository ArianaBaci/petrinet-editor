package application.view.components;

import application.Main;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class MessageBox {

    public MessageBox(String message) {


        TilePane r = new TilePane();




        // Mostra popup sul primary stage
        Stage stage = Main.getPrimaryStage();
        Scene sc = new Scene(r, 200, 200);
        stage.setScene(sc);

        stage.show();
    }
}
