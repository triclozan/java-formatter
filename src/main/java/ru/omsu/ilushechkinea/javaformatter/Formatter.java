/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.ilushechkinea.javaformatter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ilushechkinea
 */
public class Formatter {
    private Logger log = Logger.getLogger(Formatter.class);
    
    private InputStreamReader input;
    private OutputStreamWriter output;
    private FormatterSettings settings;
    private int indentSize;
    private String indentSymbol;
    private int indent;
    private int rightBraceMismatches;
    private int row;
    private int column;
    private FormatterStates state;
    private List<FormatterWarningInfo> warnings;
    
    final String PTN_COMMENT = "(//.*)$";
    final String PTN_LARGE_COMMENT = "/*[^*]**/$";
    final String PTN_ID = "[]";
    
    final String WRN_LEFT_BRACE = "Left brace mismatch";
    final String WRN_RIGHT_BRACE = "Right brace mismatch";
    
    final int BUFFER_SIZE = 4096;
    final String newLine = "\n";
    
    public final void setSettings(FormatterSettings settings) {
        this.settings = settings;
        applySettings();
    }
    
    public Formatter() {
        setSettings(new FormatterSettings());
        warnings = new LinkedList<FormatterWarningInfo>();
    }
        
    public Formatter(FormatterSettings settings) {
        setSettings(settings);
        warnings = new LinkedList<FormatterWarningInfo>();
    }
      
    public void format(InputStream input, OutputStream output) throws IOException {
        log.debug("started formatting");
        
        InputStreamReader inputReader = new InputStreamReader(input);        
        OutputStreamWriter outputWriter = new OutputStreamWriter(output);
        
        rightBraceMismatches = 0;
        warnings.clear();

        indent = 0;
        row = 0;
        column = 0;
        state = FormatterStates.STRING_START;
        char[] buffer = new char[BUFFER_SIZE];
        int bytesRead = 0;
        
        while ((bytesRead = inputReader.read(buffer)) != -1) {
            int ptr = 0;
            while (ptr < bytesRead) {
                char c = buffer[ptr];
                log.trace("processing char " + c + " in state " + state.getName());
                switch(state) {
                    case STRING_START:
                        if (c == '\n') {
                            outputWriter.write(formIndent(indent));
                            outputWriter.write(newLine);
                            row++;
                        }
                        else if (!Character.isWhitespace(c)) {
                            if (c == '}') {
                                decreaseIndent();
                            }
                            
                            outputWriter.write(formIndent(indent));
                            outputWriter.write(c);                            
                            
                            if (c == '{') {
                                increaseIndent();
                            }
                            
                            if (c == '{' || c == '}') {
                                moveToState(FormatterStates.AFTER_BRACE);  
                            }
                            else {
                                moveToState(FormatterStates.NORMAL);    
                            }
                        }
                        break;
                    case AFTER_BRACE:
                        if (c == '\n') {
                            outputWriter.write(newLine);
                            moveToState(FormatterStates.STRING_START);
                            row++;
                        }
                        else if (!Character.isWhitespace(c)) {
                            outputWriter.write(newLine);
                            moveToState(FormatterStates.STRING_START);
                            ptr--;
                            column--;
                        }
                        break;
                    case NORMAL:
                        if (c == '\n') {
                            outputWriter.write(newLine);
                            moveToState(FormatterStates.STRING_START);
                            row++;
                        }
                        else {
                            if (c == '}') {
                                outputWriter.write(newLine);
                                moveToState(FormatterStates.STRING_START);
                                ptr--;
                                column--;
                            }
                            else if (c == '{') {
                                outputWriter.write(c);
                                increaseIndent();
                                moveToState(FormatterStates.AFTER_BRACE);
                            }
                            else {
                                outputWriter.write(c);
                            }
                        }                        
                        break;
                }
                ptr++;
                column++;
            }
        } 
        outputWriter.flush();
        
        if (rightBraceMismatches > 0) {
            addWarning(FormatterWarnings.WRN_RIGHT_BRACE, rightBraceMismatches);
        }
        if (indent > 0) {
            addWarning(FormatterWarnings.WRN_LEFT_BRACE, indent);
        }
        log.debug("finished formatting");
    }  
  
    public List<FormatterWarningInfo> getWarnings() {
        return warnings;
    }    
    
    private void addWarning(FormatterWarnings warning, int count) {
        warnings.add(new FormatterWarningInfo(warning, count, -1, -1));
    }    
    
    private void increaseIndent() {
        indent++;
        log.trace("indent increased to " + Integer.toString(indent));
    }

    private void decreaseIndent() {
        indent--;
        if (indent < 0) {
            indent = 0;
            rightBraceMismatches++;
        }
        log.trace("indent decreased to " + Integer.toString(indent));
    }    

    private void moveToState(FormatterStates state) {
        this.state = state;
        log.trace("state changed to " + state.getName());
    }    
    
    private void applySettings() {
        indentSymbol = settings.getValue(FormatterProperties.INDENT_SYMBOL);
        indentSize = Integer.valueOf(settings.getValue(FormatterProperties.INDENT_SIZE));
        log.setLevel(Level.toLevel(settings.getValue(FormatterProperties.LOGGING_LEVEL)));
    }
    
    private String formIndent(int n) {
        return new String(new char[n * indentSize]).replace("\0", indentSymbol);
    }
}