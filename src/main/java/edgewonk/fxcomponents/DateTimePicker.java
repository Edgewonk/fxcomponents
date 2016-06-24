package edgewonk.fxcomponents;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.time.LocalDateTime;

/**
 * This is a simple datetimepicker component. It was developed for the trade entry wizard in the edgewonk trading journal. It should do the following things:
 *
 * 1. It has a button that will open a date picker
 * 2. It has a textbox which can be used to enter the date according to the following format:
 *    YEAR-MONTH-DAY HOUR:MINUTE
 * 3. Users can set if the field may be nulled, which means that the user can leave the field blank, if the field can not be nulled and the user blanks out the
 *    field the picker should refill it with the previously entered date.
 * 4. Users can set if the time entry is mandatory, if not the localdatetime object should be at 0:00 hours of the selected day.
 *
 * @author armin
 */
public class DateTimePicker extends Pane {

  private final ObjectProperty<LocalDateTime> dateTimeProperty = new SimpleObjectProperty<>();
  private final BooleanProperty allowNull = new SimpleBooleanProperty(false);
  private final BooleanProperty allowTime = new SimpleBooleanProperty(false);

  @FXML private Button btnShowCalendar;

  public DateTimePicker() {
    // load fxml
    getChildren().setAll(loadView());

    // bind properties, etc.
  }

  public LocalDateTime getDateTimeProperty() {
    return dateTimeProperty.get();
  }

  public ObjectProperty<LocalDateTime> dateTimePropertyProperty() {
    return dateTimeProperty;
  }

  public void setDateTimeProperty(final LocalDateTime dateTimeProperty) {
    this.dateTimeProperty.set(dateTimeProperty);
  }

  public boolean getAllowNull() {
    return allowNull.get();
  }

  public BooleanProperty allowNullProperty() {
    return allowNull;
  }

  public void setAllowNull(final boolean allowNull) {
    this.allowNull.set(allowNull);
  }

  public boolean getAllowTime() {
    return allowTime.get();
  }

  public BooleanProperty allowTimeProperty() {
    return allowTime;
  }

  public void setAllowTime(final boolean allowTime) {
    this.allowTime.set(allowTime);
  }

  private Node loadView() {
    // TODO not implemented
    return new StackPane();
  }

}
