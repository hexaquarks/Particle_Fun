
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicButtonListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AppFrame extends Application {
	static int frame_width = 800;
	static int frame_height = 600;
	static JPanel panelSouth;
	static JPanel sidePanel;
	static JPanel mainPanel;
	static JPanel panelParticleNumber;
	static JPanel panelForcesWest;
	static JPanel panelForcesEast;
	static JPanel panelForcesContainer;
	static ArrayList<JButton> shapeButtons;
	static ArrayList<JLabel> labels;
	static JLabel particleNumber, totalElectricEnergy, totalPotential, totalCollisions;

	public static void main(String[] args) {
		launch(args);

	}

	public static SampleController controller;

	@Override
	public void start(Stage stage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ParticleFunJavaFx.fxml"));
			Parent root = loader.load();

			controller = (SampleController) loader.getController();

			Scene scene = new Scene(root, 906, 636);

			BorderPane scenePanel = (BorderPane) scene.lookup("#MainPanel");

			SwingNode swingNode = new SwingNode();
			MainPanel mainPanel = new MainPanel();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					swingNode.setContent(mainPanel);
					mainPanel.setSize(691, 452);
					mainPanel.setBackground(Color.black);
					mainPanel.physicsTimer.start();
					mainPanel.fpsTimer.start();

					ShapeManager shapee = new ShapeManager(
							new Point2D(mainPanel.getSize().getWidth() / 2, mainPanel.getSize().getHeight() / 2));
					mainPanel.shape = shapee;
					mainPanel.initializeParticles(15, 100, 5);
				}
			});

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					scenePanel.setCenter(swingNode);
					controller.initializeButtons(mainPanel, scene);
				}
			});

			// System.out.println("width is : " + mainPanel.widthProperty().getValue());
			// System.out.println("width is : " + mainPanel.widthProperty());

			stage.setTitle("FXML Welcome");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}