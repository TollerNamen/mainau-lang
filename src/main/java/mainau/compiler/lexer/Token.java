package mainau.compiler.lexer;

public record Token(String value, TokenType type, Position position) {
    public record Position(int index, int line, int indexInLine) {}
}
