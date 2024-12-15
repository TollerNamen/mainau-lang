package mainau.compiler.ast;

import mainau.compiler.analysis.lexical.TokenType;

import java.util.Set;

public interface AST {
    interface Statement extends Visitable {
    }

    interface Program extends Statement {
        Statement[] body();
    }

    interface ObtainStatement extends Statement {
        AST.Expression[] targets();
        AST.Expression path();
        boolean computed();
    }

    interface VariableDeclarationStatement extends Statement {
        Expression variableType();
        String identifierSymbol();
        Expression value();
        Set<TokenType> modifiers();
    }
    interface FunctionDeclarationStatement extends Program {
        Expression returnType();
        String identifierSymbol();
        Set<TokenType> modifiers();
        Set<ParameterExpression> arguments();

        interface ParameterExpression extends Expression {
            Expression variableType();
            String identifierSymbol();
            boolean isFinal();
        }
        enum BodyType {
            BLOCK,
            STATEMENT
        }
    }

    interface Expression extends Statement {}

    interface AssignmentExpression extends Expression {
        Expression variable();
        Expression value();
    }
    interface MemberExpression extends Expression {
        Expression parent();
        Expression child();
        boolean computed();
    }
    interface FunctionInvocationExpression extends Expression {
        Expression identifier();
        Expression[] arguments();
    }
    interface BinaryExpression extends Expression {
        AST.Expression left();
        AST.Expression right();
        String operator();
    }

    interface LiteralExpression extends Expression {}

    interface IdentifierLiteralExpression extends LiteralExpression {
        String symbol();
    }
    interface NumericLiteralExpression extends LiteralExpression {
        float value();
    }
    interface StringLiteralExpression extends LiteralExpression {
        String value();
    }
    interface CharacterLiteralExpression extends LiteralExpression {
        char value();
    }
    interface NullLiteralExpression extends LiteralExpression {
    }
}
