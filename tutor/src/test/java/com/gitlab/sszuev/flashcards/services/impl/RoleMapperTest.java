package com.gitlab.sszuev.flashcards.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ContextConfiguration(classes = {RoleMapper.class})
@ExtendWith(SpringExtension.class)
public class RoleMapperTest {
    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void testMap() {
        test(null, "USER");
        test(2, "USER");
        test(256, "ADMIN");

        test(0b100010010, "ADMIN", "USER"); // 274
    }

    private void test(Integer given, String expected) {
        Assertions.assertEquals(List.of(expected), roleMapper.apply(given).collect(Collectors.toList()));
    }

    @SuppressWarnings("SameParameterValue")
    private void test(Integer given, String... expected) {
        Assertions.assertEquals(Set.of(expected), roleMapper.apply(given).collect(Collectors.toSet()));
    }

    @Test
    public void testHasRole() {
        Assertions.assertTrue(RoleMapper.hasRole(42, RoleMapper.Role.USER));
        Assertions.assertFalse(RoleMapper.hasRole(0, RoleMapper.Role.USER));
    }

    @Test
    public void testRoleCode() {
        Assertions.assertEquals(2, RoleMapper.Role.USER.code());
    }
}

