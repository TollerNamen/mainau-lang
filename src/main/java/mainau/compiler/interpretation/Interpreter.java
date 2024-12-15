package mainau.compiler.interpretation;

import mainau.compiler.ast.AST;
import mainau.compiler.visitor.ASTVisitor;

import java.util.Arrays;

public class Interpreter implements ASTVisitor<Void, Void> {
    @Override
    public Void visitStatement(AST.Statement statement, Void unused) {
        return null;
    }

    @Override
    public Void visitProgram(AST.Program program, Void unused) {
        Arrays
                .stream(program.body())
                .forEachOrdered(statement -> statement.accept(this, unused));
        return null;
    }

    @Override
    public Void visitObtainStatement(AST.ObtainStatement statement, Void unused) {
        return null;
    }

    @Override
    public Void visitVariableDeclarationStatement(AST.VariableDeclarationStatement statement, Void unused) {
        return null;
    }

    @Override
    public Void visitFunctionDeclarationStatement(AST.FunctionDeclarationStatement statement, Void unused) {
        return null;
    }

    @Override
    public Void visitExpression(AST.Expression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitAssignmentExpression(AST.AssignmentExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitMemberExpression(AST.MemberExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitFunctionInvocationExpression(AST.FunctionInvocationExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitBinaryExpression(AST.BinaryExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitLiteralExpression(AST.LiteralExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitIdentifierLiteralExpression(AST.IdentifierLiteralExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitNumericLiteralExpression(AST.NumericLiteralExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitStringLiteralExpression(AST.StringLiteralExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitCharacterLiteralExpression(AST.CharacterLiteralExpression expression, Void unused) {
        return null;
    }

    @Override
    public Void visitNullLiteralExpression(AST.NullLiteralExpression expression, Void unused) {
        return null;
    }
}
