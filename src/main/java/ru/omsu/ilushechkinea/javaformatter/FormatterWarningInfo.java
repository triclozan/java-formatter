/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.omsu.ilushechkinea.javaformatter;

/**
 *
 * @author ilushechkinea
 */
public class FormatterWarningInfo {
    private FormatterWarnings warning;
    private int count;
    private int row;
    private int column;
    
    public FormatterWarningInfo(FormatterWarnings warning, int count, int row,
                                int column) {
        this.warning = warning;
        this.column = column;
        this.row = row;
        this.count = count;
    }
    
    /**
     * @return the warning
     */
    public FormatterWarnings getWarning() {
        return warning;
    }

    /**
     * @param warning the warning to set
     */
    public void setWarning(FormatterWarnings warning) {
        this.warning = warning;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }
    
    /**
     * @return warning information as a single String
     */
    @Override
    public String toString() {
        return warning + 
               (count > 1 ? " x" + Integer.toString(count) : "") + 
               (row >= 0 && column >= 0 ? "[row: " + row + ", column: " + column + "]" : "");
    }

    /**
    * Overriden equals method to use in tests
    */
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof FormatterWarningInfo))return false;
        FormatterWarningInfo wi = (FormatterWarningInfo)other;
        if (wi.count == this.count && wi.warning.equals(this.warning)) {
            return true;
        }
        return false;
    }
}
