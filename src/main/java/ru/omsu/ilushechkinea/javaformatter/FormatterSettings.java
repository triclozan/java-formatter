/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.ilushechkinea.javaformatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import ru.omsu.ilushechkinea.javaformatter.exceptions.SettingsIOException;

/**
 * Loads, saves and stores set of formatter settings as a map
 * @author ilushechkinea
 */
 public class FormatterSettings {
    static private Logger log = Logger.getLogger(FormatterSettings.class);

    /** Collection of properties mapped to their values */
    private Map<FormatterProperties, String> values;
       
    public FormatterSettings() {
        values = new EnumMap<FormatterProperties, String>(FormatterProperties.class);
        for(FormatterProperties property : FormatterProperties.values()) {
            values.put(property, property.getDefaultValue());
        }
    }

    /**
     * Loads settings from .property file
     * @param filename File .property with settings
     * @throws SettingsIOException
     */
    public void loadFromFile(String filename) throws SettingsIOException {
        Properties p = new Properties();

        try {
            InputStream rs = new FileInputStream(filename);
            p.load(rs);
        }
        catch (Exception ex) {
            log.warn("Failed to load settings from file " + filename);
            throw new SettingsIOException("Failed to save settings to file " + filename);
        }

        for(FormatterProperties property : FormatterProperties.values()){
            setValue(property, p.getProperty(property.getName(), property.getDefaultValue()));
        }
    }

    /**
     * Saves settings to given file
     * @param filename Destination file
     * @throws SettingsIOException
     */
    public void saveToFile(String filename) throws SettingsIOException {
        Properties p = new Properties();
        for(FormatterProperties property : FormatterProperties.values()){
            p.setProperty(property.getName(), getValue(property));
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
            p.store(os, null);
            os.close();
        } 
        catch (Exception ex) {
            log.warn("Failed to save settings to file " + filename);
            throw new SettingsIOException("Failed to save settings to file " + filename);
        }
    }

    /**
     * Gets value of given property
     * @param property Property to get value for
     * @return value of given property
     */
    public String getValue(FormatterProperties property){
        return values.get(property);
    }

    /**
     * Sets specified value for given property
     * @param property Property to set value for
     * @param value New value for the property
     */
    public void setValue(FormatterProperties property, String value) {
        values.put(property, value);
    }
}