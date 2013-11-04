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
 * Formatter implementation
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
    private String operation;
    private FormatterStates state;
    private FormatterStates prevState;
    private List<FormatterWarningInfo> warnings;
          
    final int BUFFER_SIZE = 4096;
    final String newLine = "\n";
    final String OPERATIONS = "+-*/!&|><=%~^";
    
    /**
     * Applies given settings to the formatter
     * @param settings Settings to apply
     */
    public final void setSettings(FormatterSettings settings) {
        this.settings = settings;
        applySettings();
    }
    
    /**
     * Default constructor, uses default settings
     */
    public Formatter() {
        setSettings(new FormatterSettings());
        warnings = new LinkedList<FormatterWarningInfo>();
    }
        
    /**
     * Constructor with settings to apply
     * @param settings 
     */
    public Formatter(FormatterSettings settings) {
        setSettings(settings);
        warnings = new LinkedList<FormatterWarningInfo>();
    }
      
    /**
     * Takes data from the input stream and writes the formatted data to the output stream 
     * @param input Input stream to be formatted
     * @param output Output stream receiving formatting result
     * @throws IOException 
     */
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
        prevState = FormatterStates.STRING_START;
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
                                outputWriter.write(formIndent(indent));
                                outputWriter.write(c);
                                moveToState(FormatterStates.END_STRING);
                            }
                            else {
                                outputWriter.write(formIndent(indent));
                                moveToState(FormatterStates.NORMAL);
                                ptr--;
                                column--;                             
                            }                           
                        }
                        break;
                    case END_STRING:
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
                    case STRING_LITERAL:
                        if (c == '\\') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.ESC_STRING_LITERAL);
                        }
                        else if (c == '"') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.NORMAL);
                        }
                        else {
                            outputWriter.write(c);
                        }
                        break;
                    case SYMBOLIC_LITERAL:
                        if (c == '\\') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.ESC_SYMBOLIC_LITERAL);
                        }
                        else if (c == '\'') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.NORMAL);
                        }
                        else {
                            outputWriter.write(c);
                        }
                        break;
                    case ESC_STRING_LITERAL:
                        outputWriter.write(c);
                        moveToState(FormatterStates.STRING_LITERAL);
                        break;
                    case ESC_SYMBOLIC_LITERAL:
                        outputWriter.write(c);
                        moveToState(FormatterStates.SYMBOLIC_LITERAL);
                        break;
                    case COMMENT:
                        if (c == '\n') {
                            outputWriter.write(newLine);
                            moveToState(FormatterStates.STRING_START);
                        }
                        else {
                            outputWriter.write(c);
                        }
                        break;
                    case EXT_COMMENT:
                        if (c == '*') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.EXT_COMMENT_STAR);
                        }
                        else {
                            outputWriter.write(c);
                        }
                        break;
                    case EXT_COMMENT_STAR:
                        if (c == '/') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.NORMAL);
                        }
                        else {
                            outputWriter.write(c);
                            moveToState(FormatterStates.EXT_COMMENT);
                        }
                        break;
                    case OPERATION:
                        if (operation.equals("/") && c == '/') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.COMMENT);
                        }
                        else if (operation.equals("/") && c == '*') {
                            outputWriter.write(c);
                            moveToState(FormatterStates.EXT_COMMENT);
                        }
                        else if (OPERATIONS.indexOf(c) >= 0) {
                            outputWriter.write(c);
                        }
                        else {
                            ptr--;
                            column--;
                            moveToState(FormatterStates.WS_SEQ);
                        }
                        break;
                    case WS_SEQ:
                        if (!Character.isWhitespace(c)) {
                            ptr--;
                            column--;
                            outputWriter.write(' ');
                            moveToState(FormatterStates.NORMAL);
                        }
                        else {
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
                                ptr--;
                                column--;
                                outputWriter.write(newLine);
                                moveToState(FormatterStates.STRING_START);
                            }
                            else if (c == '{') {
                                outputWriter.write(c);
                                increaseIndent();
                                moveToState(FormatterStates.END_STRING);
                            }
                            else if (c == '\'') {
                                outputWriter.write(c);
                                moveToState(FormatterStates.SYMBOLIC_LITERAL);
                            }
                            else if (c == '\"') {
                                outputWriter.write(c);
                                moveToState(FormatterStates.STRING_LITERAL);
                            }
                            else if (c == ';') {
                                outputWriter.write(c);
                                moveToState(FormatterStates.END_STRING);
                            }
                            else if (OPERATIONS.indexOf(c) >= 0) {
                                if (prevState != FormatterStates.WS_SEQ) {
                                    outputWriter.write(' ');
                                }
                                outputWriter.write(c);
                                moveToState(FormatterStates.OPERATION);
                                operation = "" + c;
                            }
                            else if (Character.isWhitespace(c)) {
                                moveToState(FormatterStates.WS_SEQ);
                                operation = "" + c;
                            }
                            else {
                                prevState = FormatterStates.NORMAL;
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
  
    /**
     * 
     * @return List of warnings generated during latest formatting operation
     */
    public List<FormatterWarningInfo> getWarnings() {
        return warnings;
    }    
    
    /**
     * Adds new warning to the warning list
     * @param warning The new warning 
     * @param count Actual number of warnings it describes
     */
    private void addWarning(FormatterWarnings warning, int count) {
        warnings.add(new FormatterWarningInfo(warning, count, -1, -1));
    }    
    
    /**
     * Increases current nesting level
     */
    private void increaseIndent() {
        indent++;
        log.trace("indent increased to " + Integer.toString(indent));
    }
    
    /**
     * Decreases current nesting level
     */
    private void decreaseIndent() {
        indent--;
        if (indent < 0) {
            indent = 0;
            rightBraceMismatches++;
        }
        log.trace("indent decreased to " + Integer.toString(indent));
    }    

    /**
     * Puts finite automaton in a new state
     * @param state New state of the finite automaton
     */
    private void moveToState(FormatterStates state) {
        prevState = this.state;
        this.state = state;
        log.trace("state changed to " + state.getName());
    }    
    
    /**
     * Processes current settings to initialize some class fields
     */
    private void applySettings() {
        indentSymbol = settings.getValue(FormatterProperties.INDENT_SYMBOL);
        indentSize = Integer.valueOf(settings.getValue(FormatterProperties.INDENT_SIZE));
        log.setLevel(Level.toLevel(settings.getValue(FormatterProperties.LOGGING_LEVEL)));
    }
    
    /**
     * Auxilary method creating indentation string based on block nesting level
     * @param n The block nesting level (number of primitive indents)
     * @return The indentation string
     */
    private String formIndent(int n) {
        return new String(new char[n * indentSize]).replace("\0", indentSymbol);
    }
}