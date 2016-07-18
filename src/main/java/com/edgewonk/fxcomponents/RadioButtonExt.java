package com.edgewonk.fxcomponents;


import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * RadioButton which can be unselected on second click
 */
public class RadioButtonExt extends RadioButton {
  private boolean prevValue;

  public RadioButtonExt() {
    selectedProperty().addListener((observable, oldValue, newValue) -> {
      prevValue = oldValue;
    });

    addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
      if (event.getButton() != MouseButton.PRIMARY) {
        return;
      }

      if (prevValue) {
        setSelected(false);
      } else {
        prevValue = true;
      }
    });
  }
}
