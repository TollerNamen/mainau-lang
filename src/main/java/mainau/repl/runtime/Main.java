package mainau.repl.runtime;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Mainau Repl! v0.1");
        Scanner scanner = new Scanner(System.in);
        String input;
        ProcessTask task;
        Session session = new Session();
        while (scanner.hasNext()) {
            input = scanner.nextLine();
            switch (input) {
                case "\n" -> {}
                case null -> {}
                case "", "exit" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> {
                    task = new ProcessTask(input, "Repl-Session", true, true, session);
                    task.start();
                }
            }
        }
        scanner.close();
    }
}