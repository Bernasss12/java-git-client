package dev.bernasss12.git.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Identity(
        String name,
        String email
) {

    private static final Pattern pattern = Pattern.compile("\\s(.*)<(.*)>");
    public static Identity extract(String string) {
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find() || !(matcher.groupCount() == 2)) {
            throw new IllegalArgumentException("Cannot extract identity from: " + string);
        }
        return new Identity(matcher.group(1).trim(), matcher.group(2).trim());
    }

    public static Identity global() {
        // This represents the --global user that can be defined in git.
        return new Identity("Dummy Name", "very.real@email.il");
    }

    @Override
    public String toString() {
        return String.format("%s <%s>", name, email);
    }
}
