package it.gioxi.statemachine.repository;

import it.gioxi.statemachine.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByTitleContainingIgnoreCase(String title);
}

