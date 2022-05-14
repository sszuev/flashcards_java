package com.gitlab.sszuev.flashcards.services.impl;

import com.gitlab.sszuev.flashcards.documents.DictionaryReader;
import com.gitlab.sszuev.flashcards.documents.DictionaryWriter;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.DictionaryResource;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import com.gitlab.sszuev.flashcards.services.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DictionaryServiceImpl implements DictionaryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryServiceImpl.class);

    private final DictionaryRepository repository;
    private final EntityMapper mapper;
    private final DictionaryReader dictionaryReader;
    private final DictionaryWriter dictionaryWriter;

    public DictionaryServiceImpl(DictionaryRepository repository,
                                 EntityMapper mapper,
                                 DictionaryReader dictionaryReader,
                                 DictionaryWriter dictionaryWriter) {
        this.repository = repository;
        this.mapper = mapper;
        this.dictionaryReader = dictionaryReader;
        this.dictionaryWriter = dictionaryWriter;
    }

    @Transactional(readOnly = true)
    @Override
    public List<DictionaryResource> getDictionaries() {
        // todo: separated selects for total and learned counts
        return repository.streamAllByUserId(User.SYSTEM_USER.getID())
                .map(mapper::toResource).toList();
    }

    @Transactional
    @Override
    public DictionaryResource uploadDictionary(Resource resource) {
        Dictionary dic = dictionaryReader.parse(resource);
        LOGGER.debug("Dictionary '{}' is parsed.", dic.getName());
        dic = repository.save(dic);
        LOGGER.info("Dictionary '{}' is saved.", dic.getName());
        return mapper.toResource(dic);
    }

    @Transactional(readOnly = true)
    @Override
    public Resource downloadDictionary(long dictionaryId) {
        LOGGER.info("Downloads the dictionary with id={}", dictionaryId);
        return dictionaryWriter.write(getDictionary(dictionaryId));
    }

    @Transactional
    @Override
    public void deleteDictionary(long dictionaryId) {
        LOGGER.info("Delete dictionary with id={}", dictionaryId);
        repository.deleteById(dictionaryId);
        LOGGER.debug("The dictionary id={} has been deleted.", dictionaryId);
    }

    public Dictionary getDictionary(long dictionaryId) {
        return repository.findById(dictionaryId)
                .orElseThrow(() -> new IllegalArgumentException("Can't find dictionary by id=" + dictionaryId));
    }
}
