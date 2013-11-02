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
    String mockString;
    ByteArrayOutputStream mockBaos;
    /*
     * Data initialization
     */
    @Before
    public void setUp() {
        mockString = "mock";
        mockBaos = new ByteArrayOutputStream();
    }
    
    @Test(expected=IOException.class)
    @Ignore
    public void testWrongInput() {        
        //Formatter f = new Formatter(null, mockBaos);        
    }
    
    @Test
    public void testCorrectCases() 
            throws UnsupportedEncodingException, IOException 
    {
        String test1 = "qwe {int i=0; for (asdad) {sdf}}";
        String res1 = "qwe {\n" +
            "    int i=0; for (asdad) {\n" +
            "        sdf\n" +
            "    }\n" +
            "}";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Formatter f = new Formatter();
        f.format(new StringInputStream(test1), baos);
        assertEquals(baos.toString(), res1);

        String test2 = "qwe "
                + "{int i=0; for (asdad) "
                + "{sdf}"
                + "}"; 
        baos.reset();
        f.format(new StringInputStream(test2), baos);
        assertEquals(baos.toString(), res1);
        
        String test3 = "{}}{{"; 
        baos.reset();
        f.format(new StringInputStream(test3), baos);
        FormatterWarningInfo wi = new FormatterWarningInfo(FormatterWarnings.WRN_LEFT_BRACE, 2, -1, -1);
        FormatterWarningInfo wi2 = new FormatterWarningInfo(FormatterWarnings.WRN_RIGHT_BRACE, 1, -1, -1);
        assertEquals(f.getWarnings().contains(wi), true);
        assertEquals(f.getWarnings().contains(wi2), true);
        assertEquals(f.getWarnings().size(), 2);
    }
}
