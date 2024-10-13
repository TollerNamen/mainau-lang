package mainau.compiler.parser;

import mainau.compiler.lexer.TokenType;

import java.util.Arrays;
import java.util.Set;

public interface ASTImpl extends AST {
    class Statement implements AST.Statement {
        @Override public NodeType type() { return NodeType.GENERIC_STATEMENT; }
    }
    class Program extends Statement implements AST.Program {
        private final ASTImpl.Statement[] body;

        public Program(Statement[] body) {
            this.body = body;
        }

        @Override public NodeType type() { return NodeType.PROGRAM; }
        @Override public AST.Statement[] body() { return body; }
        @Override public String toString() { return "Program(body=" + Arrays.toString(body()) + ")"; }
    }

    class VariableDeclarationStatement extends Statement implements AST.VariableDeclarationStatement {
        private final String identifierSymbol;
        private final ASTImpl.Expression variableType, value;
        private final Set<TokenType> modifiers;

        public VariableDeclarationStatement(
                ASTImpl.Expression variableType,
                String identifierSymbol,
                Expression value, Set<TokenType> modifiers
        ) {
            this.variableType = variableType;
            this.identifierSymbol = identifierSymbol;
            this.value = value;
            this.modifiers = modifiers;
        }

        @Override public NodeType type() { return NodeType.VARIABLE_DECLARATION; }
        @Override public AST.Expression variableType() { return variableType; }
        @Override public String identifierSymbol() { return identifierSymbol; }
        @Override public AST.Expression value() { return value; }

        @Override
        public Set<TokenType> modifiers() {
            return modifiers;
        }

        @Override public String toString() {
            return "VariableDeclarationStatement(" +
                    "variableType=" + variableType +
                    ",identifierSymbol=" + identifierSymbol +
                    ",value=" + value +
                    ")";
        }
    }
    class AssignmentStatement extends Statement implements AST.AssignmentStatement {
        private final ASTImpl.Expression variable;
        private final ASTImpl.Expression value;

        public AssignmentStatement(Expression target, Expression value) {
            this.variable = target;
            this.value = value;
        }

        @Override public AST.Expression variable() { return variable; }
        @Override public AST.Expression value() { return value; }
    }
    class FunctionDeclarationStatement extends Statement implements AST.FunctionDeclarationStatement {
        final private String returnType, identifierSymbol;
        final private AST.Statement[] body;
        final private Set<TokenType> modifiers;
        final private Set<FunctionArgumentExpression> arguments;

        public FunctionDeclarationStatement(String returnType, String identifierSymbol, AST.Statement[] body, Set<TokenType> modifiers, Set<FunctionArgumentExpression> arguments) {
            this.returnType = returnType;
            this.identifierSymbol = identifierSymbol;
            this.body = body;
            this.modifiers = modifiers;
            this.arguments = arguments;
        }

        @Override public String returnType() { return returnType; }
        @Override public String identifierSymbol() { return identifierSymbol; }
        @Override public AST.Statement[] body() { return body; }
        @Override public Set<TokenType> modifiers() { return modifiers; }
        @Override public Set<FunctionArgumentExpression> arguments() { return arguments; }
    }
    class FunctionArgumentExpression extends Expression implements AST.FunctionDeclarationStatement.FunctionArgumentExpression {
        final private String returnType, identifierSymbol;
        final private boolean isFinal;

        public FunctionArgumentExpression(String returnType, String identifierSymbol, boolean isFinal) {
            this.returnType = returnType;
            this.identifierSymbol = identifierSymbol;
            this.isFinal = isFinal;
        }

        @Override public String variableType() { return returnType; }
        @Override public String identifierSymbol() { return identifierSymbol; }
        @Override public boolean isFinal() { return isFinal; }
    }

    class FunctionInvocationStatement extends Statement implements AST.FunctionInvocationStatement {
        private final ASTImpl.IdentifierLiteralExpression identifier;
        private final ASTImpl.Expression[] arguments;

        public FunctionInvocationStatement(IdentifierLiteralExpression identifier, AST.Expression[] arguments) {
            this.identifier = identifier;
            this.arguments = (Expression[]) arguments;
        }
        public FunctionInvocationStatement(IdentifierLiteralExpression identifier) {
            this.identifier = identifier;
            this.arguments = new Expression[0];
        }

        @Override public NodeType type() { return NodeType.FUNCTION_INVOCATION; }
        @Override public AST.IdentifierLiteralExpression identifier() { return identifier; }
        @Override public AST.Expression[] arguments() { return arguments; }
    }

    class Expression extends Statement implements AST.Expression {}

    class BinaryExpression extends Expression implements AST.BinaryExpression {
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
    class LiteralExpression extends Expression implements AST.LiteralExpression {}

    class IdentifierLiteralExpression extends LiteralExpression implements AST.IdentifierLiteralExpression {
        private final String symbol;
        private final String[] path;

        public IdentifierLiteralExpression(String symbol, String[] path) {
            this.symbol = symbol;
            this.path = path;
        }
        public IdentifierLiteralExpression(String symbol) {
            this.symbol = symbol;
            this.path = new String[0];
        }

        @Override public NodeType type() { return NodeType.IDENTIFIER; }
        @Override public String[] path() { return path; }
        @Override public String symbol() { return symbol; }
        @Override public String toString() { return "IdentifierLiteralExpression(symbol=" + symbol + ")"; }
    }
    class NumericLiteralExpression extends LiteralExpression implements AST.NumericLiteralExpression {
        private final float value;

        public NumericLiteralExpression(float value) {
            this.value = value;
        }

        @Override public NodeType type() { return NodeType.NUMERIC_LITERAL; }
        @Override public float value() { return value; }
        @Override public String toString() { return "NumericLiteralExpression(value=" + value + ")"; }
    }
    class StringLiteralExpression extends LiteralExpression implements AST.StringLiteralExpression {
        private final String value;

        public StringLiteralExpression(String value) {
            this.value = value;
        }

        @Override public NodeType type() { return NodeType.STRING_LITERAL; }
        @Override public String value() { return value; }
        @Override public String toString() { return "StringLiteralExpression(value=\"" + value + "\")"; }
    }
    class CharacterLiteralExpression extends LiteralExpression implements AST.CharacterLiteralExpression {
        private final char value;

        public CharacterLiteralExpression(char value) {
            this.value = value;
        }

        @Override public NodeType type() { return NodeType.CHAR_LITERAL; }
        @Override public char value() { return value; }
        @Override public String toString() { return "CharacterLiteralExpression(value='" + value + "')"; }
    }
    class NullLiteralExpression extends LiteralExpression implements AST.NullLiteralExpression {
        @Override public NodeType type() { return NodeType.NULL_LITERAL; }
    }
}
