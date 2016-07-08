package com.edgewonk.fxcomponents;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.embed.swing.JFXPanel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class DateTimePickerTest {
  JFXPanel jfxPanel;

  @Before
  public void init() {
    jfxPanel = new JFXPanel();
  }

  @Test
  public void testNotAllowNull() {
    DateTimePicker dateTimePicker = new DateTimePicker();
    dateTimePicker.setAllowNull(false);
    LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 15));
    dateTimePicker.dateTimeProperty().setValue(localDateTime);
    dateTimePicker.dateTimeProperty().setValue(null);
    assertEquals(localDateTime, dateTimePicker.dateTimeProperty().getValue());
    assertEquals(localDateTime, dateTimePicker.getDateTime());
  }

  @Test
  public void testAllowNull() {
    DateTimePicker dateTimePicker = new DateTimePicker();
    dateTimePicker.setAllowNull(true);
    LocalDateTime localDateTime = LocalDateTime.now();
    dateTimePicker.dateTimeProperty().setValue(localDateTime);
    dateTimePicker.dateTimeProperty().setValue(null);
    assertNull(dateTimePicker.dateTimeProperty().getValue());
    assertNull(dateTimePicker.getDateTime());
  }

  @Test
  public void testNotAllowTime() {
    DateTimePicker dateTimePicker = new DateTimePicker();
    dateTimePicker.setAllowTime(false);
    LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 12));
    dateTimePicker.dateTimeProperty().setValue(localDateTime);
    assertEquals(0, dateTimePicker.getDateTime().getHour());
    assertEquals(0, dateTimePicker.getDateTime().getMinute());
  }

}