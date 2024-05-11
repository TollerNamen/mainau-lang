package mainau.compiler.parser;

import mainau.compiler.error.ErrorType;
import mainau.compiler.lexer.Token;
import mainau.compiler.lexer.TokenType;
import mainau.compiler.error.TokenError;
import mainau.compiler.lexer.Lexer;
import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import mainau.repl.runtime.ProcessTask;
import mainau.repl.runtime.Util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static mainau.compiler.lexer.TokenType.*;

public class Parser {
    private final Lexer lexer;
    private final ProcessTask task;

    private Token token() {
        return lexer.token();
    }

    public Parser(Lexer lexer, ProcessTask task) {
        this.lexer = lexer;
        this.task = task;
    }

    private void check(TokenType tokenType, Token token) {
        final TokenError error = Checks.expectTokenType(tokenType, token);
        if (error != null) task.insertError(error);
    }

    public ASTImpl.Program parseModule() {
        final List<ASTImpl.Statement> statements = new ArrayList<>();

        do statements.add(parseStatement());
        while (token().type() != EOF);

        Output.simplyLog(MessageType.DEBUG, "Successfully parsed the module");

        return new ASTImpl.Program(statements.toArray(new ASTImpl.Statement[0]));
    }

    private ASTImpl.Statement parseStatement() {
        final Set<TokenType> attributeModifiers = new HashSet<>();

        while (TokenType.getAttributeModifiers().contains(token().type())) {
            final Token token = lexer.next();
            try {
                attributeModifiers.add(token.type());
            } catch (IllegalArgumentException e) {
                task.insertError(new TokenError(ErrorType.INVALID_ACTION, "You cannot repeat a attribute modifier.", token));
            }
        }

        if (TokenType.getKeywordTypes().contains(token().type())) {
            final String variableType = lexer.next().value();
            final Token identifierToken = lexer.next();
            check(IDENTIFIER, identifierToken);
            final Token assignToken = lexer.next();
            final boolean assignsValue = assignToken.type() == ASSIGN;
            final ASTImpl.Expression value = assignsValue ? parseExpression() : null;
            check(SEMI, assignToken);
            return new ASTImpl.VariableDeclarationStatement(
                    variableType,
                    identifierToken.value(),
                    attributeModifiers.contains(FINAL),
                    assignsValue,
                    value
            );
        }
        else return parseExpression();
    }

    private ASTImpl.Expression parseExpression() {
        return parseAdditiveExpression();
    }

    private ASTImpl.Expression parseAdditiveExpression() {
        final ASTImpl.Expression left = parseMultiplicativeExpression();

        if (Set.of("+", "-").contains(token().value())) {
            final String operator = lexer.next().value();
            final ASTImpl.Expression right = parseMultiplicativeExpression();
            return new ASTImpl.BinaryExpression(left, right, operator);
        }
        return left;
    }

    private ASTImpl.Expression parseMultiplicativeExpression() {
        final ASTImpl.Expression left = parseLiteralExpression();

        if (Set.of("*", "/", "%").contains(token().value())) {
            final String operator = lexer.next().value();
            final ASTImpl.Expression right = parseLiteralExpression();
            return new ASTImpl.BinaryExpression(left, right, operator);
        }
        return left;
    }

    private ASTImpl.Expression parseLiteralExpression() {
        switch (token().type()) {
            case NUMBER_VALUE:
                return new ASTImpl.NumericLiteralExpression(Float.parseFloat(lexer.next().value()));
            case STRING:
                return new ASTImpl.StringLiteralExpression(lexer.next().value());
            case CHARACTER:
                return new ASTImpl.CharacterLiteralExpression(lexer.next().value().charAt(0));
            case IDENTIFIER:
                return new ASTImpl.IdentifierLiteralExpression(lexer.next().value());
            case OPEN_PAREN:
                lexer.next();
                final ASTImpl.Expression expression = parseExpression();
                Output.simplyLog(MessageType.DEV, Util.createTreeString(expression.toString()));
                check(CLOSE_PAREN, lexer.next());
                return expression;
            case NULL:
                return new ASTImpl.NullLiteralExpression();
            default:
                Output.output().send(
                        MessageType.FATAL,
                        "Could not parse following token: " +
                        "\n" + token() +
                        "\nExiting now, Goodbye!"
                );
                System.exit(1);
                return null;
        }
    }
}
