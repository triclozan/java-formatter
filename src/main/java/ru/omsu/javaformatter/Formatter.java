/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.javaformatter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.log4j.Logger;

/**
 *
 * @author ilushechkinea
 */
public class Formatter {
    static private Logger log = Logger.getLogger(Formatter.class);
    
    private InputStreamReader input;
    private OutputStreamWriter output;
    private FormatterSettings settings;
    private int indentSize;
    private String indentSymbol;
    private int indent;
    private States state;
    
    final String PTN_COMMENT = "(//.*)$";
    final String PTN_LARGE_COMMENT = "/*[^*]**/$";
    final String PTN_ID = "[]";
    
    final int BUFFER_SIZE = 4096;
    final String newLine = "\n";
    
    public final void setSettings(FormatterSettings settings) {
        this.settings = settings;
        applySettings();
    }
    
    public Formatter() {
        setSettings(new FormatterSettings());
    }
        
    public Formatter(FormatterSettings settings) {
        setSettings(settings);
    }
      
    public void format(InputStream input, OutputStream output) throws IOException {
        log.debug("started formatting");
        
        InputStreamReader inputReader = new InputStreamReader(input);        
        OutputStreamWriter outputWriter = new OutputStreamWriter(output);

        indent = 0;
        state = States.STRING_START;
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
                                moveToState(States.AFTER_BRACE);  
                            }
                            else {
                                moveToState(States.NORMAL);    
                            }
                        }
                        break;
                    case AFTER_BRACE:
                        if (c == '\n') {
                            outputWriter.write(newLine);
                            moveToState(States.STRING_START);
                        }
                        else if (!Character.isWhitespace(c)) {
                            outputWriter.write(newLine);
                            moveToState(States.STRING_START);
                            ptr--;
                        }
                        break;
                    case NORMAL:
                        if (c == '\n') {
                            outputWriter.write(newLine);
                            moveToState(States.STRING_START);
                        }
                        else {
                            if (c == '}') {
                                outputWriter.write(newLine);
                                moveToState(States.STRING_START);
                                ptr--;
                            }
                            else if (c == '{') {
                                outputWriter.write(c);
                                increaseIndent();
                                moveToState(States.AFTER_BRACE);
                            }
                            else {
                                outputWriter.write(c);
                            }
                        }                        
                        break;
                }
                ptr++;
            }
        } 
        outputWriter.flush();
        log.debug("finished formatting");
    }  
    
    private void increaseIndent() {
        indent++;
        log.trace("indent increased to " + Integer.toString(indent));
    }

    private void decreaseIndent() {
        indent--;
        log.trace("indent decreased to " + Integer.toString(indent));
    }    

    private void moveToState(States state) {
        this.state = state;
        log.trace("state changed to " + state.getName());
    }    
    
    private void applySettings() {
        indentSymbol = settings.getValue(FormatterProperties.INDENT_SYMBOL);
        indentSize = Integer.valueOf(settings.getValue(FormatterProperties.INDENT_SIZE));
    }
    
    private String formIndent(int n) {
        return new String(new char[n * indentSize]).replace("\0", indentSymbol);
    }
}

/*
В любой строке сначала пропускаются все незначащие(пробельные) символы, 
* затем если встречена закрывающая скобка, 
*   отступ уменьшается на 1, делается отступ, пропускаются все символы до значащего или конца строки, делается перевод строки, все заново
* иначе делается отступ, печатаются символы до перевода строки или открывающей скобки включительно
* если перевод строки, все заново
* иначе пропускаются все символы до значащего или конца строки, если конец строки, то 
* затем все символы печатаются вплоть до 
* 
* ТОКЕНЫ:
* 1. комментарий многострочный
* 2. комментарий однострочный
* 3. скобка открывающая
* 4. скобка закрывающая
* 5. перевод строки
* 6. значащая строчка
* 7. литерал строковый
* 8. литерал символьный
* 9. пробел (последовательность проблельных символов)
* 
* в начале строки пробелы вырезаются и заменяются на соотв. отступ
* перед откр. скобкой пробелы заменяются на один символ пробела
 */

/*
 комментарий, перевод, строчка, перевод, строчка, перевод, строчка, перевод, строчка, перевод, комментарий, перевод, строчка, скобка, перевод, строчка, перевод, строчка, перевод, перевод, 
 */