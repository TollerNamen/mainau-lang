package mainau.repl.runtime;

import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import mainau.compiler.parser.AST;
import mainau.compiler.parser.ASTImpl;

public class Interpreter {
    public static ValuesImpl.RuntimeValue evaluate(final ASTImpl.Statement statement, final Session session) {
        if (statement == null)
            return null;
        return switch (statement.type()) {
            case NUMERIC_LITERAL -> new ValuesImpl.NumberValue(((ASTImpl.NumericLiteralExpression) statement).value());
            case NULL_LITERAL -> new ValuesImpl.NullValue();
            case BINARY_EXPRESSION -> evaluateBinaryExpression((ASTImpl.BinaryExpression) statement, session);
            case PROGRAM -> evaluateProgram((ASTImpl.Program) statement, session);
            case IDENTIFIER -> session.lookUpVariable(((ASTImpl.IdentifierLiteralExpression) statement).symbol());
            case VARIABLE_DECLARATION -> evaluateVariableDeclaration((ASTImpl.VariableDeclarationStatement) statement, session);
            default -> {
                Output.simplyLog(MessageType.FATAL,
                        "The following Statement type is not ready for interpretation: " + statement.type() +
                                "\nExiting now, Goodbye!");
                System.exit(1);
                yield null;
            }
        };
    }

    private static ValuesImpl.RuntimeValue evaluateProgram(final ASTImpl.Program program, final Session session) {
        ValuesImpl.RuntimeValue lastEvaluated = new ValuesImpl.NullValue();
        for (AST.Statement statement : program.body())
            lastEvaluated = evaluate((ASTImpl.Statement) statement, session);
        return lastEvaluated;
    }

    private static ValuesImpl.RuntimeValue evaluateVariableDeclaration(final ASTImpl.VariableDeclarationStatement statement, final Session session) {
        ValuesImpl.RuntimeValue value = evaluate((ASTImpl.Statement) statement.value(), session);
        return session.declareVariable(statement.identifierSymbol(), value);
    }

    private static ValuesImpl.RuntimeValue evaluateBinaryExpression(final ASTImpl.BinaryExpression expression, final Session session) {
        final ValuesImpl.RuntimeValue right = evaluate((ASTImpl.Statement) expression.right(), session);
        final ValuesImpl.RuntimeValue left = evaluate((ASTImpl.Statement) expression.left(), session);

        if (right == null || left == null || right.type() != ValueType.NUMBER || left.type() != ValueType.NUMBER)
            return new ValuesImpl.NullValue();
        final float rightValue = ((ValuesImpl.NumberValue) right).value();
        final float leftValue = ((ValuesImpl.NumberValue) left).value();

        return switch (expression.operator()) {
            case "+" -> new ValuesImpl.NumberValue(leftValue + rightValue);
            case "-" -> new ValuesImpl.NumberValue(leftValue - rightValue);
            case "*" -> new ValuesImpl.NumberValue(leftValue * rightValue);
            case "/" -> new ValuesImpl.NumberValue(leftValue / rightValue);
            case "%" -> new ValuesImpl.NumberValue(leftValue % rightValue);
            case "^" -> new ValuesImpl.NumberValue((float) Math.pow(leftValue, rightValue));
            default -> new ValuesImpl.NullValue();
        };
    }
}
