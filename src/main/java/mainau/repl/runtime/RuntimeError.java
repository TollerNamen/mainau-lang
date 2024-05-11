package mainau.repl.runtime;

import mainau.compiler.error.Error;
import mainau.compiler.error.ErrorType;

public class RuntimeError implements Error {
    private final ErrorType type;
    private final String message;

    public RuntimeError(ErrorType type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public ErrorType type() {
        return type;
    }
    @Override
    public String message() {
        return message;
    }

    @Override
    public String createMessage() {
        return "Error of type " + type.name() + " during Repl-Session:" +
                "\nMessage: " + message +
                "\n";
    }
}
