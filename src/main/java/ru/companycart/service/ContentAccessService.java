package ru.companycart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.companycart.mapper.ContentAccessMapper;
import ru.companycart.repository.ContentAccessRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentAccessService {

    private final ContentAccessRepository repository;
    private final ContentAccessMapper mapper;


}
