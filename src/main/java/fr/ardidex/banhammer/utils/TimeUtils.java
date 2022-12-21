package fr.ardidex.banhammer.utils;

import fr.ardidex.banhammer.enums.Time;
import fr.ardidex.banhammer.exceptions.TimeParseException;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    /**
     * parses string to time
     */
    public static long parseTime(String string) throws TimeParseException {
        Matcher matcher = Pattern.compile("\\d+|\\D+").matcher(string); // https://stackoverflow.com/questions/11232801/regex-split-numbers-and-letter-groups-without-spaces
        long time = 0;
        while (matcher.find())
        {
            String timeString = matcher.group(0);
            long i;
            try{
                i = Long.parseLong(timeString);
            }catch (Exception e){
                throw new TimeParseException();
            }

            boolean b = matcher.find();
            if(!b){
                time += Time.SECONDS.parse(i);
                break;
            }
            String timeUnit = matcher.group(0);
            try {
                time += Time.valueOf(timeUnit.toUpperCase()).parse(i);
            }catch (Exception e){
                throw new TimeParseException();
            }
        }
        return time;
    }

    /**
     * formats a long to a String representing time
     * if uptime is < 0 this will return "Permanent"
     */
    public static String formatTime(long uptime) {
        if (uptime < 0)
            return "Permanent";

        long days = TimeUnit.MILLISECONDS
                .toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS
                .toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS
                .toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS
                .toSeconds(uptime);

        long years = days > 0 ? days / 365 : 0;

        days = days % 365;

        long months = days > 0 ? days / 31 : 0;
        days = days % 31;

        String minute = "minutes";
        String second = "seconds";
        String hour = "hours";
        String day = "days";
        String month = "months";
        String year = "years";
        StringBuilder stringBuilder = new StringBuilder();
        if (years != 0)
            stringBuilder.append(years).append(years > 1 ? year : year.substring(0, year.length() - 1)).append(" ");
        if (months != 0)
            stringBuilder.append(months).append(months > 1 ? month : month.substring(0, month.length() - 1)).append(" ");
        if (days != 0)
            stringBuilder.append(days).append(days > 1 ? day : day.substring(0, day.length() - 1)).append(" ");
        if (hours != 0)
            stringBuilder.append(hours).append(hours > 1 ? hour : hour.substring(0, hour.length() - 1)).append(" ");
        if (minutes != 0)
            stringBuilder.append(minutes).append(minutes > 1 ? minute : minute.substring(0, minute.length() - 1)).append(" ");
        if (seconds != 0)
            stringBuilder.append(seconds).append(seconds > 1 ? second : second.substring(0, second.length() - 1)).append(" ");

        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
