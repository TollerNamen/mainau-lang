package mainau.compiler.parser;

import java.util.Arrays;

public class ASTImpl implements AST {
    public static class Statement implements AST.Statement {
        @Override public NodeType type() { return NodeType.GENERIC_STATEMENT; }
    }
    public static class Program extends Statement implements AST.Program {
        private final ASTImpl.Statement[] body;

        public Program(Statement[] body) {
            this.body = body;
        }

        @Override public NodeType type() { return NodeType.PROGRAM; }
        @Override public AST.Statement[] body() { return body; }
        @Override public String toString() { return "Program(body=" + Arrays.toString(body()) + ")"; }
    }

    public static class VariableDeclarationStatement extends Statement implements AST.VariableDeclarationStatement {
        private final String variableType, identifierSymbol;
        private final boolean declaresFinal, assignsValue;
        private final ASTImpl.Expression value;

        public VariableDeclarationStatement(String variableType, String identifierSymbol, boolean declaresFinal, boolean assignsValue, Expression value) {
            this.variableType = variableType;
            this.identifierSymbol = identifierSymbol;
            this.declaresFinal = declaresFinal;
            this.assignsValue = assignsValue;
            this.value = value;
        }

        @Override public NodeType type() { return NodeType.VARIABLE_DECLARATION; }
        @Override public String variableType() { return variableType; }
        @Override public String identifierSymbol() { return identifierSymbol; }
        @Override public boolean declaresFinal() { return declaresFinal; }
        @Override public boolean assignsValue() { return assignsValue; }
        @Override public AST.Expression value() { return value; }
    }

    public static class Expression extends Statement implements AST.Expression {}

    public static class BinaryExpression extends Expression implements AST.BinaryExpression {
        private final AST.Expression left, right;
        private final String operator;

        public BinaryExpression(AST.Expression left, AST.Expression right, String operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override public NodeType type() { return NodeType.BINARY_EXPRESSION; }
        @Override public AST.Statement left() { return left; }
        @Override public AST.Statement right() { return right; }
        @Override public String operator() { return operator; }
        @Override public String toString() { return "BinaryExpression(left=" + left + ",right=" + right + ",operator=\"" + operator + "\")"; }
    }
    public static class LiteralExpression extends Expression implements AST.LiteralExpression {}

    public static class IdentifierLiteralExpression extends LiteralExpression implements AST.IdentifierLiteralExpression {
        private final String symbol;

        public IdentifierLiteralExpression(String symbol) {
            this.symbol = symbol;
        }

        @Override public NodeType type() { return NodeType.IDENTIFIER; }
        @Override public String symbol() { return symbol; }
        @Override public String toString() { return "IdentifierLiteralExpression(symbol=" + symbol + ")"; }
    }
    public static class NumericLiteralExpression extends LiteralExpression implements AST.NumericLiteralExpression {
        private final float value;

        public NumericLiteralExpression(float value) {
            this.value = value;
        }

        @Override public NodeType type() { return NodeType.NUMERIC_LITERAL; }
        @Override public float value() { return value; }
        @Override public String toString() { return "NumericLiteralExpression(value=" + value + ")"; }
    }
    public static class StringLiteralExpression extends LiteralExpression implements AST.StringLiteralExpression {
        private final String value;

        public StringLiteralExpression(String value) {
            this.value = value;
        }

        @Override public NodeType type() { return NodeType.STRING_LITERAL; }
        @Override public String value() { return value; }
        @Override public String toString() { return "StringLiteralExpression(value=\"" + value + "\")"; }
    }
    public static class CharacterLiteralExpression extends LiteralExpression implements AST.CharacterLiteralExpression {
        private final char value;

        public CharacterLiteralExpression(char value) {
            this.value = value;
        }

        @Override public NodeType type() { return NodeType.CHAR_LITERAL; }
        @Override public char value() { return value; }
        @Override public String toString() { return "CharacterLiteralExpression(value='" + value + "')"; }
    }
    public static class NullLiteralExpression extends LiteralExpression implements AST.NullLiteralExpression {
        @Override public NodeType type() { return NodeType.NULL_LITERAL; }
    }
}
