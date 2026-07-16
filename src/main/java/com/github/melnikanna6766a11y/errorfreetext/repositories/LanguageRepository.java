package com.github.melnikanna6766a11y.errorfreetext.repositories;

import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    public Optional<Language> findByLanguage(String language);
}
