package ru.omsu.ilushechkinea.javaformatter;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import ru.omsu.ilushechkinea.util.StringInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import ru.omsu.ilushechkinea.javaformatter.exceptions.*;

/**
 * Tests for JavaFormatter class
 * @author ilushechkinea
 */
public class JavaFormatterTest {
    private ByteArrayOutputStream mockBaos;
    private Formatter formatter;
    
    /*
     * Data initialization
     */
    @Before
    public void setUp() {
        mockBaos = new ByteArrayOutputStream();
        formatter = new Formatter();  
    }
    
    /*
     * Test for null input stream
     */    
    @Test(expected=InvalidStreamException.class)
    public void testNullInput() throws InvalidStreamException, 
                                       FormattingException {        
        formatter.format(null, mockBaos);
    }

    /*
     * Test for null output stream
     */   
    @Test(expected=InvalidStreamException.class)
    public void testNullOutput() throws InvalidStreamException, 
                                        FormattingException,
                                        UnsupportedEncodingException {        
        formatter.format(new StringInputStream("") , null);
    }   
    
    /*
     * Test for nonexistent file
     */    
    @Test(expected=FileNotFoundException.class)
    public void testNonexistentFile() throws InvalidStreamException, 
                                             FormattingException, 
                                             FileNotFoundException,
                                             UnsupportedEncodingException {        
        FileOutputStream out = new FileOutputStream("b//l//a//b//l//a//b//l//a");
        formatter.format(new StringInputStream(""), out);
    }   
    
    /*
     * Tests for correct input and output streams (but input content can be wrong itself)
     */     
    @Test
    public void testCorrectCases() 
             throws InvalidStreamException, 
             FormattingException,
             UnsupportedEncodingException {
        //Test for formatting of single string code
        String test1 = "qwe {int i=0; for (asdad) {sdf}}";
        String res1 = "qwe {\n" +
            "    int i = 0;\n" + 
            "    for (asdad) {\n" +
            "        sdf\n" +
            "    }\n" +
            "}";
        formatter.format(new StringInputStream(test1), mockBaos);
        assertEquals(mockBaos.toString(), res1);

        //Test for preserving newlines during formatting
        String test2 = "qwe "
                + "{int i = 0; for (asdad) "
                + "{sdf}"
                + "}"; 
        mockBaos.reset();
        formatter.format(new StringInputStream(test2), mockBaos);
        assertEquals(mockBaos.toString(), res1);
        
        //Test for empty string handling
        String test3 = ""; 
        String res2 = "";
        mockBaos.reset();
        formatter.format(new StringInputStream(test3), mockBaos);
        assertEquals(mockBaos.toString(), res2);
        
        //Test for string literals and operation signs handling
        String test4 = "{i =\"{}\";for(a =1){b =2+5;c =7}}";
        String res3 = "{\n" +
                    "    i = \"{}\";\n" +
                    "    for(a = 1){\n" +
                    "        b = 2 + 5;\n" +
                    "        c = 7\n" +
                    "    }\n" +
                    "}";
        mockBaos.reset();
        formatter.format(new StringInputStream(test4), mockBaos);
        assertEquals(mockBaos.toString(), res3);
        
        //Test for brace mismatch detection
        String test10 = "{}}{{"; 
        mockBaos.reset();
        formatter.format(new StringInputStream(test10), mockBaos);
        FormatterWarningInfo wi = new FormatterWarningInfo(FormatterWarnings.WRN_LEFT_BRACE, 2, -1, -1);
        FormatterWarningInfo wi2 = new FormatterWarningInfo(FormatterWarnings.WRN_RIGHT_BRACE, 1, -1, -1);
        assertEquals(formatter.getWarnings().contains(wi), true);
        assertEquals(formatter.getWarnings().contains(wi2), true);
        assertEquals(formatter.getWarnings().size(), 2);
    }
}
