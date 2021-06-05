package com.gitlab.sszuev.flashcards.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.services.SoundService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

/**
 * Created by @ssz on 15.05.2021.
 */
@SpringBootTest
@ContextConfiguration(classes = {EntityMapper.class, ObjectMapper.class})
public class EntityMapperTest {

    @SuppressWarnings("unused")
    @MockBean
    private SoundService service;
    @Autowired
    private EntityMapper mapper;

    @Test
    public void testReadDetailsAsMap() {
        Card card = TestUtils.createCard(42, "x", 0, "{\"writing\":[true,true],\"self-test\":[false]}");
        Map<Stage, List<Boolean>> res = mapper.readDetailsAsMap(card);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(List.of(true, true), res.get(Stage.WRITING));
        Assertions.assertEquals(List.of(false), res.get(Stage.SELF_TEST));
    }

    @Test
    public void testWriteDetailsAsString() {
        Map<Stage, List<Boolean>> data = Map.of(Stage.SELF_TEST, List.of(true, false, true), Stage.MOSAIC, List.of(true));
        String json = mapper.writeDetailsAsString(data);
        Assertions.assertTrue(json.startsWith("{") && json.endsWith("}"));
        Assertions.assertTrue(json.contains("\"self-test\":[true,false,true]"));
        Assertions.assertTrue(json.contains("\"mosaic\":[true]"));
    }
}
