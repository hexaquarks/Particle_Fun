
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

public class GameFrame extends Application{
	static int frame_width = 800;
	static int frame_height = 600;
	static JPanel panelSouth;
	static JPanel sidePanel;
	static JPanel mainPanel;
	static JPanel panelParticleNumber;
    static JPanel panelForcesWest;
    static JPanel panelForcesEast;
    static JPanel panelForcesContainer;
	static ArrayList<JGradientButton> buttons;
	static ArrayList<JButton> shapeButtons;
	static ArrayList<JLabel> labels;
	static JLabel particleNumber, totalElectricEnergy, totalPotential, totalCollisions;
	
	public static class JGradientButton extends JButton{
		Color color;
		public JGradientButton(String text, Color color) {
			super(text);
			this.color = color;
			setContentAreaFilled(false);
            setFocusPainted(false); // used for demonstration
		}
		
		protected void paintComponent(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(
                    new Point(0, 0), 
                    Color.WHITE, 
                    new Point(0, getHeight()), 
                    color.darker()));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();

            super.paintComponent(g);
        }
		
	}
	
	static void setLabels(int pNumber, double[] stats) {
		particleNumber.setText(Integer.toString(pNumber));
		totalElectricEnergy.setText(Double.toString(stats[0]));
		totalPotential.setText(Double.toString(stats[1]));
		totalCollisions.setText(Double.toString(stats[2]));
	}
	static void initializePanels(GameFrame frame, GamePanel GamePanel){
		panelSouth = new JPanel();
		sidePanel = new JPanel();
		mainPanel = new JPanel();

		GamePanel.setBackground(Color.BLACK);
		mainPanel.setSize(new Dimension(frame_width, frame_height));
		
		mainPanel.setLayout(new BorderLayout(0, 0));
		mainPanel.add(sidePanel, BorderLayout.EAST);
		sidePanel.setLayout(new BoxLayout(sidePanel,BoxLayout.Y_AXIS));
		sidePanel.setSize(new Dimension(150, 100));
		sidePanel.setPreferredSize(new Dimension(150, 100));

		panelSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
		panelSouth.setPreferredSize(new Dimension(frame_width, 120));

		panelSouth.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));
		GamePanel.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));
		sidePanel.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));

		// Define the panel to hold the buttons 
        panelParticleNumber = new JPanel();
        panelForcesWest = new JPanel();
        panelForcesEast = new JPanel();
        panelForcesContainer = new JPanel();
         
        // Set up the title for different panels
        panelParticleNumber.setBorder(BorderFactory.createTitledBorder("Particle number"));
        panelForcesContainer.setBorder(BorderFactory.createTitledBorder("Forces"));

        // Set up the BoxLayout
        BoxLayout layout1 = new BoxLayout(panelParticleNumber, BoxLayout.Y_AXIS);
        BoxLayout layout2 = new BoxLayout(panelForcesWest, BoxLayout.Y_AXIS);
        BoxLayout layout3 = new BoxLayout(panelForcesEast, BoxLayout.Y_AXIS);
        
        panelForcesContainer.setLayout(new BorderLayout(15,0));
        panelForcesContainer.add(panelForcesWest, BorderLayout.WEST);
        panelForcesContainer.add(panelForcesEast, BorderLayout.EAST); 
        
        panelParticleNumber.setLayout(layout1);
        panelForcesWest.setLayout(layout2);
        panelForcesEast.setLayout(layout3);
	}

	static void initializeStatistics(){
		particleNumber = new JLabel();
		totalElectricEnergy = new JLabel();
		totalPotential = new JLabel();
		totalCollisions = new JLabel();

		labels = new ArrayList<JLabel>();
		Collections.addAll(labels, particleNumber,totalElectricEnergy,totalPotential,totalCollisions);
		
		particleNumber.setBorder(BorderFactory.createTitledBorder("Particle number"));
		totalElectricEnergy.setBorder(BorderFactory.createTitledBorder("Total electric energy"));
		totalPotential.setBorder(BorderFactory.createTitledBorder("Total potential energy"));
		totalCollisions.setBorder(BorderFactory.createTitledBorder("Total collisions"));

		for(JLabel label : labels) {
			label.setMaximumSize(new Dimension(sidePanel.getPreferredSize().width , 80));
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			sidePanel.add(label);
		}
	}

	static void initializeButtons(GamePanel gamePanel, Scene scene){
		System.out.println(scene);

		// ToggleGroup toggles = new ToggleGroup();
		// RadioButton circle = (RadioButton) scene.lookup("#circle"); 
		// System.out.println("corcle is : " + circle);
		// RadioButton square = (RadioButton) scene.lookup("#square"); square.setToggleGroup(toggles);
		// RadioButton diamond = (RadioButton) scene.lookup("#diamond"); diamond.setToggleGroup(toggles);

		// Button addParticle = (Button) scene.lookup("#addParticle");
		// Button removeParticle = (Button) scene.lookup("#removeParticle");

		CheckBox collisions = (CheckBox) scene.lookup("#collisions");
		System.out.println("SD " + collisions );
		// CheckBox gravity = (CheckBox) scene.lookup("#gravity");
		
		// CheckBox electrostatics = (CheckBox) scene.lookup("#electrostatics");

		// toggles.selectedToggleProperty().addListener(new ChangeListener<Toggle>() 
        // {
        //     public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n)
        //     {
        //         RadioButton rb = (RadioButton) toggles.getSelectedToggle();
        //         if (rb != null) {
		// 			System.out.println("IN");
		// 			if(rb.isSelected()){
		// 				rb.setSelected(false);
		// 			} else {
		// 				rb.setSelected(true);
		// 			}
		// 			// (rb.isSelected()) ? rb.setSelected(false) : rb.setSelected(true);
        //         }

        //     }
        // });

		

		// buttons = new ArrayList<JGradientButton>();
		// String[] name = {"Add Particle", "Remove Particle" , "Collisons", " Electric Force" , "Gravity"};
		// Color[] color = {Color.white, Color.red, Color.green, Color.green, Color.green}; 
		// for(int i = 0 ; i < 5 ; i++) {
		// 	buttons.add(new JGradientButton(name[i] , color[i]));
		// }
				
		// buttons.get(0).setMaximumSize(new Dimension(buttons.get(1).getMaximumSize()));
		// buttons.get(2).setMaximumSize(new Dimension(buttons.get(3).getMaximumSize()));
		// buttons.get(4).setMaximumSize(new Dimension(buttons.get(3).getMaximumSize()));
		// buttons.get(4).setPreferredSize(new Dimension(buttons.get(3).getMaximumSize()));

		
		// for (JGradientButton button : buttons) {
		// 	button.addActionListener(new ActionListener() {
		
		// 		@Override
		// 		public void actionPerformed(ActionEvent e) {
		// 			if (button == buttons.get(0)) {
		// 				GamePanel.b1Pressed();
		// 			} else if (button == buttons.get(1)) {
		// 				GamePanel.b2Pressed();
		// 				update(button, GamePanel.removeFlag, Color.green, Color.red);
		// 			} else if (button == buttons.get(2)) {
		// 				GamePanel.b3Pressed();collisions
		// 				update(button, !GamePanel.collisionFlag, Color.green, Color.red);
		// 			} else if (button == buttons.get(3)) {
		// 				GamePanel.b4Pressed();
		// 				update(button, !GamePanel.electricFlag, Color.green, Color.red);
		// 			} else {
		// 				GamePanel.b5Pressed();
		// 				update(button, !GamePanel.gravityFlag, Color.green, Color.red);
		// 			}
		// 		}
		// 	});
		// }
		 // Add the buttons into the panel with three different alignment options
		//  for(JGradientButton button : buttons) button.setAlignmentX(Component.CENTER_ALIGNMENT);
	}

	static protected void update(JGradientButton button, boolean state, Color trueState, Color falseState) {
		button.color = state ? trueState : falseState;
		button.repaint();
	}

	static void addComponentsToFrame(GameFrame frame, GamePanel GamePanel){
		panelParticleNumber.add(buttons.get(0));
        panelParticleNumber.add(buttons.get(1));
     
        panelForcesWest.add(buttons.get(2));
        panelForcesWest.add(buttons.get(3));
        
        panelForcesEast.add(buttons.get(4));

        
        panelSouth.add(panelParticleNumber);
        panelSouth.add(Box.createHorizontalStrut(60));
        panelSouth.add(panelForcesContainer);

		
		mainPanel.add(panelSouth, BorderLayout.SOUTH);
		mainPanel.add(GamePanel, BorderLayout.CENTER);
		// frame.add(mainPanel);
	}
	static void initializeComponents(GameFrame frame, GamePanel GamePanel) {
		initializePanels(frame, GamePanel);
		initializeStatistics();
		// initializeButtons(GamePanel, scene);
		addComponentsToFrame(frame, GamePanel);
        
		
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(10, 10, 20, 2));
		spinner.setBounds(178, 12, 59, 26);
		
	}
	public void updateLabels(int[] list) {
		//particleNumber -> totalElectircEnergy -> totalPotential
		//-> totalCollisions
		
	}
	public static void main(String[] args) {

		// try {
		//     for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		//         if ("Nimbus".equals(info.getName())) {
		//             UIManager.setLookAndFeel(info.getClassName());
		//             break;
		//         }
		//     }
		// } catch (Exception e) {
		//     // If Nimbus is not available, you can set the GUI to another look and feel.
		// }
		// GameFrame frame = new GameFrame();
		// GamePanel GamePanel = new GamePanel();
		

		// frame.setTitle("Particle Fun");
		// frame.setSize(frame_width, frame_height);
		// frame.setDefaultLookAndFeelDecorated(true);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		
		// initializeComponents(frame, GamePanel);
		// GamePanel.setSize(GameFrame.frame_width - GameFrame.sidePanel.getSize().width,
		// 		GameFrame.frame_height - GameFrame.panelSouth.getSize().height);
		
		// frame.setVisible(true);
		// frame.setResizable(true);
		
		// ShapeManager shapee = new ShapeManager(new Point2D(GamePanel.getSize().width/2, GamePanel.getSize().height/2));
		// GamePanel.shape = shapee;
		// GamePanel.physicsTimer.start();
		// GamePanel.fpsTimer.start();
		// GamePanel.initializeParticles(28);	
		// System.out.println(panelSouth.getSize().height);
		// System.out.println(frame_height- panelSouth.getSize().height);
		// System.out.println(frame_height);
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

		BorderPane mainPanel = (BorderPane) scene.lookup("#MainPanel");
		Pane shapesPanel = (Pane) scene.lookup("#ShapesPanel");
		Pane forcesPanel = (Pane) scene.lookup("#ForcesPanel");
		Pane addRemovePanel = (Pane) scene.lookup("#AddRemovePanel");



		SwingNode swingNode = new SwingNode();
		GamePanel gamePanel = new GamePanel();
		gamePanel.setSize(691, 452);
		gamePanel.physicsTimer.start();
		gamePanel.fpsTimer.start();


		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				swingNode.setContent(gamePanel);
			}
		});
		mainPanel.setCenter(swingNode);

		ShapeManager shapee = new ShapeManager(new Point2D(gamePanel.getSize().getWidth()/2, 
											gamePanel.getSize().getHeight()/2));
		gamePanel.shape = shapee;
		gamePanel.initializeParticles(15);
		controller.initializeButtons(gamePanel, scene);
		// controller.test();

		// System.out.println("width is : " + mainPanel.widthProperty().getValue());
		// System.out.println("width is : " + mainPanel.widthProperty());
    
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}

    }

}
