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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
    private Pane StatisticsPanel;

    @FXML
    private TitledPane MassChargePanel;

    @FXML
    private BorderPane MainPanel;

    public void increment() {
        System.out.println("here");
    }

    public void initializeButtons(GamePanel gamePanel, Scene scene){
        ToggleGroup toggles = new ToggleGroup();
        circle.setToggleGroup(toggles);
        square.setToggleGroup(toggles);
        diamond.setToggleGroup(toggles);

 
        // ArrayList<ButtonBase> buttons = new ArrayList<ButtonBase>(
        //     Arrays.asList(addParticle, removeParticle, collisions, electrostatics, gravity));
        // for(ButtonBase button : buttons){
        //     button.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b1Pressed());

        // }
        addParticle.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b1Pressed());
        removeParticle.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b2Pressed());
        collisions.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b3Pressed());
        electrostatics.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b4Pressed());
        gravity.addEventHandler(ActionEvent.ACTION, event -> gamePanel.b5Pressed());

		// System.out.println("SD " + collisions );
		// // CheckBox gravity = (CheckBox) scene.lookup("#gravity");
		
		// // CheckBox electrostatics = (CheckBox) scene.lookup("#electrostatics");

		toggles.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                 if(toggles.getSelectedToggle().isSelected()){
                         toggles.getSelectedToggle().setSelected(false);
                     } else {
                         toggles.getSelectedToggle().setSelected(true);
                     }
                System.out.println("curently : " + toggles.getSelectedToggle().isSelected());
        
             } 
        });
    }
    public void test(){
        System.out.println("IDFDSF");
    }

}
