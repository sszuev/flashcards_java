package com.gitlab.sszuev.flashcards.services.impl;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A facility to extract roles from {@code Integer}.
 * Created by @ssz on 01.08.2021.
 */
@Component
public class RoleMapper implements Function<Integer, Stream<String>> {

    @Override
    public Stream<String> apply(Integer value) {
        return roles(value).map(Enum::name);
    }

    protected Stream<Role> roles(Integer value) {
        return value == null ? Stream.of(Role.USER) : Arrays.stream(Role.values()).filter(x -> hasRole(value, x));
    }

    protected static boolean hasRole(int value, Role role) {
        return (value & role.code) == role.code;
    }

    protected enum Role {
        USER(2),
        ADMIN(256),
        ;
        private final int code;

        Role(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }
}
