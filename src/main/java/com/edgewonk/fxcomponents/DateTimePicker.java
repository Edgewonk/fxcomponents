package com.edgewonk.fxcomponents;

import com.sun.javafx.scene.control.skin.DatePickerContent;
import com.sun.javafx.scene.control.skin.DatePickerSkin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
public class DateTimePicker extends DatePicker {

  private final ObjectProperty<LocalDateTime> dateTimeProperty = new SimpleObjectProperty<>();
  private final BooleanProperty allowNull = new SimpleBooleanProperty(true);
  private final BooleanProperty allowTime = new SimpleBooleanProperty(true);
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private final TextField textHours = new TextField();
  private final TextField textMinutes = new TextField();

  public DateTimePicker() {
    allowTime.addListener((observable, oldValue, newValue) -> {
      textHours.setEditable(newValue);
      textMinutes.setEditable(newValue);

      if (!newValue) {
        textHours.setText("00");
        textMinutes.setText("00");
      }
    });

    limitTimeField(textHours, 23);
    limitTimeField(textMinutes, 59);
    setConverter();

    setOnShowing(event -> {
      if (dateTimeProperty.get() == null) {
        if (allowTime.get()) {
          LocalTime now = LocalTime.now();
          textHours.setText(String.valueOf(now.getHour()));
          textMinutes.setText(String.valueOf(now.getMinute()));
        }
      }
      getEditor().fireEvent(new KeyEvent(getEditor(), getEditor(), KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
    });

    addValuePropertyListener();

    // Synchronize changes to dateTimePropertyProperty back to the underlying date value
    dateTimeProperty.addListener((observable, oldValue, newValue) -> {
      if (oldValue != null && newValue == null && !allowNull.get()) {
        dateTimeProperty.setValue(oldValue);
      }

      setValue(newValue == null ? null : newValue.toLocalDate());
    });

    // Persist changes onblur
    getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        getEditor().fireEvent(new KeyEvent(getEditor(), getEditor(), KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
      }
    });

    setOnMouseClicked(event -> {
      if (!isShowing()) {
        setTimeFromCalendarPopup();
      }
    });

    final EventHandler<MouseEvent> dayCellActionHandler = ev -> {
      if (ev.getButton() != MouseButton.PRIMARY) {
        return;
      }
      setTimeFromCalendarPopup();
    };

    setDayCellFactory(param -> {
      DateCell dateCell = new DateCell();
      dateCell.addEventHandler(MouseEvent.MOUSE_CLICKED, dayCellActionHandler);
      return dateCell;
    });
  }

  private LocalTime getLocalTime(LocalTime current) {
    int hours;
    int minutes;
    if (textHours.getText().isEmpty()) {
      hours = current.getHour();
    } else {
      hours = Integer.parseInt(textHours.getText());
    }

    if (textMinutes.getText().isEmpty()) {
      minutes = current.getMinute();
    } else {
      minutes = Integer.parseInt(textMinutes.getText());
    }

    return LocalTime.of(hours, minutes);
  }

  private void limitTimeField(TextField textField, int maxValue) {
    textField.setPrefColumnCount(2);

    textField.textProperty().addListener((ov, oldValue, newValue) -> {
      if (!newValue.chars().allMatch(Character::isDigit)) {
        textField.setText(oldValue);
        return;
      }

      String textValue = textField.getText();

      if (textValue.length() == 0) {
        textField.setText("00");
      } else if (textValue.length() == 1) {
        textField.setText("0" + textValue);
      } else if (textValue.length() == 2) {
        if (Integer.parseInt(textValue) > maxValue) {
          textField.setText("0" + textValue.substring(1));
        }
      } else {
        //textValue.length() > 2
        int caretPosition = textField.getCaretPosition();

        if (caretPosition <= 1) {
          textValue = textValue.substring(0, 2);
          if (Integer.parseInt(textValue) > maxValue) {
            textValue = "0" + textValue.substring(1);
          }
          Platform.runLater(() -> textField.positionCaret(textField.getCaretPosition() + 1));
        } else {
          textValue = textValue.substring(textValue.length() - 2, textValue.length());
          if (Integer.parseInt(textValue) > maxValue) {
            textValue = "0" + textValue.substring(1);
          }
        }
        textField.setText(textValue);
      }
    });
  }

  private void setConverter() {
    setConverter(new StringConverter<LocalDate>() {
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
          if (localDateTime == null) {
            return null;
          }
        }

        if (allowTime.get()) {
          textHours.setText(String.valueOf(localDateTime.getHour()));
          textMinutes.setText(String.valueOf(localDateTime.getMinute()));
        }

        dateTimeProperty.set(localDateTime);
        return dateTimeProperty.get().toLocalDate();
      }
    });
  }

  private void setTimeFromCalendarPopup() {
    LocalDateTime localDateTime = dateTimeProperty.get();
    if (localDateTime == null) {
      return;
    }
    LocalTime time = getLocalTime(localDateTime.toLocalTime());
    localDateTime = localDateTime.with(time);

    dateTimeProperty.set(localDateTime);

    getEditor().setText(getConverter().toString(localDateTime.toLocalDate()));
  }

  private void addValuePropertyListener() {
    valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        if (allowNull.get()) {
          dateTimeProperty.set(null);
        }
      } else {
        if (dateTimeProperty.get() == null) {
          LocalTime localTime = getLocalTime(LocalTime.now());
          dateTimeProperty.set(LocalDateTime.of(newValue, localTime));
        } else {
          LocalTime time = getLocalTime(dateTimeProperty.get().toLocalTime());
          dateTimeProperty.set(LocalDateTime.of(newValue, time));
        }
      }
    });
  }

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
}
