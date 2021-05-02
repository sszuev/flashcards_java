package com.gitlab.sszuev.flashcards.parser;

/**
 * Created by @ssz on 02.05.2021.
 */
public class WrongDataException extends IllegalArgumentException {

    public WrongDataException(String message) {
        super(message);
    }

    public WrongDataException(Throwable cause) {
        super(cause);
    }

    public static <X> X requireNonNull(X obj, String msg) {
        if (obj == null)
            throw new WrongDataException(msg);
        return obj;
    }
}
