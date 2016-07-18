package com.edgewonk.fxcomponents;

import org.junit.Before;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RadioButtonExtTest {
  JFXPanel jfxPanel;

  @Before
  public void init() {
    jfxPanel = new JFXPanel();
  }

  @Test
  public void testSecondClickDeselectButton() {
    RadioButtonExt radioButtonExt = new RadioButtonExt();
    assertFalse(radioButtonExt.isSelected());

    radioButtonExt.setSelected(true);

    //first click
    radioButtonExt.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
    assertTrue(radioButtonExt.isSelected());

    //second click
    radioButtonExt.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
    assertFalse(radioButtonExt.isSelected());
  }
}