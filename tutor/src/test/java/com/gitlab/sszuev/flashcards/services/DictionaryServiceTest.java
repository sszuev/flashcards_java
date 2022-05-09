package com.gitlab.sszuev.flashcards.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.documents.impl.LingvoDictionaryWriter;
import com.gitlab.sszuev.flashcards.documents.impl.LingvoDocumentParser;
import com.gitlab.sszuev.flashcards.documents.impl.Status;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.DictionaryResource;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import com.gitlab.sszuev.flashcards.services.impl.DictionaryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
@ContextConfiguration(classes = {DictionaryServiceImpl.class, RunConfig.class, EntityMapper.class, ObjectMapper.class})
@TestPropertySource(properties = {
        "app.loader.enabled=false",
        "app.tutor.run.answers=" + DictionaryServiceTest.NUMBER_OF_ANSWERS_TO_LEARN
})
public class DictionaryServiceTest {
    static final int NUMBER_OF_ANSWERS_TO_LEARN = 6;

    @Autowired
    private DictionaryService service;
    @MockBean
    private DictionaryRepository dictionaryRepository;
    @MockBean
    private LingvoDocumentParser lingvoParser;
    @MockBean
    private LingvoDictionaryWriter lingvoWriter;
    @MockBean
    private SoundService soundService;

    @Test
    public void testListDictionaries() {
        long id1 = -1;
        long id2 = -2;
        String name1 = "A";
        String name2 = "B";
        Language lang1 = TestUtils.mockLanguage("ee", List.of("A", "B"));
        Language lang2 = TestUtils.mockLanguage("rr", List.of("C", "D"));
        Language lang3 = TestUtils.mockLanguage("xx", List.of("E", "F"));
        Map<Status, Integer> cards1 = Map.of(Status.IN_PROCESS, 2, Status.LEARNED, 3);
        Map<Status, Integer> cards2 = Map.of(Status.UNKNOWN, 1, Status.LEARNED, 42);

        Dictionary dic1 = TestUtils.mockDictionary(id1, name1, lang1, lang2, NUMBER_OF_ANSWERS_TO_LEARN, cards1);
        Dictionary dic2 = TestUtils.mockDictionary(id2, name2, lang2, lang3, NUMBER_OF_ANSWERS_TO_LEARN, cards2);

        Mockito.when(dictionaryRepository.streamAllByUserId(Mockito.eq(User.SYSTEM_USER.getID())))
                .thenReturn(Stream.of(dic1, dic2));

        List<DictionaryResource> res = service.getDictionaries();
        Assertions.assertNotNull(res);
        Assertions.assertEquals(2, res.size());
        assertDictionaryResource(res.get(0), id1, name1, lang1, lang2, cards1);
        assertDictionaryResource(res.get(1), id2, name2, lang2, lang3, cards2);
    }

    @Test
    public void testUploadDictionary() {
        Language src = TestUtils.mockLanguage("src", null);
        Language dst = TestUtils.mockLanguage("dst", null);

        Dictionary dic1 = TestUtils.mockDictionary(null, "The dictionary #1");
        Dictionary dic2 = TestUtils.mockDictionary(42L, "The dictionary #2", src, dst);
        Mockito.when(lingvoParser.parse(Mockito.any(Reader.class))).thenReturn(dic1);
        Mockito.when(dictionaryRepository.save(dic1)).thenReturn(dic2);

        DictionaryResource res = service.uploadDictionary("<xml>");
        Assertions.assertNotNull(res);
        Assertions.assertEquals(42L, res.id());
        Assertions.assertEquals("The dictionary #2", res.name());
        Assertions.assertEquals("src", res.sourceLang());
        Assertions.assertEquals("dst", res.targetLang());
    }

    @Test
    public void testDeleteDictionary() {
        Long id = 42L;
        Mockito.doAnswer(i -> i.getArgument(0) == id ? null : Assertions.fail("unexpected arg"))
                .when(dictionaryRepository).deleteById(Mockito.anyLong());
        service.deleteDictionary(id);
        Mockito.verify(dictionaryRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    public void testDownloadDictionary() {
        Long id = 42L;
        Dictionary d = TestUtils.mockDictionary(id, "XXx");
        Mockito.when(dictionaryRepository.findById(Mockito.eq(id))).thenReturn(Optional.of(d));
        service.downloadDictionary(id);
        Mockito.verify(dictionaryRepository, Mockito.times(1)).findById(Mockito.eq(id));
        Mockito.verify(lingvoWriter, Mockito.times(1)).write(d);
    }

    private void assertDictionaryResource(DictionaryResource res,
                                          long id, String name,
                                          Language src, Language dst,
                                          Map<Status, Integer> data) {
        Assertions.assertEquals(name, res.name());
        Assertions.assertEquals(id, res.id());
        Assertions.assertEquals(src.getID(), res.sourceLang());
        Assertions.assertEquals(dst.getID(), res.targetLang());
        Assertions.assertEquals(data.get(Status.LEARNED).longValue(), res.learned());
        Assertions.assertEquals(data.values().stream().mapToLong(x -> x).sum(), res.total());
        List<String> partsOfSpeech = List.of(src.getPartsOfSpeech().toLowerCase(Locale.ROOT).split(","));
        Assertions.assertEquals(partsOfSpeech, res.partsOfSpeech());
    }
}
