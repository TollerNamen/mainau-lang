package mainau.repl.runtime;

import mainau.compiler.parser.Parser;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        class Goodbye {
            static void execute() {
                System.out.println("Goodbye!");
                System.exit(0);
            }
        }
        System.out.println("Mainau Parser! v0.1");
        Scanner scanner = new Scanner(System.in);
        String input;
        Parser parser;
        ProcessTask task;
        while (scanner.hasNext()) {
            input = scanner.nextLine();
            switch (input) {
                case null -> Goodbye.execute();
                case "exit" -> Goodbye.execute();
                default -> {
                    task = new ProcessTask(input, "Repl-Session", true, true);
                    task.start();
                }
            }
        }
        scanner.close();
    }
}