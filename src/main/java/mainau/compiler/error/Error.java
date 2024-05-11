package mainau.compiler.error;

public interface Error {
    ErrorType type();
    String message();
    String createMessage();
}
