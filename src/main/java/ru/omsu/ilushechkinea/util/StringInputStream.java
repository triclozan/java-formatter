/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.ilushechkinea.util;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * Simple ByteArrayInputStream wrapper (InputStream descendant) that reads from a string
 * @author ilushechkinea
 */
public class StringInputStream extends ByteArrayInputStream {
    public StringInputStream(String s) throws UnsupportedEncodingException {
        super(s.getBytes("UTF-8"));
    }
}
