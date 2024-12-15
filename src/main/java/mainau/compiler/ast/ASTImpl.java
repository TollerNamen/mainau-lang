package mainau.compiler.ast;

import mainau.compiler.analysis.lexical.TokenType;
import mainau.compiler.visitor.ASTVisitor;

import java.util.Arrays;
import java.util.Set;

public interface ASTImpl extends AST {
    class Statement implements AST.Statement {
        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitStatement(this, param);
        }
    }
    class Program extends Statement implements AST.Program {
        private final AST.Statement[] body;

        public Program(AST.Statement[] body) {
            this.body = body;
        }

        @Override public AST.Statement[] body() { return body; }
        @Override public String toString() { return "Program(body=" + Arrays.toString(body()) + ")"; }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitProgram(this, param);
        }
    }

    class ObtainStatement extends Statement implements AST.ObtainStatement {
        private final AST.Expression[] targets;
        private final AST.Expression path;
        private final boolean computed;

        public ObtainStatement(AST.Expression[] targets, AST.Expression path, boolean computed) {
            this.targets = targets;
            this.path = path;
            this.computed = computed;
        }

        @Override public AST.Expression[] targets() { return targets; }
        @Override public AST.Expression path() { return path; }
        @Override public boolean computed() { return computed; }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitObtainStatement(this, param);
        }
    }

    class VariableDeclarationStatement extends Statement implements AST.VariableDeclarationStatement {
        private final String identifierSymbol;
        private final AST.Expression variableType, value;
        private final Set<TokenType> modifiers;

        public VariableDeclarationStatement(
                AST.Expression variableType,
                String identifierSymbol,
                AST.Expression value,
                Set<TokenType> modifiers
        ) {
            this.variableType = variableType;
            this.identifierSymbol = identifierSymbol;
            this.value = value;
            this.modifiers = modifiers;
        }

        @Override public AST.Expression variableType() { return variableType; }
        @Override public String identifierSymbol() { return identifierSymbol; }
        @Override public AST.Expression value() { return value; }

        @Override
        public Set<TokenType> modifiers() {
            return modifiers;
        }

        @Override
        public String toString() {
            return "VariableDeclarationStatement(" +
                    "identifierSymbol='" + identifierSymbol + '\'' +
                    ", variableType=" + variableType +
                    ", value=" + value +
                    ", modifiers=" + modifiers +
                    ')';
        }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitVariableDeclarationStatement(this, param);
        }
    }
    class FunctionDeclarationStatement extends Program implements AST.FunctionDeclarationStatement {
        final private String identifierSymbol;
        final private AST.Expression returnType;
        final private AST.Statement[] body;
        final private Set<TokenType> modifiers;
        final private Set<AST.FunctionDeclarationStatement.ParameterExpression> arguments;
        final private boolean isBlock;

        public FunctionDeclarationStatement(
                AST.Expression returnType,
                String identifierSymbol,
                AST.Statement[] body,
                Set<TokenType> modifiers,
                Set<AST.FunctionDeclarationStatement.ParameterExpression> arguments,
                boolean isBlock
        ) {
            super(body);
            this.returnType = returnType;
            this.identifierSymbol = identifierSymbol;
            this.body = body;
            this.modifiers = modifiers;
            this.arguments = arguments;
            this.isBlock = isBlock;
        }

        @Override public AST.Expression returnType() { return returnType; }
        @Override public String identifierSymbol() { return identifierSymbol; }
        @Override public AST.Statement[] body() { return body; }
        @Override public Set<TokenType> modifiers() { return modifiers; }
        @Override public Set<AST.FunctionDeclarationStatement.ParameterExpression> arguments() { return arguments; }
        @Override public boolean isBlock() { return isBlock; }

        @Override
        public String toString() {
            return "FunctionDeclarationStatement(" +
                    "identifierSymbol='" + identifierSymbol + '\'' +
                    ", returnType=" + returnType +
                    ", body=" + Arrays.toString(body) +
                    ", modifiers=" + modifiers +
                    ", arguments=" + arguments +
                    ')';
        }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitFunctionDeclarationStatement(this, param);
        }

        public static class ParameterExpression extends Expression implements AST.FunctionDeclarationStatement.ParameterExpression {
            private final boolean isFinal;
            private final AST.Expression variableType;
            private final String identifierSymbol;

            public ParameterExpression(boolean isFinal, AST.Expression variableType, String identifierSymbol) {
                this.isFinal = isFinal;
                this.variableType = variableType;
                this.identifierSymbol = identifierSymbol;
            }

            @Override public AST.Expression variableType() { return variableType; }
            @Override public String identifierSymbol() { return identifierSymbol; }
            @Override public boolean isFinal() { return isFinal; }

            @Override
            public String toString() {
                return "ParameterExpression(" +
                        "isFinal=" + isFinal +
                        ", variableType=" + variableType +
                        ", identifierSymbol='" + identifierSymbol + '\'' +
                        ')';
            }
        }
    }
    
    class Expression extends Statement implements AST.Expression {
        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitExpression(this, param);
        }
    }

    class AssignmentExpression extends Expression implements AST.AssignmentExpression {
        private final AST.Expression variable, value;

        public AssignmentExpression(AST.Expression target, AST.Expression value) {
            this.variable = target;
            this.value = value;
        }

        @Override public AST.Expression variable() { return variable; }
        @Override public AST.Expression value() { return value; }

        @Override
        public String toString() {
            return "AssignmentExpression{" +
                    "variable=" + variable +
                    ", value=" + value +
                    '}';
        }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitAssignmentExpression(this, param);
        }
    }
    class BinaryExpression extends Expression implements AST.BinaryExpression {
        private final AST.Expression left, right;
        private final String operator;

        public BinaryExpression(AST.Expression left, AST.Expression right, String operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override public AST.Expression left() { return left; }
        @Override public AST.Expression right() { return right; }
        @Override public String operator() { return operator; }
        @Override public String toString() { return "BinaryExpression(left=" + left + ",right=" + right + ",operator=\"" + operator + "\")"; }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitBinaryExpression(this, param);
        }
    }
    class FunctionInvocationExpression extends Expression implements AST.FunctionInvocationExpression {
        private final AST.Expression identifier;
        private final AST.Expression[] arguments;

        public FunctionInvocationExpression(AST.Expression identifier, AST.Expression[] arguments) {
            this.identifier = identifier;
            this.arguments = (Expression[]) arguments;
        }
        public FunctionInvocationExpression(AST.Expression identifier) {
            this.identifier = identifier;
            this.arguments = new Expression[0];
        }

        @Override public AST.Expression identifier() { return identifier; }
        @Override public AST.Expression[] arguments() { return arguments; }

        @Override
        public String toString() {
            return "FunctionInvocationExpression(" +
                    "identifier=" + identifier +
                    ", arguments=" + Arrays.toString(arguments) +
                    ')';
        }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitFunctionInvocationExpression(this, param);
        }
    }
    class MemberExpression extends Expression implements AST.MemberExpression {
        private final AST.Expression parent, child;
        private final boolean computed;

        public MemberExpression(AST.Expression parent, AST.Expression child, boolean computed) {
            this.parent = parent;
            this.child = child;
            this.computed = computed;
        }

        @Override public AST.Expression parent() { return parent; }
        @Override public AST.Expression child() { return child; }
        @Override public boolean computed() { return computed; }

        @Override
        public String toString() {
            return "MemberExpression(" +
                    "parent=" + parent +
                    ", child=" + child +
                    ", computed=" + computed +
                    ')';
        }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitMemberExpression(this, param);
        }
    }

    class LiteralExpression extends Expression implements AST.LiteralExpression {
        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitLiteralExpression(this, param);
        }
    }

    class IdentifierLiteralExpression extends LiteralExpression implements AST.IdentifierLiteralExpression {
        private final String symbol;

        public IdentifierLiteralExpression(String symbol) {
            this.symbol = symbol;
        }

        @Override public String symbol() { return symbol; }
        @Override public String toString() { return "IdentifierLiteralExpression(symbol=" + symbol + ")"; }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitIdentifierLiteralExpression(this, param);
        }
    }
    class NumericLiteralExpression extends LiteralExpression implements AST.NumericLiteralExpression {
        private final float value;

        public NumericLiteralExpression(float value) {
            this.value = value;
        }

        @Override public float value() { return value; }
        @Override public String toString() { return "NumericLiteralExpression(value=" + value + ")"; }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitNumericLiteralExpression(this, param);
        }
    }
    class StringLiteralExpression extends LiteralExpression implements AST.StringLiteralExpression {
        private final String value;

        public StringLiteralExpression(String value) {
            this.value = value;
        }

        @Override public String value() { return value; }
        @Override public String toString() { return "StringLiteralExpression(value=\"" + value + "\")"; }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitStringLiteralExpression(this, param);
        }
    }
    class CharacterLiteralExpression extends LiteralExpression implements AST.CharacterLiteralExpression {
        private final char value;

        public CharacterLiteralExpression(char value) {
            this.value = value;
        }

        @Override public char value() { return value; }
        @Override public String toString() { return "CharacterLiteralExpression(value='" + value + "')"; }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitCharacterLiteralExpression(this, param);
        }
    }
    class NullLiteralExpression extends LiteralExpression implements AST.NullLiteralExpression{
        @Override
        public String toString() {
            return "NullLiteralExpression(value='null')";
        }

        @Override
        public <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param) {
            return visitor.visitNullLiteralExpression(this, param);
        }
    }
}
