package mainau.repl.runtime;

public class Util {
    private static final String TAB = "  ";

    /**
     * Custom implementation of {@link java.util.Arrays#toString(Object[])}
     * @param array the array to convert to String
     * @param <T> the kind of the array parameter
     * @return the converted String
     */
    public static <T> String arrayToString(T[] array) {
        return arrayToString(array, 0);
    }

    public static <T> String arrayToString(T[] array, int indentations) {
        if (array == null)
            return "null";

        int iMax = array.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append("\n\t").append("\t".repeat(indentations)).append(array[i]);
            if (i == iMax)
                return b.append("\n\t".repeat(indentations)).append(']').toString();
            b.append(",");
        }
    }

    public static String createTreeString(String object) {
        final char[] chars = object.toCharArray();
        int indentations = 0;
        final StringBuilder builder = new StringBuilder();
        boolean disableSpaceTrimming = false;
        for (char character : chars)
            switch (character) {
                default -> builder.append(character);
                case '"' -> {
                    disableSpaceTrimming = !disableSpaceTrimming;
                    builder.append(character);
                }
                case ' ' -> {
                    if (disableSpaceTrimming) builder.append(" ");
                }
                case '(' -> {
                    builder.append(" ").append(character).append("\n" + TAB).append(TAB.repeat(indentations));
                    indentations++;
                }
                case '[' -> {
                    builder.append(character).append("\n" + TAB).append(TAB.repeat(indentations));
                    indentations++;
                }
                case '=' -> builder.append(" = ");
                case ',' -> builder.append(",\n").append(TAB.repeat(indentations));
                case ')', ']' -> {
                    indentations--;
                    builder.append("\n").append(TAB.repeat(indentations)).append(character);
                }
            }
        return builder.toString();
    }
}
