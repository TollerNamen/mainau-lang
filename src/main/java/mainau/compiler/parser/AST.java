package mainau.compiler.parser;

public interface AST {
    interface Statement {
        NodeType type();
    }
    interface Program extends Statement {
        @Override default NodeType type() { return NodeType.PROGRAM; }
        Statement[] body();
    }

    interface VariableDeclarationStatement extends Statement {
        @Override default NodeType type() { return NodeType.VARIABLE_DECLARATION; }
        String variableType();
        String identifierSymbol();
        boolean declaresFinal();
        boolean assignsValue();
        Expression value();
    }

    interface Expression extends Statement {}

    interface BinaryExpression extends Expression {
        @Override default NodeType type() { return NodeType.BINARY_EXPRESSION; }
        AST.Statement left();
        AST.Statement right();
        String operator();
    }
    interface LiteralExpression extends Expression {}

    interface IdentifierLiteralExpression extends LiteralExpression {
        @Override default NodeType type() { return NodeType.IDENTIFIER; }
        String symbol();
    }
    interface NumericLiteralExpression extends LiteralExpression {
        @Override default NodeType type() { return NodeType.NUMERIC_LITERAL; }
        float value();
    }
    interface StringLiteralExpression extends LiteralExpression {
        @Override default NodeType type() { return NodeType.STRING_LITERAL; }
        String value();
    }
    interface CharacterLiteralExpression extends LiteralExpression {
        @Override default NodeType type() { return NodeType.CHAR_LITERAL; }
        char value();
    }
    interface NullLiteralExpression extends LiteralExpression {
        @Override default NodeType type() { return NodeType.NULL_LITERAL; }
    }
}
