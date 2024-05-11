package mainau.repl.runtime;

import mainau.compiler.error.Error;
import mainau.compiler.error.ErrorStorage;
import mainau.compiler.lexer.Lexer;
import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;
import mainau.compiler.parser.ASTImpl;
import mainau.compiler.parser.Parser;

public class ProcessTask {
    private final ErrorStorage errorStorage;
    private final Parser parser;
    private final boolean replMode, verbose;
    private final Session session;

    public ProcessTask(String sourceCode, String filePath, boolean replMode, boolean verbose) {
        this.errorStorage = new ErrorStorage(sourceCode, filePath);
        this.parser = new Parser(new Lexer(sourceCode), this);
        this.replMode = replMode;
        this.verbose = verbose;
        session = new Session(null, this);
        session.declareVariable("year", new ValuesImpl.NumberValue(2024));
    }
    public ProcessTask(String sourceCode, String filePath) {
        this(sourceCode, filePath, false, false);
    }

    public void start() {
        ASTImpl.Program program = parser.parseModule();
        executeSendVerboseMessage(program.toString());

        if (checkErrorStorage()) return;

        final ValuesImpl.RuntimeValue runtimeValue;
        if (replMode) {
            runtimeValue = Interpreter.evaluate(program, session);
            executeSendVerboseMessage(runtimeValue.toString());
        }
        if (checkErrorStorage()) return;
    }

    private void executeSendVerboseMessage(String string) {
        if (!verbose) return;
        Output.simplyLog(MessageType.DEV, Util.createTreeString(string));
    }

    private boolean checkErrorStorage() {
        if (errorStorage.getErrorAmount() > 0) {
            errorStorage.printAll();
            return true;
        }
        return false;
    }

    public void insertError(Error error) {
        if (replMode)
            errorStorage.addError(error, true);
        errorStorage.addError(error);
    }
}
