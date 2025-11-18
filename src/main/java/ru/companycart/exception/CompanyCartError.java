package ru.companycart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CompanyCartError {

    private final CompanyCartErrorCode code;

    private final String message;

}
