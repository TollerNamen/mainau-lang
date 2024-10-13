package mainau.compiler.error;

import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ErrorStorage {
    private final String[] sourceCodeLines;
    private final String filePath;

    private final List<Error> errors = new ArrayList<>();

    private final Consumer<TokenError> printTokenError = tokenError -> {
        final int line = tokenError.getLineNumber();
        final String lineSnippet = getLineSnippetByLineNumber(line);
        tokenError.setLineSnippet(lineSnippet);
        tokenError.setFilePath(filePath());
        if (tokenError.type() == ErrorType.FATAL) {
            Output.simplyLog(MessageType.FATAL, tokenError.createMessage());
            System.exit(1);
            return;
        }
        Output.simplyLog(MessageType.ERROR, tokenError.createMessage());
    };
    private final Consumer<Error> printGenericError = error -> {
        if (error.type() == ErrorType.FATAL) {
            Output.simplyLog(MessageType.FATAL, error.createMessage());
            System.exit(1);
            return;
        }
        Output.simplyLog(MessageType.ERROR, error.createMessage());
    };

    public ErrorStorage(String sourceCode, String filePath) {
        this.sourceCodeLines = sourceCode.split("\\R");
        this.filePath = filePath;
    }

    public String getLineSnippetByLineNumber(int line) {
        return sourceCodeLines[line - 1];
    }
    public String getLineSnippetByIndex(int index) {
        return sourceCodeLines[index];
    }

    private String filePath() {
        return filePath;
    }

    public void addError(Error error) {
        addError(error, false);
    }
    public void addError(Error error, boolean fatalize) {
        if (error.type() == ErrorType.FATAL || fatalize) {
            printError(error);
            Output.simplyLog(MessageType.FATAL, "Exiting now, Goodbye!");
            System.exit(1);
        }
        errors.add(error);
    }

    public int getErrorAmount() {
        return errors.size();
    }

    public void printAll() {
        errors.forEach(this::printError);
    }

    private void printError(Error error) {
        if (error.getClass().isAssignableFrom(TokenError.class))
            printTokenError.accept((TokenError) error);
        else
            printGenericError.accept(error);
    }
}
