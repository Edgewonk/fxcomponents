package com.edgewonk.fxcomponents;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author armin
 */
public class FileChooserControl extends Pane {

  private final HBox layout = new HBox(8);
  private final Button button = new Button();
  private final TextField textField = new TextField();
  private final BooleanProperty disableProperty = new SimpleBooleanProperty(false);
  private final BooleanProperty existsProperty = new SimpleBooleanProperty();
  private final StringProperty titleProperty = new SimpleStringProperty("Pick file...");
  private final BooleanProperty saveProperty = new SimpleBooleanProperty(false);
  private final ObjectProperty<ObservableList<String>> extensions = new SimpleObjectProperty<>(FXCollections.observableArrayList());

  public FileChooserControl() {
    // initialize view.
    getChildren().setAll(layout);
    layout.getChildren().add(textField);
    layout.getChildren().add(button);

    button.setText("Open");

    // add an icon for the button.
    button.setOnAction(event -> pickFile());

    // make the exists property...
    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      existsProperty.set(newValue != null && new File(newValue).exists());
    });
  }

  /**
   * pick a file on button click.
   */
  private void pickFile() {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(titleProperty.get());

    // check if there's previous input
    if (!"".equals(textField.getText())) {
      final File f = new File(textField.getText());
      if (f.exists()) {
        // previous input exists, thus use it as default values.
        fileChooser.setInitialDirectory(f.getParentFile());
        fileChooser.setInitialFileName(f.getName());
      }
    }

    // add file extensions.
    extensions.get().stream()
      .map(x -> {
        final String[] split1 = x.split(":");
        final String[] extensions = split1[1].split(",");
        return new FileChooser.ExtensionFilter(split1[0], extensions);
      })
      .forEach(x -> fileChooser.getExtensionFilters().add(x));

    // show a file picker and use it's result if possible,
    final File result = isSave() ? fileChooser.showSaveDialog(null) : fileChooser.showOpenDialog(null);
    if (result != null) {
      textField.setText(result.getAbsolutePath());
    }
  }

  public ObservableList<String> getExtensions() {
    return extensions.get();
  }

  public ObjectProperty<ObservableList<String>> extensionsProperty() {
    return extensions;
  }

  public void setExtensions(final ObservableList<String> extensions) {
    this.extensions.set(extensions);
  }

  public boolean isSave() {
    return saveProperty.get();
  }

  public BooleanProperty saveProperty() {
    return saveProperty;
  }

  public void setSave(final boolean saveProperty) {
    this.saveProperty.set(saveProperty);
  }

  public String getFilename() {
    return textField.getText();
  }

  public void setFilename(final String filename) {
    textField.setText(filename);
  }

  public StringProperty fileNameProperty() {
    return textField.textProperty();
  }

  public String getTitleProperty() {
    return titleProperty.get();
  }

  public StringProperty titlePropertyProperty() {
    return titleProperty;
  }

  public void setTitleProperty(final String titleProperty) {
    this.titleProperty.set(titleProperty);
  }

  public boolean isExists() {
    return existsProperty.get();
  }

  public ReadOnlyBooleanProperty existsProperty() {
    return existsProperty;
  }

  public Button getButton() {
    return button;
  }
}
