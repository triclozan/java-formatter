/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.omsu.ilushechkinea.javaformatter.exceptions;

/**
 * Exception in formatting process
 * @author ilushechkinea
 */
public class FormattingException extends Exception {
      public FormattingException() {}

      //Constructor that accepts a message
      public FormattingException(String message)
      {
         super(message);
      }
}