/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.ilushechkinea.javaformatter;

import java.util.Properties;

/**
 * Enumeration of recognizable formatter properties
 * @author ilushechkinea
 */
 public enum FormatterProperties {
    INDENT_SIZE("formatter.indent.size", "4"), 
    INDENT_SYMBOL("formatter.indent.symbol", " "),
    LOGGING_LEVEL("formatter.logging.level", "INFO"); 

    /** Name of the property, key in the .property file */
    private String name;

    /** Default value of the property */
    private String defaultValue;

    private FormatterProperties(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets property name
     * @return property name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets property default value
     * @return property default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}