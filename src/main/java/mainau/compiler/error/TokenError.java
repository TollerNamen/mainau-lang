package mainau.compiler.error;

import mainau.compiler.lexer.Token;

public class TokenError implements Error {
    private final ErrorType type;
    private final String message;
    private final Token token;
    private String lineSnippet, filePath;

    public TokenError(ErrorType type, String message, Token token) {
        this.type = type;
        this.message = message;
        this.token = token;
    }

    private String createPointer() {
        Token.Position position = token.position();
        return " ".repeat(position.indexInLine()) + "^".repeat(token.value().length());
    }

    public void setLineSnippet(String lineSnippet) {
        this.lineSnippet = lineSnippet;
    }

    public int getLineNumber() {
        return token.position().line();
    }

    @Override
    public ErrorType type() {
        return type;
    }

    @Override
    public String message() {
        return message;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String createMessage() {
        return "Error of type " + type.name() + " in File " + filePath + ":" +
                "\nMessage: " + message +
                "\n" + lineSnippet +
                "\n" + createPointer() +
                "\n";
    }
}
