package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by @ssz on 23.02.2022.
 */
public interface LanguageRepository extends JpaRepository<Language, String> {
}
