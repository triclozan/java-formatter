/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.omsu.ilushechkinea.javaformatter.exceptions;

/**
 * Exception for failure while saving or loading exceptions
 * @author ilushechkinea
 */
public class SettingsIOException extends Exception {
      public SettingsIOException() {}

      //Constructor that accepts a message
      public SettingsIOException(String message)
      {
         super(message);
      }
}