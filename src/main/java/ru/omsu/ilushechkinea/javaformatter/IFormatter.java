/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.omsu.ilushechkinea.javaformatter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import ru.omsu.ilushechkinea.javaformatter.exceptions.*;

/**
 *
 * @author ilushechkinea
 */
public interface IFormatter {

    /**
     * Takes data from the input stream and writes the formatted data to the output stream
     * @param input Input stream to be formatted
     * @param output Output stream receiving formatting result
     * @throws InvalidStreamException, FormattingException
     */
    void format(InputStream input, OutputStream output) throws InvalidStreamException, FormattingException;

    /**
     *
     * @return List of warnings generated during latest formatting operation
     */
    List<FormatterWarningInfo> getWarnings();

    /**
     * Applies given settings to the formatter
     * @param settings Settings to apply
     */
    void setSettings(FormatterSettings settings);

}
