/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.ilushechkinea.javaformatter;

/**
 * Enumeration of finite automata (kernel of the formatter) states
 * @author ilushechkinea
 */
public enum FormatterStates {
    STRING_START("[Start of a line]"), 
    AFTER_BRACE("[After a brace]"),
    NORMAL("[Normal copying]"); 
    private String name;

    private FormatterStates(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }    
}
