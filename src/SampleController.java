import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
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
    private RadioButton sunflower;

    @FXML
    private Pane AddRemovePanel;

    @FXML
    private Button removeParticle;

    @FXML
    private Button addParticle;

    @FXML
    private Button removeAll;

    @FXML
    private Spinner<Integer>  addXParticles;

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
    private Spinner<Double>  spiralSlider;

    @FXML
    private Button resetCharge;

    @FXML
    private Button resetMass;

    @FXML
    private BorderPane MainPanel;


    
    /** 
     * Method that sets the default values and handlers to the interactive components of the scene/canvas. 
     * 
     * @param mainPanel     instance of the GamePanel to interact with the canvas.
     */
    public void initializeButtons(MainPanel mainPanel){
        // massSlider.setStyle("-fx-control-inner-background: palegreen;");
        // chargeSlider.setStyle("-fx-control-inner-background: palegreen;");

        ToggleGroup toggles = new ToggleGroup();

        SpinnerValueFactory<Integer> spinnerAddParticleValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 1);
        addXParticles.setValueFactory(spinnerAddParticleValues);

        SpinnerValueFactory<Double>  spinnerSpiralAngleValues = new SpinnerValueFactory.DoubleSpinnerValueFactory(4, 10, (
            Math.PI * (1 + Math.sqrt(5) / 4) ),
            0.4
        );
        spiralSlider.setValueFactory(spinnerSpiralAngleValues);
        
        addParticle.addEventHandler(ActionEvent.ACTION, event -> mainPanel.addParticleButtonPressed(
            (int) Math.round(massSlider.valueProperty().getValue()),
            (int) Math.round(chargeSlider.valueProperty().getValue()),
            (int) addXParticles.getValue()
        ));

        spiralSlider.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,Number oldValue , Number newValue ) {
                mainPanel.setSpiralAngle( spiralSlider.valueProperty().getValue());
            }
        });

        removeParticle.addEventHandler(ActionEvent.ACTION, event -> mainPanel.removeParticleButtonPressed("one"));
        removeAll.addEventHandler(ActionEvent.ACTION, event -> mainPanel.removeParticleButtonPressed("all"));

        collisions.addEventHandler(ActionEvent.ACTION, event -> mainPanel.forcesButtonsPressed("Collision"));
        electrostatics.addEventHandler(ActionEvent.ACTION, event -> mainPanel.forcesButtonsPressed("Electrostatics"));
        gravity.addEventHandler(ActionEvent.ACTION, event -> mainPanel.forcesButtonsPressed("Gravity"));

        ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>(
            Arrays.asList(circle, square, diamond, spiral, looseSpiral, sunflower)
        );
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


    
    /** 
     * Method that updates the statistics labels with the appropriate values
     * 
     * @param pNumber   number of particles present on the canvas
     * @param stats     double array - total electric force , total potential force, number of collisions per second
     */
    public void setLabels(int pNumber, double[] stats) {
        Platform.runLater(() -> {
            numberOfParticles.setText(Integer.toString(pNumber));
		    totalElectric.setText(Double.toString(stats[0]));
		    totalPotential.setText(Double.toString(stats[1]));
		    collisionsPerSecond.setText(Double.toString(stats[2]));
        });
	}
}