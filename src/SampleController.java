import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class SampleController {

    @FXML
    private Pane ForcesPanel;

    @FXML
    private CheckBox collisions;

    @FXML
    private CheckBox gravity;

    @FXML
    private CheckBox electrostatics;

    @FXML
    private RadioButton circle;

    @FXML
    private RadioButton square;

    @FXML
    private RadioButton diamond;

    @FXML
    private Pane AddRemovePanel;

    @FXML
    private Button removeParticle;

    @FXML
    private Button addParticle;

    @FXML
    private Label numberOfParticles;

    @FXML
    private Label totalElectric;

    @FXML
    private Label totalPotential;

    @FXML
    private Label collisionsPerSecond;

    @FXML
    private Pane StatisticsPanel;

    @FXML
    private TitledPane MassChargePanel;

    @FXML
    private Slider massSlider;

    @FXML
    private Slider chargeSlider;

    // private final double massSliderDefaultValue = massSlider.valueProperty().getValue();
    // private final double chargeSliderDefaultValue = chargeSlider.valueProperty().getValue();

    @FXML
    private Button resetCharge;

    @FXML
    private Button resetMass;

    @FXML
    private BorderPane MainPanel;

    public void initializeButtons(GamePanel gamePanel, Scene scene){
        ToggleGroup toggles = new ToggleGroup();
        // circle.setToggleGroup(toggles);
        // square.setToggleGroup(toggles);
        // diamond.setToggleGroup(toggles);

 
        // ArrayList<ButtonBase> buttons = new ArrayList<ButtonBase>(
        //     Arrays.asList(addParticle, removeParticle, collisions, electrostatics, gravity));
        // for(ButtonBase button : buttons){
        //     button.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b1Pressed());

        // }
        addParticle.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b1Pressed(
            (int) Math.round(massSlider.valueProperty().getValue()),
            (int) Math.round(chargeSlider.valueProperty().getValue())
        ));
        removeParticle.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b2Pressed());
        collisions.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b3Pressed());
        electrostatics.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b4Pressed());
        gravity.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b5Pressed());

        // circle.addEventHandler(ActionEvent.ACTION, event -> System.out.println("IN"));
        // square.addEventHandler(ActionEvent.ACTION, event -> gamePanel.shapeButtonPressed("square"));
        // diamond.addEventHandler(ActionEvent.ACTION, event -> gamePanel.shapeButtonPressed("diamond"));

		// toggles.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
        //     public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
        //          if(toggles.getSelectedToggle().isSelected()){
        //                  toggles.getSelectedToggle().setSelected(false);
        //              } else {
        //                  toggles.getSelectedToggle().setSelected(true);
        //              }
        //         System.out.println("curently : " + toggles.getSelectedToggle().isSelected());
        
        //      } 
        // });

        ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>(Arrays.asList(circle, square, diamond));
        for(RadioButton button : radioButtons){
            button.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (button.isSelected()) {
                    toggles.selectToggle(null);
                    e.consume();

                }
                gamePanel.shapeButtonPressed(button.getText());
            });
            button.setToggleGroup(toggles);
        }

        resetMass.addEventHandler(ActionEvent.ACTION, event -> massSlider.setValue(100));
        resetCharge.addEventHandler(ActionEvent.ACTION, event -> chargeSlider.setValue(5));
    }


    public void setLabels(int pNumber, double[] stats) {
		numberOfParticles.setText(Integer.toString(pNumber));
		totalElectric.setText(Double.toString(stats[0]));
		totalPotential.setText(Double.toString(stats[1]));
		collisionsPerSecond.setText(Double.toString(stats[2]));
	}
}