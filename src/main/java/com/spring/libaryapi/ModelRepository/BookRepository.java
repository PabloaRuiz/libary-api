package com.spring.libaryapi.ModelRepository;

import com.spring.libaryapi.ModelEntity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsById(Long id);

    Optional<Book> findByIsbn(String isbn);
}
