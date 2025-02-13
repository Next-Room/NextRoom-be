package com.nextroom.nextRoomServer.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class TimestampedTest {

    @Test
    void stringToKstLocalDate() {
        String isoDateString = "2025-03-12T10:30:08.733Z";
        LocalDate kstDate = Timestamped.stringToKstLocalDate(isoDateString);
        System.out.println(kstDate);
    }
}
