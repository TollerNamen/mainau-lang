package mainau.compiler.parser;

import mainau.compiler.error.TokenError;
import mainau.compiler.error.ErrorType;
import mainau.compiler.lexer.Token;
import mainau.compiler.lexer.TokenType;

import java.util.Arrays;
import java.util.Collection;

public class Checks {
    public static TokenError expectTokenType(TokenType type, Token token) {
        /*
        if (token == null)
            return new TokenError(ErrorType.EXPECTED, "Expected following symbol: "
                    + type.name() + ", but received nothing instead", null);
         */
        if (token.type() != type) {
            return new TokenError(ErrorType.EXPECTED, "unexpected symbol: "
                    + token.type().name() + " expected: " + type.name(), token);
        }
        return null;
    }
    public static TokenError expectTokenType(Collection<TokenType> types, Token token) {
        return types.contains(token.type()) ? null : new TokenError(ErrorType.EXPECTED,
                "unexpected symbol: " + token.type().name() + " expected one of: " +
                        token.type().name() + Arrays.toString(types.toArray()), token);
    }
}
