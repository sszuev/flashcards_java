package com.gitlab.sszuev.flashcards.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

/**
 * Created by @ssz on 08.05.2021.
 */
@SpringBootTest
public class SoundServiceTest {
    @Autowired
    private SoundService service;
    @MockBean
    private SoundProvider provider;

    @Test
    public void testGetResourceName() {
        String given = "x";
        String expected = "y";
        Mockito.when(provider.getResourceName(Mockito.eq(given))).thenReturn(expected);

        Assertions.assertEquals(expected, service.getResourceName(given));
        Assertions.assertNull(service.getResourceName("XXX"));
    }

    @Test
    public void testGetResource() {
        String given = "x";
        Resource expected = Mockito.mock(Resource.class);
        Mockito.when(provider.getResource(Mockito.eq(given))).thenReturn(expected);

        Assertions.assertSame(expected, service.getResource(given));
        Assertions.assertNull(service.getResource("XXX"));
    }
}
