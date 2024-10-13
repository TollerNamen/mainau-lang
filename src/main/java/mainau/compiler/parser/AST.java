package mainau.compiler.parser;

import mainau.compiler.lexer.TokenType;

import java.util.Set;

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
        Expression variableType();
        String identifierSymbol();
        Expression value();
        Set<TokenType> modifiers();
    }
    interface AssignmentStatement extends Statement {
        @Override default NodeType type() { return NodeType.ASSIGNMENT; }
        Expression variable();
        Expression value();
    }
    interface FunctionDeclarationStatement extends Statement {
        @Override default NodeType type() { return NodeType.FUNCTION_DECLARATION; }
        String returnType();
        String identifierSymbol();
        Statement[] body();
        Set<TokenType> modifiers();
        Set<FunctionArgumentExpression> arguments();

        interface FunctionArgumentExpression extends Expression {
            @Override default NodeType type() { return NodeType.FUNCTION_ARGUMENT; }
            String variableType();
            String identifierSymbol();
            boolean isFinal();
        }
    }
    interface FunctionInvocationStatement extends Statement {
        @Override default NodeType type() { return NodeType.FUNCTION_INVOCATION; }
        IdentifierLiteralExpression identifier();
        Expression[] arguments();
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
        String[] path();
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
