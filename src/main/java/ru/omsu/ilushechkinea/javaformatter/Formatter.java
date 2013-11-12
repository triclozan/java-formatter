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
import ru.omsu.ilushechkinea.javaformatter.exceptions.FormattingException;
import ru.omsu.ilushechkinea.javaformatter.exceptions.InvalidStreamException;

/**
 * Formatter implementation
 * @author ilushechkinea
 */
public class Formatter implements IFormatter {
    private Logger log = Logger.getLogger(Formatter.class);

    /** Settings of the formatter */
    private FormatterSettings settings;

    /** Size of indent */
    private int indentSize;

    /** Symbol used for indentation */
    private String indentSymbol;

    /** Number of right brace mismatches found */
    private int rightBraceMismatches;

    /** List of generated warnings */
    private List<FormatterWarningInfo> warnings;
    
    /** Current nesting level */
    private int indent;

    /** Additional buffer storing current operation sign string */
    private String operation;

    /** Current formatter state */
    private FormatterStates state;

    /** Previous formatter state */
    private FormatterStates prevState;

    /** Size of read buffer */
    final int BUFFER_SIZE = 4096;

    /** Line break string */
    final String newLine = "\n";

    /** String containing operation signs */
    final String OPERATIONS = "+-*/!&|><=%~^";

    /**
     * Applies given settings to the formatter
     * @param settings Settings to apply
     */
    @Override
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
     * @param settings FormatterSettings object containing settings for the formatter
     */
    public Formatter(FormatterSettings settings) {
        setSettings(settings);
        warnings = new LinkedList<FormatterWarningInfo>();
    }
      
    /**
     * Takes data from the input stream and writes the formatted data to the output stream 
     * @param input Input stream to be formatted
     * @param output Output stream receiving formatting result
     * @throws InvalidStreamException, FormattingException 
     */
    @Override
    public void format(InputStream input, OutputStream output) 
           throws InvalidStreamException, FormattingException {
        log.debug("started formatting");

        // Checks source, destination, initializes readers
        if (input == null) {
            throw new InvalidStreamException("Null input stream");
        }

        if (output == null) {
            throw new InvalidStreamException("Null output stream");
        }

        InputStreamReader inputReader = new InputStreamReader(input);
        OutputStreamWriter outputWriter = new OutputStreamWriter(output);
        
        // Reset of member fields
        warnings.clear();
        rightBraceMismatches = 0;
        indent = 0;
        int parenthesisLevel = 0;
        int row = 0;
        int column = 0;
        state = FormatterStates.STRING_START;
        prevState = FormatterStates.STRING_START;
        char prevChar = '\0';

        // Additional required variables
        boolean goNext;
        int bytesRead;
        char[] buffer = new char[BUFFER_SIZE];

        try {
            // Finite automaton run
            while ((bytesRead = inputReader.read(buffer)) != -1) {
                int ptr = 0;
                while (ptr < bytesRead) {
                    char c = buffer[ptr];
                    goNext = true;
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
                                    moveToState(FormatterStates.STRING_END);
                                }
                                else {
                                    outputWriter.write(formIndent(indent));
                                    moveToState(FormatterStates.NORMAL);
                                    goNext = false;                         
                                }                           
                            }
                            break;
                        case STRING_END:
                            if (c == '\n') {
                                outputWriter.write(newLine);
                                moveToState(FormatterStates.STRING_START);
                                row++;
                            }
                            else if (!Character.isWhitespace(c)) {
                                outputWriter.write(newLine);
                                moveToState(FormatterStates.STRING_START);
                                goNext = false;   
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
                                goNext = false;   
                                moveToState(FormatterStates.WS_SEQ);
                            }
                            break;
                        case WS_SEQ:
                            if (!Character.isWhitespace(c)) {
                                goNext = false;   
                                outputWriter.write(' ');
                                moveToState(FormatterStates.NORMAL);
                            }
                            else {
                                if (c == '\n') {
                                    moveToState(FormatterStates.STRING_START);
                                    outputWriter.write(newLine);
                                }
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
                                    goNext = false;   
                                    outputWriter.write(newLine);
                                    moveToState(FormatterStates.STRING_START);
                                }
                                else if (c == '{') {
                                    outputWriter.write(c);
                                    increaseIndent();
                                    moveToState(FormatterStates.STRING_END);
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
                                    if (parenthesisLevel > 0) {
                                        moveToState(FormatterStates.WS_SEQ);
                                    }
                                    else {
                                        moveToState(FormatterStates.STRING_END);
                                    }
                                }
                                else if (OPERATIONS.indexOf(c) >= 0) {
                                    if (c == '*' && prevChar == '.') {
                                        outputWriter.write(c);
                                    }
                                    else {
                                        if (prevState != FormatterStates.WS_SEQ && prevState != FormatterStates.STRING_START) {
                                            outputWriter.write(' ');
                                        }
                                        outputWriter.write(c);
                                        moveToState(FormatterStates.OPERATION);
                                        operation = "" + c;
                                    }
                                }
                                else if (Character.isWhitespace(c)) {
                                    moveToState(FormatterStates.WS_SEQ);
                                    operation = "" + c;
                                }
                                else {
                                    if (c == '(') {
                                        parenthesisLevel++;
                                    }
                                    if (c == ')') {
                                        parenthesisLevel--;
                                    }
                                    prevState = FormatterStates.NORMAL;
                                    outputWriter.write(c);
                                }
                            }                        
                            break;
                    }
                    if (goNext) {
                        ptr++;
                        column++;
                        prevChar = c;
                    }
                }
            }
            // Save changes to destination
            outputWriter.flush();
        }        
        catch(IOException e) {
            throw new FormattingException("Error occurred while reading from stream or writing to stream");
        }
        catch(Exception e) {
             throw new FormattingException("Unexpected formatting error occurred");
        }
        
        // Add warning for right brace mismatches
        if (rightBraceMismatches > 0) {
            addWarning(FormatterWarnings.WRN_RIGHT_BRACE, rightBraceMismatches);
        }

        // Add warning for left brace mismatches
        if (indent > 0) {
            addWarning(FormatterWarnings.WRN_LEFT_BRACE, indent);
        }

        log.debug("finished formatting");
    }  
  
    /**
     * 
     * @return List of warnings generated during latest formatting operation
     */
    @Override
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
     * Auxiliary method creating indentation string based on block nesting level
     * @param n The block nesting level (number of primitive indents)
     * @return The indentation string
     */
    private String formIndent(int n) {
        return new String(new char[n * indentSize]).replace("\0", indentSymbol);
    }
}