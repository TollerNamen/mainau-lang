package mainau.compiler.analysis.syntactic;

import mainau.compiler.Util;
import mainau.compiler.ast.AST;
import mainau.compiler.ast.ASTImpl;
import mainau.compiler.error.ErrorType;
import mainau.compiler.error.TokenError;
import mainau.compiler.analysis.lexical.Token;
import mainau.compiler.analysis.lexical.TokenType;
import mainau.compiler.analysis.lexical.Lexer;
import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import mainau.repl.runtime.ProcessTask;

import java.util.*;

import static mainau.compiler.analysis.lexical.TokenType.*;

public class Parser {
    private final Lexer lexer;
    private final ProcessTask task;
    final List<AST.Statement> statements = new ArrayList<>();

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
        return parseModule(EOF);
    }
    private ASTImpl.Program parseModule(TokenType terminate) {
        do statements.add(parseStatement());
        while (token().type() != terminate || token().type() != EOF);

        if (terminate != EOF && token().type() == EOF)
            check(terminate, token()); // will definitely insert Error

        Output.simplyLog(MessageType.DEBUG, "Successfully parsed the module");

        return new ASTImpl.Program(statements.toArray(new AST.Statement[0]));
    }

    private AST.Statement parseStatement() {
        var attributeModifiers = parseAttributeModifiers();

        return switch (token().type()) {
            case OBTAIN -> null;
            case IDENTIFIER -> {
                var member = parseMemberFunctionInvocationExpression();
                if (member instanceof AST.FunctionInvocationExpression)
                    yield member;
                yield parseVariableDeclarationStatement(
                        member,
                        attributeModifiers
                );
            }
            default -> {
                if (TokenType.getKeywordTypes().contains(token().type()))
                    yield parseVariableDeclarationStatement(
                            new ASTImpl.IdentifierLiteralExpression(lexer.next().value()),
                            attributeModifiers
                    );
                else yield parseExpression();
            }
        };
    }

    private Set<TokenType> parseAttributeModifiers() {
        final Set<TokenType> attributeModifiers = new HashSet<>();

        while (TokenType.getAttributeModifiers().contains(token().type())) {
            final Token token = lexer.next();
            if (attributeModifiers.add(token.type()))
                continue;
            task.insertError(new TokenError(ErrorType.INVALID_ACTION, "repeating attribute modifier", token));
        }
        return attributeModifiers;
    }

    private AST.Statement parseVariableDeclarationStatement(
            AST.Expression variableType,
            Set<TokenType> modifiers
    ) {
        List<ASTImpl.Statement> variableDeclarationStatements = new ArrayList<>();

        do {
            final Token identifierToken = lexer.next();
            check(IDENTIFIER, identifierToken);

            var check = lexer.next();
            final boolean isAssigning = check.type() == ASSIGN;

            // is function?
            if (variableType instanceof AST.IdentifierLiteralExpression identifier
                    && identifier.symbol().equals("fun"))
                return check.type() == OPEN_BRACE
                        ? parseFunctionDeclarationStatement(identifierToken.value(), modifiers)
                        : parseSingleFunctionDeclarationStatement(identifierToken.value(), modifiers);

            variableDeclarationStatements.add(new ASTImpl.VariableDeclarationStatement(
                    variableType,
                    identifierToken.value(),
                    isAssigning ? parseAdditiveExpression() : null,
                    modifiers
            ));

            check(Set.of(ASSIGN, COMMA, SEMI), check);
            Output.simplyLog(MessageType.DEV, Arrays.toString(variableDeclarationStatements.toArray()));

            check = isAssigning ? lexer.next() : check;
            if (check.type() == SEMI) break;
            else check(Set.of(SEMI, COMMA), check);

            Output.simplyLog(MessageType.DEV, "should not have gotten here");

        } while (true);
        Output.simplyLog(MessageType.DEV, "got here");

        return Util.returnLastAddToTarget(variableDeclarationStatements, statements);
    }

    private AST.Statement parseFunctionDeclarationStatement(
            String identifier,
            Set<TokenType> modifiers
    ) {
        check(Set.of(ASSIGN, OPEN_BRACE), token());

        if (token().type() == ASSIGN)
            return parseSingleFunctionDeclarationStatement(identifier, modifiers);

        List<AST.FunctionDeclarationStatement> functions = new ArrayList<>();

        Set<TokenType> specificModifiers;

        do {
            specificModifiers = new HashSet<>(modifiers);
            specificModifiers.addAll(parseAttributeModifiers());

            functions.add(parseSingleFunctionDeclarationStatement(identifier, specificModifiers));
        } while (token().type() != CLOSE_BRACE);

        return Util.returnLastAddToTarget(functions, statements);
    }

    private AST.FunctionDeclarationStatement parseSingleFunctionDeclarationStatement(
            String identifier,
            Set<TokenType> modifiers
    ) {
        check(TokenType.getPossibleFunctionReturnTypeTokenTypes(), lexer.peakNext());
        var returnTypeIdentifier = parseMemberExpression();

        check(OPEN_PAREN, lexer.next());
        var arguments = parseFunctionDeclarationArguments();

        check(LAMBDA, lexer.next());

        boolean isBlock = token().type() == OPEN_BRACE;

        var body = isBlock
                ? parseModule(CLOSE_BRACE).body()
                : new AST.Statement[]{parseStatement()};

        return new ASTImpl.FunctionDeclarationStatement(
                returnTypeIdentifier,
                identifier,
                body,
                modifiers,
                arguments,
                isBlock
        );
    }

    private Set<AST.FunctionDeclarationStatement.ParameterExpression> parseFunctionDeclarationArguments() {
        Set<AST.FunctionDeclarationStatement.ParameterExpression> arguments = new HashSet<>();

        if (token().type() == CLOSE_PAREN) {
            lexer.next();
            return Set.of();
        }

        AST.Expression variableType;
        String symbol;
        boolean isFinal;

        do {
            isFinal = lexer.next().type() == FINAL;

            variableType = parseMemberExpression();

            check(IDENTIFIER, token());
            symbol = lexer.next().value();

            arguments.add(new ASTImpl.FunctionDeclarationStatement.ParameterExpression(
                    isFinal,
                    variableType,
                    symbol
            ));
        } while (token().type() == COMMA && lexer.next().type() != CLOSE_PAREN);

        return arguments;
    }

    private AST.Expression parseExpression() {
        return parseAssignmentExpression();
    }

    private AST.Expression parseAssignmentExpression() {
        AST.Expression left = parseAdditiveExpression();
        if (!Set.of(ASSIGN, BINARY_ASSIGN).contains(token().type()))
            return left;

        final var operator = lexer.next();

        if (operator.type() == ASSIGN)
            return new ASTImpl.AssignmentExpression(left, parseAdditiveExpression());

        return left;
    }

    private AST.Expression parseAdditiveExpression() {
        AST.Expression left = parseMultiplicativeExpression();

        while (Set.of("+", "-").contains(token().value())) {
            final String operator = lexer.next().value();
            final AST.Expression right = parseMultiplicativeExpression();
            left = new ASTImpl.BinaryExpression(left, right, operator);
        }
        return left;
    }

    private AST.Expression parseMultiplicativeExpression() {
        AST.Expression left = parseMemberFunctionInvocationExpression();

        while (Set.of("*", "/", "%").contains(token().value())) {
            final String operator = lexer.next().value();
            final AST.Expression right = parseMemberFunctionInvocationExpression();
            left = new ASTImpl.BinaryExpression(left, right, operator);
        }
        return left;
    }

    private AST.Expression parseMemberFunctionInvocationExpression() {
        final var member = parseMemberExpression();

        if (token().type() == OPEN_PAREN)
            return parseFunctionInvocationExpression(member);

        return member;
    }

    private AST.Expression parseFunctionInvocationExpression(AST.Expression identifier) {
        ASTImpl.Expression expression;

        lexer.next(); // get past OPEN_PAREN

        if (token().type() == CLOSE_PAREN) {
            expression = new ASTImpl.FunctionInvocationExpression(identifier);
            lexer.next();
        }
        else {
            List<AST.Expression> arguments = new ArrayList<>();

            do {
                arguments.add(parseAdditiveExpression());
                check(Set.of(CLOSE_PAREN, COMMA), token());
            } while (token().type() == COMMA && lexer.next().type() != CLOSE_PAREN);

            expression = new ASTImpl.FunctionInvocationExpression(
                    identifier,
                    arguments.toArray(AST.Expression[]::new)
            );
        }

        // allow currying
        if (lexer.next().type() == OPEN_PAREN)
            return parseFunctionInvocationExpression(expression);
        return expression;
    }

    private AST.Expression parseMemberExpression() {
        var parent = parseLiteralExpression();

        if (!(parent instanceof AST.IdentifierLiteralExpression))
            return parent;

        boolean computed;
        AST.Expression child;

        while (Set.of(DOT, OPEN_BRACKET).contains(token().type())) {
            computed = lexer.next().type() == OPEN_BRACKET;

            if (computed) {
                child = parseAdditiveExpression();
                check(CLOSE_BRACKET, lexer.next());
            }
            else {
                check(IDENTIFIER, token());
                child = new ASTImpl.IdentifierLiteralExpression(lexer.next().value());
            }

            parent = new ASTImpl.MemberExpression(
                    parent,
                    child,
                    computed
            );
        }
        return parent;
    }

    private AST.Expression parseLiteralExpression() {
        return switch (token().type()) {
            case NUMBER_VALUE -> new ASTImpl.NumericLiteralExpression(Float.parseFloat(lexer.next().value()));
            case STRING -> new ASTImpl.StringLiteralExpression(lexer.next().value());
            case CHARACTER -> new ASTImpl.CharacterLiteralExpression(lexer.next().value().charAt(0));
            case IDENTIFIER -> new ASTImpl.IdentifierLiteralExpression(lexer.next().value());
            case OPEN_PAREN -> {
                lexer.next();
                final AST.Expression expression = parseExpression();
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
}
