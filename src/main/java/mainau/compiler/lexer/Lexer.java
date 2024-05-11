package mainau.compiler.lexer;

import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import java.util.Arrays;
import java.util.List;

public class Lexer {
    private final String[] source;
    private int line = 1, lastLineIndex = 0, index = 0;
    private Token currentToken;

    public final String WHITE_SPACE_REGEX = "\\s";
    public final String LINE_BREAK_REGEX = "\\R";
    private final List<String> KEYWORDS = Arrays.stream(TokenType.getKeywords())
            .map(TokenType::nameToLowerCase)
            .toList();

    public Lexer(String sourceCode) {
        if (sourceCode.isEmpty())
            throw new IllegalArgumentException("Cannot tokenize an empty String!");
        this.source = sourceCode.split("");
        final String message = "Successfully initialized module";
        if (sourceCode.split("\\R").length == 1) {
            new Output().setEraseLine(true).send(MessageType.DEBUG, message + " of " + Arrays.toString(source));
        }
        else new Output().send(MessageType.DEBUG, message + "!");
        currentToken = nextToken();
        shift();
    }

    private void shift() {
        index++;
        try {
            Output.simplyLog(MessageType.DEV, "+ old { " + (index - 1) + " - '" + source[index - 1] + "' } new { " + index + " - '" + source[index] + "' }");
        } catch (ArrayIndexOutOfBoundsException e) {
            Output.simplyLog(MessageType.ERROR, e.getMessage());
        }
    }
    private void unShift() {
        index -= 1;
        try {
            Output.simplyLog(MessageType.DEV, "- old { " + (index + 1) + " - '" + source[index + 1] + "' } new { " + index + " - '" + source[index] + "' }");
        } catch (ArrayIndexOutOfBoundsException e) {
            Output.simplyLog(MessageType.ERROR, e.getMessage());
        }
    }

    private boolean matches(String regex) {
        return source[index].matches(regex);
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
            value.append(source[index]);
            shift();
        } while (!checkEOF() && predicate.test());
        unShift();
        return new Token(value.toString(), type, createPosition(startIndex));
    }

    public Token token() {
        return currentToken;
    }
    public Token next() {
        //Output.simplyLog(MessageType.DEBUG, currentToken.toString());
        final Token previousToken = currentToken;
        currentToken = index <= source.length ? nextToken() : createEOF();
        Output.simplyLog(MessageType.DEV, previousToken.toString());
        shift();
        return previousToken;
    }

    private Token createEOF() {
        return new Token("EOF", TokenType.EOF, createPosition());
    }
    private boolean checkEOF() {
        return index >= source.length;
    }

    private Token nextToken() {



        if (checkEOF()) return createEOF();

        // Whitespace
        if (matches(LINE_BREAK_REGEX)) nextLine();
        if (matches(WHITE_SPACE_REGEX)) {
            shift();
            if (checkEOF()) return createEOF();
        }

        // Number
        if (matches("[0-9]")) {
            return createTokenAsLongAs(() -> matches("[.0-9a-zA-Z]"), TokenType.NUMBER_VALUE);
        }

        // Identifier
        if (matches("[_a-zA-Z]")) {
            final Token token = createTokenAsLongAs(() -> matches("[_a-zA-Z]"), TokenType.IDENTIFIER);
            // check reserved
            if (KEYWORDS.contains(token.value()))
                return new Token(token.value(), TokenType.fromLowerCaseName(token.value()), token.position());
            return token;
        }

        switch (source[index]) {

            // Binary
            case "+", "-", "*", "/", "%" -> {
                return new Token(source[index], TokenType.BINARY_OPERATOR, createPosition());
            }

            // String and Character
            case "\"" -> {
                final StringBuilder value = getLiteralValue("\"");
                return new Token(value.toString(), TokenType.STRING, createPosition());
            }
            case "'" -> {
                final StringBuilder value = getLiteralValue("'");
                return new Token(value.toString(), TokenType.CHARACTER, createPosition());
            }

            case "(" -> {
                return new Token(source[index], TokenType.OPEN_PAREN, createPosition());
            }
            case ")" -> {
                return new Token(source[index], TokenType.CLOSE_PAREN, createPosition());
            }
            case "=" -> {
                return new Token(source[index], TokenType.ASSIGN, createPosition());
            }
            case ";" -> {
                return new Token(source[index], TokenType.SEMI, createPosition());
            }

            // Unicode logic
            case "\\" -> {}

            // Could not tokenize
            default -> {
                System.err.println("Could not tokenize following char: {" + source[index] + "}");
                System.err.println("Exiting now, Goodbye!");
                System.exit(1);
            }
        }
        return null;
    }

    private StringBuilder getLiteralValue(String literalIdentifier) {
        final StringBuilder value = new StringBuilder();
        boolean isEscaped = false, inLiteral = true;
        shift();
        if (checkEOF()) return value;
        do {
            isEscaped = !isEscaped && source[index].equals("\\");
            if (source[index].equals(literalIdentifier) || matches(LINE_BREAK_REGEX) || checkEOF()) {
                inLiteral = false;
            }
            else if (isEscaped) {
                value.append(source[index]);
            }
            else {
                value.append(source[index]);
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
