package ru.companycart.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.companycart.repository.ContentAccessRepository;

@Component
@RequiredArgsConstructor
public class ContentAccessMapper {

    private final ContentAccessRepository contentAccessRepository;


}
