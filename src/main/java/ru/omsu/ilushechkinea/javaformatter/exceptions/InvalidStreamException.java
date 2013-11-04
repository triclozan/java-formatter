/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.omsu.ilushechkinea.javaformatter.exceptions;

/**
 * Exception for invalid input or output stream provided
 * @author ilushechkinea
 */
public class InvalidStreamException extends Exception {
      public InvalidStreamException() {}

      //Constructor that accepts a message
      public InvalidStreamException(String message)
      {
         super(message);
      }
}