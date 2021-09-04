package com.gitlab.sszuev.flashcards.services;

import com.gitlab.sszuev.flashcards.dto.AccountRequest;

/**
 * Created by @ssz on 04.09.2021.
 */
public interface AccountService {
    boolean save(AccountRequest user);
}
