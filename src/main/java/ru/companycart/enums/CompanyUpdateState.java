package ru.companycart.enums;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public enum CompanyUpdateState {

    ACTUAL,

    OLD,

    OUTDATED,

    DELETED,

    API_ERROR,

    SENT;

}