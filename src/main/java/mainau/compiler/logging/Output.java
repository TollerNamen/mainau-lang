package mainau.compiler.logging;

import java.io.PrintStream;
import java.time.LocalDateTime;
import static org.fusesource.jansi.Ansi.*;

public class Output {
    public static Output output() {
        return new Output();
    }
    public static void simplyLog(MessageType type, String message) {
        output().send(new Message(type, message));
    }

    private boolean enableTimeDisplay = false, eraseLine = false;

    public Output setEnableTimeDisplay(boolean enableTimeDisplay) {
        this.enableTimeDisplay = enableTimeDisplay;
        return this;
    }
    public Output setEraseLine(boolean eraseLine) {
        this.eraseLine = eraseLine;
        return this;
    }

    public void send(MessageType type, String message) {
        send(new Message(type, message));
    }

    public void send(Message message) {
        PrintStream printStream;
        String color, prefix;
        switch (message.type()) {
            case DEV:
                printStream = System.out;
                color = "fg_cyan";
                break;
            case DEBUG, SUGGEST:
                printStream = System.out;
                color = "fg_green";
                break;
            case WARNING:
                printStream = System.out;
                color = "fg_yellow";
                break;
            case ERROR:
                printStream = System.out;
                color = "fg_red";
                break;
            case FATAL:
                printStream = System.err;
                color = "fg_white,bg_red";
                break;
            case null, default:
                send(new Message(MessageType.FATAL, "Could not determine the message type: " + message.type()));
                return;
        }
        final int frontSpace, backSpace, rest;
        rest = 7 - message.type().name().length();
        frontSpace = rest / 2;
        backSpace = rest / 2;
        boolean isFatal = message.type() == MessageType.FATAL;

        prefix = !isFatal
                ? "@|bold,fg_white [|@@|" + color + " " + " ".repeat(frontSpace) + message.type().name() + " ".repeat(backSpace) + "|@@|bold,fg_white ]|@ "
                : "@|bold,fg_white,bg_red [" + " ".repeat(frontSpace) + message.type().name() + " ".repeat(backSpace) + "]|@ ";
        if (enableTimeDisplay)
            prefix = prefix + LocalDateTime.now().toString().replace("T", " T") + " - ";

        final String prefixedMessage = prefix + message.message().replace("\n", "\n" + prefix);
        printStream.println(eraseLine ? ansi().eraseLine().render(prefixedMessage) : ansi().render(prefixedMessage));
    }
}
