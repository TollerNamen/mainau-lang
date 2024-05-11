package mainau.compiler.parser;

import mainau.compiler.error.TokenError;
import mainau.compiler.error.ErrorType;
import mainau.compiler.lexer.Token;
import mainau.compiler.lexer.TokenType;

public class Checks {
    public static TokenError expectTokenType(TokenType type, Token token) {
        if (token == null)
            return new TokenError(ErrorType.EXPECTED, "Expected following symbol: "
                    + type.name() + ", but received nothing instead", null);
        if (token.type() != type) {
            return new TokenError(ErrorType.EXPECTED, "Expected following symbol: "
                    + type.name() + ", but received: " + token.type().name() + " instead", token);
        }
        return null;
    }
}
