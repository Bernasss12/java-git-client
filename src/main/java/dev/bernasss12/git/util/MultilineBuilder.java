package dev.bernasss12.git.util;

public class MultilineBuilder {

    private final StringBuilder builder = new StringBuilder();

    public void append(String string) {
        builder.append(string);
    }

    public void appendln() {
        builder.append(System.lineSeparator());
    }

    public void appendln(String string) {
        builder.append(string);
        builder.append(System.lineSeparator());
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
