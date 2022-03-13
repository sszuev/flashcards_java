package com.gitlab.sszuev.flashcards.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.services.SoundService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

/**
 * Created by @ssz on 15.05.2021.
 */
@SpringBootTest
@ContextConfiguration(classes = {EntityMapper.class, ObjectMapper.class})
public class EntityMapperTest {

    @MockBean
    private SoundService service;
    @MockBean
    private RunConfig config;
    @Autowired
    private EntityMapper mapper;

    @Test
    public void testReadDetailsAsMap() {
        Map<Stage, Long> res = mapper.readDetailsAsMap("{\"writing\":3,\"self-test\":0}");
        Assertions.assertNotNull(res);
        Assertions.assertEquals(3L, res.get(Stage.WRITING));
        Assertions.assertEquals(0L, res.get(Stage.SELF_TEST));
    }

    @Test
    public void testWriteDetailsAsString() {
        Map<Stage, Long> data = Map.of(Stage.SELF_TEST, 42L, Stage.MOSAIC, 1L);
        String json = mapper.writeDetailsAsString(data);
        Assertions.assertTrue(json.startsWith("{") && json.endsWith("}"));
        Assertions.assertTrue(json.contains("\"self-test\":42"));
        Assertions.assertTrue(json.contains("\"mosaic\":1"));
    }
}
