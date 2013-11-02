java-formatter
==============
Formatter of java source code created for educational purposes.

## Description
Besides a class for code formatting the project contains gui form that can be used to invoke the formatter for files and strings. When formatting a string source is replaced by the result, result of file formatting is put to another specified file. The form allows to load settings from .properties files, by default settings are read from <b>formatter.properties</b> lying near jar executable. Logs are put in the file <b>logs/application.log</b>.

## Recognizable settings
<b>formatter.indent.symbol</b> - symbol (or string) used for indentation
<b>formatter.indent.size</b> - number of indentation symbols per nesting level
<b>formatter.logging.level</b> - level of logging (can be TRACE, DEBUG, INFO, WARN, ERROR, FATAL)

## Current abilities
1. Inserts new line symbol after opening and closing braces (if there is no one)
2. Makes indentation based on block's nesting level
3. Finds brace mismatch and logs it
4. Detailed trace of string processing (including underlying finite automaton transitions) if needed

## Known limitations
1. Can't deal with braces inside string or symbol literals
2. Don't preserve comments on the same line as brace

## License
Copyright (c) 2013 Eugene Ilushechkin
Licensed under the MIT license.