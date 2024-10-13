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

    public ProcessTask(String sourceCode, String filePath, boolean replMode, boolean verbose, Session session) {
        this.errorStorage = new ErrorStorage(sourceCode, filePath);
        this.parser = new Parser(new Lexer(sourceCode), this);
        this.replMode = replMode;
        this.verbose = verbose;
        this.session = session;
        this.session.setTask(this);
    }
    public ProcessTask(String sourceCode, String filePath, Session session) {
        this(sourceCode, filePath, false, false, session);
    }

    public void start() {
        ASTImpl.Program program = parser.parseModule();
        executeSendVerboseMessage(program.toString());

        final ValuesImpl.RuntimeValue runtimeValue;
        if (replMode && !checkErrorStorage()) {
            runtimeValue = Interpreter.evaluate(program, session);
            if (runtimeValue != null)
                Output.simplyLog(MessageType.DEBUG, runtimeValue.toString());
        }
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
            errorStorage.addError(error);
        errorStorage.addError(error);
    }
}
