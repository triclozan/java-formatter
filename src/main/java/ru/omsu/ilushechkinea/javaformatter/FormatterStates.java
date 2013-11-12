/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.omsu.ilushechkinea.javaformatter;

/**
 * Enumeration of finite automaton (kernel of the formatter) states
 * @author ilushechkinea
 */
public enum FormatterStates {
    STRING_START("[Start of a line]"), 
    STRING_END("[String ending]"),
    WS_SEQ("[Sequence of whitespace symbols]"),
    COMMENT("[One-line comment]"),
    EXT_COMMENT("[Multiline comment]"),
    EXT_COMMENT_STAR("[Met star in multiline comment]"),
    SYMBOLIC_LITERAL("[Symbolic literal]"),
    STRING_LITERAL("[String literal]"),
    ESC_SYMBOLIC_LITERAL("[Escape symbol in symbolic literal]"),
    ESC_STRING_LITERAL("[Escape symbol in string literal]"),
    /*PLUS("[Met plus symbol]"),
    MINUS("[Met minus symbol]"),
    INCREMENT("[Met increment operation]"),
    DECREMENT("[Met decrement operation]"),
    SLASH("[Met a slash]"),
    MULTIPLY("[Met multiplication operation]"),*/
    OPERATION("[Met operator sign]"),
    NORMAL("[Normal copying]"); 
    private String name;

    private FormatterStates(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }    
}
