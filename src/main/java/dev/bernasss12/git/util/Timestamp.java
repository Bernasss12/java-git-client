package dev.bernasss12.git.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Timestamp(Long epochSeconds, int offset) {

    private static final Pattern pattern = Pattern.compile(">\\s(.*)\\s(.*)");

    public static Timestamp now() {
        OffsetDateTime zdt = OffsetDateTime.now();
        return new Timestamp(zdt.toEpochSecond(), zdt.getOffset().getTotalSeconds());
    }

    public static Timestamp extract(String string) {
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find() || matcher.groupCount() != 2) {
            throw new IllegalArgumentException("Cannot extract timestamp from: " + string);
        }
        long seconds = Long.parseLong(matcher.group(1));
        int offset = ZoneOffset.of(matcher.group(2)).getTotalSeconds();
        return new Timestamp(seconds, offset);
    }

    @Override
    public String toString() {
        return String.format("%d %s", epochSeconds, secondsToOffsetString(offset));
    }

    private String secondsToOffsetString(int offset) {
        final char c = offset >= 0 ? '+' : '-';
        final int secondsInMinute = 60;
        final int minutesInHour = 60;
        final int secondsInHour = minutesInHour * secondsInMinute;
        offset = Math.abs(offset);
        final int hours = offset / secondsInHour;
        final int minutes = (offset % secondsInHour) / secondsInMinute;
        return String.format("%c%02d%02d", c, hours, minutes);
    }
}
