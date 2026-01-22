package ru.companycart.enums;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public enum CompanyUpdateState {

    ACTUAL(30),

    OLD(5),

    OUTDATED(90),

    DELETED,

    API_ERROR(3),

    SENT;

    private final Integer daysThreshold;

    CompanyUpdateState(Integer daysThreshold) {
        this.daysThreshold = daysThreshold;
    }

    CompanyUpdateState() {
        this(null);
    }

    public Integer getDaysThreshold() {
        return daysThreshold;
    }

    public boolean shouldUpdate(Instant lastUpdate) {
        // Для состояний без порога возвращаем false
        if (daysThreshold == null) {
            return false;
        }

        long daysSinceUpdate = ChronoUnit.DAYS.between(lastUpdate, Instant.now());
        return daysSinceUpdate > this.daysThreshold;
    }
}