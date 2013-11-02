/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.omsu.javaformatter;

/**
 *
 * @author ilushechkinea
 */
public enum FormatterWarnings {
    WRN_RIGHT_BRACE("Right brace mismatch"), 
    WRN_LEFT_BRACE("Left brace mismatch"); 
    private String name;

    private FormatterWarnings(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }    
}
