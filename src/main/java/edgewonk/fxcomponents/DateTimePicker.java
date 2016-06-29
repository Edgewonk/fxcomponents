package edgewonk.fxcomponents;

import com.sun.javafx.scene.control.skin.DatePickerContent;
import com.sun.javafx.scene.control.skin.DatePickerSkin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

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
  private final BooleanProperty allowNull = new SimpleBooleanProperty(true);
  private final BooleanProperty allowTime = new SimpleBooleanProperty(true);
  private final DatePicker datePicker;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private final TextField textHours = new TextField();
  private final TextField textMinutes = new TextField();

  public DateTimePicker() {
    allowTime.addListener((observable, oldValue, newValue) -> {
      textHours.setEditable(newValue);
      textMinutes.setEditable(newValue);

      if(!newValue) {
        textHours.setText("00");
        textMinutes.setText("00");
      }
    });

    textHours.setPrefColumnCount(2);
    textHours.textProperty().addListener((ov, oldValue, newValue) -> {
      if (!newValue.chars().allMatch(Character::isDigit)) {
        textHours.setText(oldValue);
        return;
      }
      if (textHours.getText().length() > 2) {
        String s = textHours.getText().substring(0, 2);
        textHours.setText(s);
      }
    });

    textMinutes.setPrefColumnCount(2);

    datePicker = new DatePicker() {
      @Override
      protected Skin<?> createDefaultSkin() {
        DatePickerSkin datePickerSkin = new DatePickerSkin(this);
        DatePickerContent datePickerContent = (DatePickerContent) datePickerSkin.getPopupContent();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(7.);

        hBox.getChildren().addAll(textHours, new Label(":"), textMinutes);
        datePickerContent.getChildren().add(0, hBox);

        return datePickerSkin;
      }
    };

    datePicker.setConverter(new StringConverter<LocalDate>() {
      @Override
      public String toString(LocalDate object) {
        LocalDateTime value = getDateTime();
        if (value == null) {
          return "";
        }

        if (!allowTime.get()) {
          value = value.withHour(0).withMinute(0).withSecond(0);
        }

        return value.format(formatter);
      }

      @Override
      public LocalDate fromString(String value) {
        if (value == null || (allowNull.get() && value.trim().isEmpty())) {
          dateTimeProperty.set(null);
          return null;
        }

        LocalDateTime localDateTime;
        try {
           localDateTime = LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException e) {
          localDateTime = dateTimeProperty.get();
          if(localDateTime == null) {
            return null;
          }
        }

        if(allowTime.get()) {
          textHours.setText(String.valueOf(localDateTime.getHour()));
          textMinutes.setText(String.valueOf(localDateTime.getMinute()));
        }

        dateTimeProperty.set(localDateTime);
        return dateTimeProperty.get().toLocalDate();
      }
    });

    datePicker.setOnShowing(event -> {
      if(dateTimeProperty.get() == null) {
        if(allowTime.get()) {
          LocalTime now = LocalTime.now();
          textHours.setText(String.valueOf(now.getHour()));
          textMinutes.setText(String.valueOf(now.getMinute()));
        }
      }
      datePicker.getEditor().fireEvent(new KeyEvent(datePicker.getEditor(), datePicker.getEditor(), KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
    });

    datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        if (allowNull.get()) {
          dateTimeProperty.set(null);
        }
      } else {
        if (dateTimeProperty.get() == null) {
          LocalTime localTime = LocalTime.now();
          if (!textHours.getText().isEmpty()) {
            localTime = localTime.withHour(Integer.parseInt(textHours.getText()));
          }

          if (!textMinutes.getText().isEmpty()) {
            localTime = localTime.withMinute(Integer.parseInt(textMinutes.getText()));
          }
          dateTimeProperty.set(LocalDateTime.of(newValue, localTime));
        } else {
          LocalTime time = dateTimeProperty.get().toLocalTime();
          int hours;
          int minutes;
          if (textHours.getText().isEmpty()) {
            hours = time.getHour();
          } else {
            hours = Integer.parseInt(textHours.getText());
          }

          if (textMinutes.getText().isEmpty()) {
            minutes = time.getMinute();
          } else {
            minutes = Integer.parseInt(textMinutes.getText());
          }
          time = LocalTime.of(hours, minutes);

          dateTimeProperty.set(LocalDateTime.of(newValue, time));
        }
      }
    });

    // Synchronize changes to dateTimePropertyProperty back to the underlying date value
    dateTimeProperty.addListener((observable, oldValue, newValue) -> {
      if (oldValue != null && newValue == null && !allowNull.get()) {
        dateTimeProperty.setValue(oldValue);
      }

      datePicker.setValue(newValue == null ? null : newValue.toLocalDate());
    });

    // Persist changes onblur
    datePicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        datePicker.getEditor().fireEvent(new KeyEvent(datePicker.getEditor(), datePicker.getEditor(), KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
      }
    });

    getChildren().setAll(loadView());
  }

  public LocalDateTime getDateTime() {
    return dateTimeProperty.get();
  }

  public ObjectProperty<LocalDateTime> dateTimeProperty() {
    return dateTimeProperty;
  }

  public void setDateTime(final LocalDateTime dateTimeProperty) {
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
    StackPane stackPane = new StackPane();
    stackPane.getChildren().add(datePicker);
    return stackPane;
  }
}
