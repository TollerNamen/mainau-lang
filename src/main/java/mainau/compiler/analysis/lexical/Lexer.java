package mainau.compiler.analysis.lexical;

import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import java.util.Arrays;
import java.util.List;

import static mainau.compiler.analysis.lexical.TokenType.*;

public class Lexer {
    private final String source;
    private int line = 1, lastLineIndex = 0, index = 0;
    private Token currentToken;
    private char charValue;
    private String stringValue;

    public static final String WHITE_SPACE_REGEX = "\\s";
    public static final String LINE_BREAK_REGEX = "\\R";
    private static final List<String> LOWERCASE_KEYWORDS = Arrays.stream(TokenType.getKeywords())
            .map(TokenType::nameToLowerCase)
            .toList();

    public Lexer(String sourceCode) {
        if (sourceCode.isEmpty())
            throw new IllegalArgumentException("Cannot tokenize an empty String!");
        this.source = sourceCode;
        final String message = "Successfully initialized module";
        if (sourceCode.split("\\R").length == 1)
            new Output().setEraseLine(true).send(MessageType.DEBUG, message + " of " + Arrays.toString(source.toCharArray()));
        else new Output().send(MessageType.DEBUG, message + "!");
        updateValue();
        currentToken = nextToken();
        shift();
    }

    private void shift() {
        index++;
        updateValue();
        /*
        try {
            Output.simplyLog(MessageType.DEV, "shift | old { " + (index - 1) + " - '" + getStringValue(index - 1) +
                    "' } new { " + index + " - '" + stringValue + "' }");
        } catch (StringIndexOutOfBoundsException e) {
            Output.simplyLog(MessageType.ERROR, e.getMessage());
        }
         */
    }
    private void unShift() {
        index--;
        updateValue();
        /*
        try {
            Output.simplyLog(MessageType.DEV, "unShift | old { " + (index + 1) + " - '" + getStringValue(index + 1) +
                    "' } new { " + index + " - '" + stringValue + "' }");
        } catch (StringIndexOutOfBoundsException e) {
            Output.simplyLog(MessageType.ERROR, e.getMessage());
        }
         */
    }
    private void updateValue() {
        charValue = getCharValue(index);
        stringValue = getStringValue(index);
    }
    private char getCharValue(int index) {
        return checkEOF() ? 0 : source.charAt(index);
    }
    private String getStringValue(int index) {
        return checkEOF() ? null : String.valueOf(source.charAt(index));
    }

    private boolean matches(String regex) {
        return stringValue != null && stringValue.matches(regex);
    }

    private Token.Position createPosition() {
        return new Token.Position(index, line, index - lastLineIndex);
    }
    private Token.Position createPosition(int index) {
        return new Token.Position(index, line, index - lastLineIndex);
    }

    private Token createTokenAsLongAs(ZeroParamPredicate predicate, TokenType type) {
        final StringBuilder value = new StringBuilder();
        final int startIndex = index;
        do {
            value.append(charValue);
            shift();
            //Output.simplyLog(MessageType.DEV, "invoked shift");
        } while (!checkEOF() && predicate.test());
        unShift();
        //Output.simplyLog(MessageType.DEV, "invoked unShift");
        return new Token(value.toString(), type, createPosition(startIndex));
    }

    public Token token() {
        return currentToken;
    }
    public Token next() {
        //Output.simplyLog(MessageType.DEBUG, currentToken.toString());
        if (currentToken.type() == EOF)
            throw new IllegalStateException("EOF is already reached");
        final Token previousToken = currentToken;
        Output.simplyLog(MessageType.DEV, previousToken.toString());
        currentToken = checkEOF() ? createEOF() : nextToken();
        shift();
        return previousToken;
    }
    public Token peakNext() {
        return peakNext(1);
    }
    public Token peakNext(int steps) {
        for (int i = 0; i < steps; i++)
            shift();
        var token = checkEOF() ? createEOF() : nextToken();
        for (int i = 0; i < steps; i++)
            unShift();
        return token;
    }

    private Token createEOF() {
        return new Token("EOF", EOF, createPosition());
    }
    private boolean checkEOF() {
        return index >= source.length();
    }

    private Token nextToken() {

        if (checkEOF()) return createEOF();

        // Whitespace
        if (matches(LINE_BREAK_REGEX)) nextLine();
        while (matches(WHITE_SPACE_REGEX)) {
            shift();
            if (checkEOF()) return createEOF();
        }

        // Number
        if (matches("[0-9]")) {
            return createTokenAsLongAs(() -> matches("[.0-9a-zA-Z]"), NUMBER_VALUE);
        }

        // Identifier
        if (matches("[_a-zA-Z]")) {
            final Token token = createTokenAsLongAs(() -> matches("[_a-zA-Z0-9]"), IDENTIFIER);
            // check reserved
            if (LOWERCASE_KEYWORDS.contains(token.value()))
                return new Token(token.value(), TokenType.fromLowerCaseName(token.value()), token.position());
            return token;
        }

        return switch (charValue) {

            // String and Character
            case '"', '\'' -> new Token(getLiteralValue(charValue).toString(), STRING, createPosition());

            case '+', '-', '*', '/', '%' -> {
                final String operator = stringValue;
                shift();

                if (charValue == '=')
                    yield new Token(operator, BINARY_ASSIGN, createPosition());

                else if (operator.equals("-") && charValue == '>')
                    yield new Token("->", LAMBDA, createPosition());

                unShift();
                yield simpleTokenFromType(BINARY_OPERATOR);
            }
            case '{' -> simpleTokenFromType(OPEN_BRACE);
            case '}' -> simpleTokenFromType(CLOSE_BRACE);
            case '(' -> simpleTokenFromType(OPEN_PAREN);
            case ')' -> simpleTokenFromType(CLOSE_PAREN);
            case '[' -> simpleTokenFromType(OPEN_BRACKET);
            case ']' -> simpleTokenFromType(CLOSE_BRACKET);
            case '=' -> simpleTokenFromType(ASSIGN);
            case ';' -> simpleTokenFromType(SEMI);
            case ',' -> simpleTokenFromType(COMMA);

            // Unicode logic
            case '\\' -> null;

            // Could not tokenize
            default -> {
                Output.simplyLog(MessageType.FATAL, "Could not tokenize following char: {" + charValue + "}");
                Output.simplyLog(MessageType.FATAL,"Exiting now, Goodbye!");
                System.exit(1);
                yield null;
            }
        };
    }

    private Token simpleTokenFromType(TokenType type) {
        return new Token(stringValue, type, createPosition());
    }

    private StringBuilder getLiteralValue(char literalIdentifier) {
        final StringBuilder value = new StringBuilder();
        boolean isEscaped = false, inLiteral = true;
        shift();
        if (checkEOF()) return value;
        do {
            isEscaped = !isEscaped && charValue == '\\';
            if (charValue == literalIdentifier || matches(LINE_BREAK_REGEX) || checkEOF()) {
                inLiteral = false;
            }
            /*
            else if (isEscaped) {
            }
             */
            else {
                value.append(charValue);
            }
            shift();
        } while (inLiteral);
        unShift();
        return value;
    }

    private void nextLine() {
        line++;
        lastLineIndex = index;
        shift();
    }
    
    interface ZeroParamPredicate {
        boolean test();
    }
}
