package application.controller;


import application.Main;
import application.view.ViewNavigator;
import application.model.UserRepository;
import application.model.NetRepository;
import application.model.ComputationRepository;
import application.model.Computation;
import application.model.Transition;
import application.model.CompTransition;
import application.model.PetriNet;
import application.model.DotRenderer;
import application.model.DotLang;
import application.view.components.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class HomeController {
	@FXML
	private Label userNameLabel;
	@FXML
	private Label message;

	@FXML
	private ListView<String> netList;

	@FXML
	private Button detailsButton;

	@FXML
	private Label netInfo;

	@FXML
	private ImageView netPreview;

	@FXML
	private NetBrowser content;
	@FXML
	private BorderPane mainContainer;

	private Image netImage;

	private UserRepository userRepository;

	private NetRepository netRepository;

	private ComputationRepository computationRepo;

	private static int typeView;

	@FXML
	private Label typeViewLabel;
	@FXML
	private Button AdminView;
	@FXML
	private Button UserView;

	private static Computation comp;


	public void initialize() {
		userRepository = Main.getUserRepository();
		netRepository = Main.getNetRepository();
		computationRepo = Main.getComputationRepository();
		typeView = 1;
		typeViewLabel.setText("                                                                     Subscriptions to Your Nets");
		loadView(typeView);

	}
		public void loadView(int type) {
			List<NetMiniature> minis = new ArrayList<NetMiniature>();

			//distingue admin e userview

			for (Computation c : computationRepo.getComputations()) {
				if (type == 1) {
					typeViewLabel.setText("                                                                     Subscriptions to Your Nets");
					if (c.getPetriNet().getAdminName().equals(userRepository.getLoggedUser().getName())) {
						NetMiniature nm = new NetMiniature(c.getPetriNet(), netRepository.getImages().get(c.getPetriNet()));
						minis.add(nm);
						comp=c;
					}
				} else if (type == 2) {
					typeViewLabel.setText("                                                                      Your Subscriptions");
					if (c.getUser().equals(userRepository.getLoggedUser().getName())) {
						NetMiniature nm = new NetMiniature(c.getPetriNet(), netRepository.getImages().get(c.getPetriNet()));
						minis.add(nm);
						comp=c;
					}
				}
			}

			content = new NetBrowser(minis);
			if (minis.size() > 0) {

				for (Node nd : content.getGrid().getChildren()) {
					if (nd instanceof NetMiniature) {
						NetMiniature nm = (NetMiniature) nd;
						nm.getAdminLabel().setText("User: " + comp.getUser());
						setInteraction(nm);
					}
				}

			} else {
				Button btn = new Button();
				if(type==1) {
					btn.setText("Create a new Petrinet");
					btn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
						PetriNetEditorController.petriNet = (new PetriNet(userRepository.getLoggedUser().getName()));
						ViewNavigator.navigateToPetriNetEditor();
					});
				}else if(type==2) {
					btn.setText("Explore Available Nets");
					btn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
						PetriNetEditorController.petriNet = (new PetriNet(userRepository.getLoggedUser().getName()));
						ViewNavigator.navigateToExplore();
					});
				}
				content.getBox().getChildren().add(btn);
			}

			mainContainer.setCenter(content);
		}

		private void setInteraction (NetMiniature nm){
			PetriNet n = nm.getNet();
			nm.setCursor(Cursor.HAND);
			nm.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				try {
					createNetBox(n);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			});
		}

		private void createNetBox (PetriNet n) throws IOException {
			NetSubscriptionController.net = n;
			NetSubscriptionController.netRepo = netRepository;
			NetPopUpController.net = n;
			NetPopUpController.netRepo = netRepository;
			if (typeView == 1) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/NetPopUpView.fxml"));
					Parent root = loader.load();
					Stage popupStage = new Stage();
					popupStage.setTitle("Manage Your Net");
					popupStage.setScene(new Scene(root));
					popupStage.initModality(Modality.APPLICATION_MODAL);
					root.getStylesheets().add(getClass().getResource("/resources/css/styles.css").toExternalForm());
					popupStage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (typeView == 2) {
				try {
					for (Computation c : computationRepo.getComputations()) {
						if (c.getUser().equals(userRepository.getLoggedUser().getName()) && c.getPetriNet().equals(n)) {
							n.ObsNodesNumber.addListener((ListChangeListener<Integer>) p -> {
								loadView(2);
							});
							NetSubscriptionController.net = n;
							NetSubscriptionController.computation=c;
						}
						}
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/YourSubscriptionPopUp.fxml"));
						Parent root = loader.load();
						Stage popupStage = new Stage();
						popupStage.setTitle("Manage Subscription");
						popupStage.setScene(new Scene(root));
						popupStage.initModality(Modality.APPLICATION_MODAL);
						root.getStylesheets().add(getClass().getResource("/resources/css/styles.css").toExternalForm());
						popupStage.show();
					loadView(2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				loadView(2);
			}
		}
		@FXML
		private void showAdminView() {
		typeView=1;
		loadView(typeView);
		}
		@FXML
		private void showUserView() {
		typeView=2;
		loadView(typeView);
		}
	}
