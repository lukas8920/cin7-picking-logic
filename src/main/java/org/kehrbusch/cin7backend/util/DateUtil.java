package org.kehrbusch.cin7backend.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {
    private static final String ZONE = "Pacific/Auckland";

    public static Date getCurrentDateTime(){
        ZoneId zoneId = ZoneId.of(ZONE);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }
}
