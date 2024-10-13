package mainau.repl.runtime;

import mainau.compiler.error.Error;
import mainau.compiler.error.ErrorType;

public record RuntimeError(ErrorType type, String message) implements Error {
    @Override
    public String createMessage() {
        return "Error of type " + type.name() + " during Repl-Session:" +
                "\nMessage: " + message +
                "\n";
    }
}
