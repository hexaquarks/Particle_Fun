
import java.awt.Color;
import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AppFrame extends Application {

	
	/** 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Instances
	 */
	public static SampleController controller;

	
	/** 
	 * @param stage
	 * @throws Exception
	 */
	@Override
	public void start(Stage stage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ParticleFunJavaFx.fxml"));
			Parent root = loader.load();

			controller = (SampleController) loader.getController();

			Scene scene = new Scene(root, 906, 636);

			BorderPane scenePanel = (BorderPane) scene.lookup("#MainPanel");

			SwingNode swingNode = new SwingNode();
			MainPanel mainPanel = new MainPanel(controller);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mainPanel.setSize(691, 452);
					mainPanel.setBackground(Color.black);
					mainPanel.physicsTimer.start();
					mainPanel.fpsTimer.start();
					mainPanel.initializeParticles(15, 100, 5);
				}
			});

			scenePanel.setCenter(swingNode);
			swingNode.setContent(mainPanel);
			controller.initializeButtons(mainPanel);

			ShapeManager shapee = new ShapeManager(
					new Point2D(mainPanel.getSize().getWidth() / 2, mainPanel.getSize().getHeight() / 2));
			System.out.println(mainPanel.getSize().getWidth() / 2 + " , " + mainPanel.getSize().getHeight() / 2);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mainPanel.shape = shapee;
				}
			});

			stage.setTitle("Particle Geometrical Simulation");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}