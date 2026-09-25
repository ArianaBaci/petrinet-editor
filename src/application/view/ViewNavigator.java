package application.view;

import application.Main;
import application.controller.MainController;
import application.controller.PetriNetEditorController;
import application.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

/**
 * This class handles navigation between different views in the application.
 * It works as a bridge between controllers and views, allowing for simplified navigation.
 */
public class ViewNavigator {

    // Reference to the main controller
    private static MainController mainController;
    public static void setMainController(MainController controller) {
        mainController = controller;
    }

    /**
     * METODO PER CARICARE L'FXML
     */

    public static void loadView(String fxml) {
        try {
            URL fxmlUrl = Main.class.getResource(Main.pathPrefix + "/fxml/" + fxml);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Node view = loader.load();
            mainController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxml);
        }
    }

    public static void navigateToPetriNetEditor() {
                 loadView("PetriNetEditorView.fxml");
        }

    public static void navigateToLogin() {
        System.out.println("//DEBUGLOGIN 1");
        URL url = ViewNavigator.class.getResource("/resources/fxml/LoginView.fxml");
        System.out.println("FXML URL: " + url);
        try {
            loadView("LoginView.fxml");
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
        System.out.println("//DEBUGLOGIN 1");
        }

    public static void navigateToHome() {
                 loadView("HomeView.fxml");
        }

    public static void navigateToExplore() {
                 loadView("ExploreView.fxml");
        }

    public static void navigateToMyNets() {
                 loadView("MyNetsView.fxml");
        }

	public static void navigateToMyComputations() {
	loadView("MyComputationsView.fxml");
	}

	public static void navigateToInbox() {
	loadView("InboxView.fxml");
	}

    public static void navigateToCompletedComputations() {
        loadView("CompletedComputationsView.fxml");
    }

    public static void navigateToMyDraftNets() {
        loadView("MyDraftNetsView.fxml");
    }
    public static void navigateToPublishedNetPopUp() {
        loadView("MyDraftNetsView.fxml");
    }
}
