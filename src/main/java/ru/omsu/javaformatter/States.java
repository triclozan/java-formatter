/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.javaformatter;

/**
 *
 * @author ilushechkinea
 */
public enum States {
    STRING_START("[Start of a line]"), 
    AFTER_BRACE("[After a brace]"),
    NORMAL("[Normal copying]"); 
    private String name;

    private States(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }    
}
