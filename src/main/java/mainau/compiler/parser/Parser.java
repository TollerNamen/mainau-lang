package mainau.compiler.parser;

import mainau.compiler.error.ErrorType;
import mainau.compiler.error.TokenError;
import mainau.compiler.lexer.Token;
import mainau.compiler.lexer.TokenType;
import mainau.compiler.lexer.Lexer;
import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import mainau.repl.runtime.ProcessTask;

import java.util.*;

import static mainau.compiler.lexer.TokenType.*;

public class Parser {
    private final Lexer lexer;
    private final ProcessTask task;
    final List<ASTImpl.Statement> statements = new ArrayList<>();

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

    private void check(Collection<TokenType> types, Token token) {
        final var error = Checks.expectTokenType(types, token);
        if (error != null)
            task.insertError(error);
    }

    public ASTImpl.Program parseModule() {
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
                task.insertError(new TokenError(ErrorType.INVALID_ACTION, "repeating attribute modifier", token));
            }
        }

        return switch (token().type()) {
            case OBTAIN -> null;
            case IDENTIFIER -> {
                var identifier = parseIdentifierExpression();
                yield switch (lexer.peakNext().type()) {
                    case IDENTIFIER -> parseVariableDeclarationStatement(identifier, attributeModifiers);
                    case COLON -> parseFunctionDeclarationStatement(identifier, attributeModifiers);
                    case ASSIGN -> {
                        if (lexer.peakNext(2).type() == IDENTIFIER
                                || lexer.peakNext(3).type() == OPEN_PAREN
                        ) yield parseFunctionDeclarationStatement(identifier, attributeModifiers);
                        else yield parseAssignmentStatement();
                    }
                    case BINARY_ASSIGN -> parseAssignmentStatement();
                    case OPEN_PAREN -> parseFunctionInvocationStatement(identifier);
                    default -> null;
                };
            }
            default -> {
                if (TokenType.getKeywordTypes().contains(token().type()))
                    yield parseVariableDeclarationStatement(
                            new ASTImpl.IdentifierLiteralExpression(token().value()),
                            attributeModifiers
                    );
                else yield parseExpression();
            }
        };
    }

    private ASTImpl.Statement parseVariableDeclarationStatement(
            ASTImpl.Expression variableType,
            Set<TokenType> modifiers
    ) {
        List<ASTImpl.Statement> variableDeclarationStatements = new ArrayList<>();

        do {
            final Token identifierToken = lexer.next();
            check(IDENTIFIER, identifierToken);

            var check = lexer.next();
            final boolean isAssigning = check.type() == ASSIGN;

            variableDeclarationStatements.add(new ASTImpl.VariableDeclarationStatement(
                    variableType,
                    identifierToken.value(),
                    isAssigning ? parseExpression() : null,
                    modifiers
            ));

            check(Set.of(ASSIGN, COMMA, SEMI), check);

            check = isAssigning ? lexer.next() : check;
            if (check.type() == SEMI) break;
            else check(Set.of(SEMI, COMMA), check);

        } while (true);

        var last = variableDeclarationStatements.getLast();

        variableDeclarationStatements = variableDeclarationStatements
                .stream()
                .filter(statement -> statement != last)
                .toList();

        statements.addAll(variableDeclarationStatements);

        return last;
    }

    private ASTImpl.Statement parseAssignmentStatement() {
        ASTImpl.Expression left = parseExpression();

        if (!Set.of(ASSIGN, BINARY_ASSIGN).contains(token().type())) {
            return left;
        }
        final var operator = lexer.next();

        if (operator.type() == ASSIGN)
            return new ASTImpl.AssignmentStatement(left, parseExpression());

        return new ASTImpl.AssignmentStatement(left, new ASTImpl.BinaryExpression(
                left,
                parseExpression(),
                operator.value()
        ));
    }

    private ASTImpl.Statement parseFunctionDeclarationStatement(
            AST.Expression functionIdentifier,
            Set<TokenType> modifiers
    ) {

    }

    private ASTImpl.Statement parseFunctionInvocationStatement(
            AST.Expression functionIdentifier
    ) {
        lexer.next();

        var identifier = (ASTImpl.IdentifierLiteralExpression) functionIdentifier;

        if (token().type() == CLOSE_PAREN)
            return new ASTImpl.FunctionInvocationStatement(identifier);

        List<AST.Expression> arguments = new ArrayList<>();

        do {
            arguments.add(parseExpression());
            check(Set.of(CLOSE_PAREN, COMMA), token());
        } while (token().type() == COMMA || lexer.next().type() != CLOSE_PAREN);

        return new ASTImpl.FunctionInvocationStatement(identifier, arguments.toArray(AST.Expression[]::new));
    }

    private ASTImpl.Expression parseExpression() {
        return parseAdditiveExpression();
    }

    private ASTImpl.Expression parseAdditiveExpression() {
        ASTImpl.Expression left = parseMultiplicativeExpression();

        while (Set.of("+", "-").contains(token().value())) {
            final String operator = lexer.next().value();
            final ASTImpl.Expression right = parseMultiplicativeExpression();
            left = new ASTImpl.BinaryExpression(left, right, operator);
        }
        return left;
    }

    private ASTImpl.Expression parseMultiplicativeExpression() {
        ASTImpl.Expression left = parseLiteralExpression();

        while (Set.of("*", "/", "%", "^").contains(token().value())) {
            final String operator = lexer.next().value();
            final ASTImpl.Expression right = parseLiteralExpression();
            left = new ASTImpl.BinaryExpression(left, right, operator);
        }
        return left;
    }

    private ASTImpl.Expression parseLiteralExpression() {
        return switch (token().type()) {
            case NUMBER_VALUE -> new ASTImpl.NumericLiteralExpression(Float.parseFloat(lexer.next().value()));
            case STRING -> new ASTImpl.StringLiteralExpression(lexer.next().value());
            case CHARACTER -> new ASTImpl.CharacterLiteralExpression(lexer.next().value().charAt(0));
            case IDENTIFIER -> parseIdentifierExpression();
            case OPEN_PAREN -> {
                lexer.next();
                final ASTImpl.Expression expression = parseExpression();
                check(CLOSE_PAREN, lexer.next());
                yield expression;
            }
            case NULL -> new ASTImpl.NullLiteralExpression();
            default -> {
                Output.output().send(
                        MessageType.FATAL,
                        "Could not parse following token: " +
                                "\n" + token() +
                                "\nExiting now, Goodbye!"
                );
                System.exit(1);
                yield null;
            }
        };
    }

    private ASTImpl.Expression parseIdentifierExpression() {
        List<String> path = new ArrayList<>();

        do {
            check(IDENTIFIER, token());
            path.add(lexer.next().value());
        } while (lexer.next().type() == DOT);

        final var symbol = path.getLast();

        path = path.stream()
                .filter(s -> s.equals(symbol))
                .toList();

        return new ASTImpl.IdentifierLiteralExpression(symbol, path.toArray(new String[0]));
    }
}























