package ru.omsu.ilushechkinea.javaformatter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import ru.omsu.ilushechkinea.javaformatter.Formatter;
import ru.omsu.ilushechkinea.javaformatter.FormatterWarningInfo;
import ru.omsu.ilushechkinea.javaformatter.FormatterWarnings;
import ru.omsu.ilushechkinea.util.StringInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unit test for simple App.
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
        Formatter formatter = new Formatter();  
    }
    
    /*
     * Test for null input stream
     */    
    @Test(expected=NullPointerException.class)
    public void testNullInput() throws UnsupportedEncodingException, IOException {        
        formatter.format(null, mockBaos);
    }

    /*
     * Test for null output stream
     */   
    @Test(expected=NullPointerException.class)
    public void testNullOutput() throws UnsupportedEncodingException, IOException {        
        formatter.format(new StringInputStream("") , null);
    }    

    /*
     * Tests for correct input and output streams (but input content can be wrong itself)
     */     
    @Test
    public void testCorrectCases() 
            throws UnsupportedEncodingException, IOException 
    {
        //Test for formatting of single string code
        String test1 = "qwe {int i=0; for (asdad) {sdf}}";
        String res1 = "qwe {\n" +
            "    int i=0; for (asdad) {\n" +
            "        sdf\n" +
            "    }\n" +
            "}";
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Formatter f = new Formatter();
        f.format(new StringInputStream(test1), mockBaos);
        assertEquals(mockBaos.toString(), res1);

        //Test for preserving newlines during formatting
        String test2 = "qwe "
                + "{int i=0; for (asdad) "
                + "{sdf}"
                + "}"; 
        mockBaos.reset();
        f.format(new StringInputStream(test2), mockBaos);
        assertEquals(mockBaos.toString(), res1);
        
        //Test for empty string handling
        String test3 = ""; 
        String res2 = "";
        mockBaos.reset();
        f.format(new StringInputStream(test3), mockBaos);
        assertEquals(mockBaos.toString(), res2);
        
        //Test for brace mismatch detection
        String test10 = "{}}{{"; 
        mockBaos.reset();
        f.format(new StringInputStream(test10), mockBaos);
        FormatterWarningInfo wi = new FormatterWarningInfo(FormatterWarnings.WRN_LEFT_BRACE, 2, -1, -1);
        FormatterWarningInfo wi2 = new FormatterWarningInfo(FormatterWarnings.WRN_RIGHT_BRACE, 1, -1, -1);
        assertEquals(f.getWarnings().contains(wi), true);
        assertEquals(f.getWarnings().contains(wi2), true);
        assertEquals(f.getWarnings().size(), 2);
    }
}
