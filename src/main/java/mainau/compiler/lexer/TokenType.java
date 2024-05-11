package mainau.compiler.lexer;

import java.util.Arrays;
import java.util.Set;

public enum TokenType {
    // Type Declarations
    CLASS(true), INTERFACE(true), RECORD(true), ANNOTATION(true), ENUM(true),

    // Primitive Types
    NUMBER_TYPE(true) {
        @Override public String nameToLowerCase() { return "number"; }
    },
    LONG(true), INT(true), CHAR(true), SHORT(true), BYTE(true), BOOLEAN(true),
    DOUBLE(true), FLOAT(true),
    VAR(true),

    // Attribute Modifier
    FINAL(true),
    PUBLIC(true), PRIVATE(true), PROTECTED(true),

    // Value Stuff
    ASSIGN,
    BINARY_OPERATOR,
    NUMBER_VALUE,
    IDENTIFIER,
    STRING,
    CHARACTER,
    NULL(true),

    // Syntax Symbols
    OPEN_PAREN,
    CLOSE_PAREN,
    SEMI,

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
    TokenType() {}

    public boolean isKeyword() {
        return isKeyword;
    }
    public boolean isReplOnly() {
        return isReplOnly;
    }

    public String nameToLowerCase() { return name().replace("_", "-").toLowerCase(); }
    public static TokenType fromLowerCaseName(String name) {
        if (name.equals("number")) return NUMBER_TYPE;
        return TokenType.valueOf(name.replace("-", "_").toUpperCase());
    }

    public static Set<TokenType> getKeywordTypes() { return Set.of(LONG, INT, CHAR, SHORT, BYTE, BOOLEAN, DOUBLE, FLOAT, VAR); }
    public static Set<TokenType> getAttributeModifiers() { return Set.of(FINAL, PUBLIC, PRIVATE, PROTECTED); }
    public static TokenType[] getKeywords() {
        return Arrays.stream(values()).filter(tokenType -> tokenType.isKeyword).toArray(TokenType[]::new);
    }
    public static TokenType[] getKeywordsExcludingRepl() {
        return Arrays.stream(values()).filter(tokenType -> tokenType.isKeyword && !tokenType.isReplOnly).toArray(TokenType[]::new);
    }
    public static TokenType[] valuesWithoutRepl() {
        return Arrays.stream(values()).filter(tokenType -> !tokenType.isReplOnly).toArray(TokenType[]::new);
    }

    public enum Tag {
        KEYWORD,
        REPL_ONLY
    }
}
