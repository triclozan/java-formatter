/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.javaformatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ilushechkinea
 */
 public class FormatterSettings {
    private Map<FormatterProperties, String> values;
       
    public FormatterSettings() {
        values = new EnumMap<FormatterProperties, String>(FormatterProperties.class);
        for(FormatterProperties property : FormatterProperties.values()) {
            values.put(property, property.getDefaultValue());
        }
    }
       
    public void loadFromFile(String filename) throws Exception {
        Properties p = new Properties();
        InputStream rs = new FileInputStream(filename);
        p.load(rs);
        
        for(FormatterProperties property : FormatterProperties.values()){
            setValue(property, p.getProperty(property.getName(), property.getDefaultValue()));
        }
    }
           
    public void saveToFile(String filename) {
        Properties p = new Properties();
        for(FormatterProperties property : FormatterProperties.values()){
            p.setProperty(property.getName(), getValue(property));
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
            p.store(os, null);
        } 
        catch (IOException ex) {
        } 
        finally {
            try {
                os.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public String getValue(FormatterProperties property){
        return values.get(property);
    }
    
    public void setValue(FormatterProperties property, String value) {
        values.put(property, value);
    }
}