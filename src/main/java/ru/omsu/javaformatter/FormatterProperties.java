/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.javaformatter;

import java.util.Properties;

/**
 *
 * @author ilushechkinea
 */
 public enum FormatterProperties {
    INDENT_SIZE("formatter.indent.size", "4"), 
    INDENT_SYMBOL("formatter.indent.symbol", " "); 
    private String name;
    private String defaultValue;

    private FormatterProperties(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}