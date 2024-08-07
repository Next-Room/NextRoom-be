package com.nextroom.nextRoomServer.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Timestamped {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    public static String dateTimeFormatter(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static LocalDate stringToKstLocalDate(String isoDateString) {
        ZonedDateTime utcDateTime = ZonedDateTime.parse(isoDateString, DateTimeFormatter.ISO_DATE_TIME);
        ZonedDateTime kstDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        return kstDateTime.toLocalDate();
    }

}
