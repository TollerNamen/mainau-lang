package mainau.compiler.visitor;

import static mainau.compiler.ast.AST.*;

public interface ASTVisitor<R, P> {
    R visitStatement(Statement statement, P p);

    R visitProgram(Program program, P p);

    R visitObtainStatement(ObtainStatement statement, P p);

    R visitVariableDeclarationStatement(VariableDeclarationStatement statement, P p);
    R visitFunctionDeclarationStatement(FunctionDeclarationStatement statement, P p);

    R visitExpression(Expression expression, P p);

    R visitAssignmentExpression(AssignmentExpression expression, P p);
    R visitMemberExpression(MemberExpression expression, P p);
    R visitFunctionInvocationExpression(FunctionInvocationExpression expression, P p);
    R visitBinaryExpression(BinaryExpression expression, P p);

    R visitLiteralExpression(LiteralExpression expression, P p);

    R visitIdentifierLiteralExpression(IdentifierLiteralExpression expression, P p);
    R visitNumericLiteralExpression(NumericLiteralExpression expression, P p);
    R visitStringLiteralExpression(StringLiteralExpression expression, P p);
    R visitCharacterLiteralExpression(CharacterLiteralExpression expression, P p);
    R visitNullLiteralExpression(NullLiteralExpression expression, P p);
}
