package mainau.compiler.lexer;

import java.util.Arrays;
import java.util.Set;

public enum TokenType {
    // Import
    OBTAIN,

    // Type Declarations
    CLASS(true), INTERFACE(true), RECORD(true), ANNOTATION(true), ENUM(true),

    // Branching
    IF(true), ELSE(true),
    SWITCH(true),

    // Loops
    FOR(true), WHILE(true), DO(true),

    // Primitive Types
    LONG(true), INT(true), CHAR(true), SHORT(true), BYTE(true), BOOLEAN(true),
    DOUBLE(true), FLOAT(true),

    VAR(true),

    // Attribute Modifier
    FINAL(true),
    STATIC(true),
    PUBLIC(true), PRIVATE(true), PROTECTED(true),
    DEFAULT(true),

    // Value Stuff
    ASSIGN,
    BINARY_OPERATOR,
    BINARY_ASSIGN,
    NUMBER_VALUE,
    IDENTIFIER,
    STRING,
    CHARACTER,

    NULL(true),
    TRUE(true), FALSE(true),

    // Syntax Symbols
    OPEN_PAREN, CLOSE_PAREN,
    OPEN_BRACE, CLOSE_BRACE,
    OPEN_BRACKET, CLOSE_BRACKET,
    SEMI, COMMA, COLON, DOT,

    // Repl Stuff
    PRINT(true, true),
    INPUT(true, true),

    EOF;

    private boolean isKeyword = false;
    private boolean isReplOnly = false;

    TokenType(boolean isKeyword, boolean isReplOnly) {
        this.isKeyword = isKeyword;
        this.isReplOnly = isReplOnly;
    }

    TokenType(boolean isKeyword) {
        this.isKeyword = isKeyword;
    }

    TokenType() {
    }

    public boolean isKeyword() {
        return isKeyword;
    }

    public boolean isReplOnly() {
        return isReplOnly;
    }

    public String nameToLowerCase() {
        return name().replace("_", "-").toLowerCase();
    }

    public static TokenType fromLowerCaseName(String name) {
        return TokenType.valueOf(name.replace("-", "_").toUpperCase());
    }

    public static Set<TokenType> getKeywordTypes() {
        return Set.of(LONG, INT, CHAR, SHORT, BYTE, BOOLEAN, DOUBLE, FLOAT, VAR);
    }

    public static Set<TokenType> getAttributeModifiers() {
        return Set.of(FINAL, PUBLIC, PRIVATE, PROTECTED, STATIC);
    }

    public static TokenType[] getKeywords() {
        return Arrays.stream(values()).filter(tokenType -> tokenType.isKeyword).toArray(TokenType[]::new);
    }

    public static TokenType[] getKeywordsExcludingRepl() {
        return Arrays.stream(values()).filter(tokenType -> tokenType.isKeyword && !tokenType.isReplOnly).toArray(TokenType[]::new);
    }

    public static TokenType[] valuesWithoutRepl() {
        return Arrays.stream(values()).filter(tokenType -> !tokenType.isReplOnly).toArray(TokenType[]::new);
    }
}
