import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.text.html.StyleSheet;

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
    private RadioButton spiral;

    @FXML
    private RadioButton looseSpiral;

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

    @FXML
    private Button resetCharge;

    @FXML
    private Button resetMass;

    @FXML
    private BorderPane MainPanel;


    public void initializeButtons(MainPanel mainPanel){
        // massSlider.setStyle("-fx-control-inner-background: palegreen;");
        // chargeSlider.setStyle("-fx-control-inner-background: palegreen;");

        ToggleGroup toggles = new ToggleGroup();
        
        addParticle.addEventHandler(ActionEvent.ACTION, event -> mainPanel.b1Pressed(
            (int) Math.round(massSlider.valueProperty().getValue()),
            (int) Math.round(chargeSlider.valueProperty().getValue())
        ));
        removeParticle.addEventHandler(ActionEvent.ACTION, event -> mainPanel.b2Pressed());
        collisions.addEventHandler(ActionEvent.ACTION, event -> mainPanel.b3Pressed());
        electrostatics.addEventHandler(ActionEvent.ACTION, event -> mainPanel.b4Pressed());
        gravity.addEventHandler(ActionEvent.ACTION, event -> mainPanel.b5Pressed());

        ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>(Arrays.asList(circle, square, diamond, spiral, looseSpiral));
        for(RadioButton button : radioButtons){
            button.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (button.isSelected()) {
                    toggles.selectToggle(null);
                    e.consume();

                }
                mainPanel.shapeButtonPressed(button.getText());
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