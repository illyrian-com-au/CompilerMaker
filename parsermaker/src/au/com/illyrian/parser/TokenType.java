package au.com.illyrian.parser;

public enum TokenType {
    END,
    DELIMITER,
    OPERATOR,
    IDENTIFIER,
    RESERVED,
    NUMBER,
    DECIMAL,
    CHARACTER,
    STRING,
    COMMENT,
    ERROR;
}