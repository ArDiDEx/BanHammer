package fr.ardidex.banhammer.enums;

import java.util.concurrent.TimeUnit;

public enum Time {
    Y(TimeUnit.DAYS.toMillis(1)*30*12),
    YEARS(TimeUnit.DAYS.toMillis(1)*30*12),
    YEAR(TimeUnit.DAYS.toMillis(1)*30*12),
    MONTHS(TimeUnit.DAYS.toMillis(1)*30),
    MONTH(TimeUnit.DAYS.toMillis(1)*30),
    W(TimeUnit.DAYS.toMillis(1)*7),
    WEEKS(TimeUnit.DAYS.toMillis(1)*7),
    WEEK(TimeUnit.DAYS.toMillis(1)*7),
    D(TimeUnit.DAYS.toMillis(1)),
    DAYS(TimeUnit.DAYS.toMillis(1)),
    DAY(TimeUnit.DAYS.toMillis(1)),
    H(TimeUnit.HOURS.toMillis(1)),
    HOURS(TimeUnit.HOURS.toMillis(1)),
    HOUR(TimeUnit.HOURS.toMillis(1)),
    M(TimeUnit.MINUTES.toMillis(1)),
    MINUTES(TimeUnit.MINUTES.toMillis(1)),
    MINUTE(TimeUnit.MINUTES.toMillis(1)),
    S(TimeUnit.SECONDS.toMillis(1)),
    SECONDS(TimeUnit.SECONDS.toMillis(1)),
    SECOND(TimeUnit.SECONDS.toMillis(1)),


    ;


    public final long multiplyBy;

    Time(long multiplyBy) {
        this.multiplyBy = multiplyBy;
    }

    public long parse(long l){
        return l * multiplyBy;
    }
}
