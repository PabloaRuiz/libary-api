package com.spring.libaryapi.ModelRepository;

import com.spring.libaryapi.ModelEntity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsById(Long id);

    Optional<Book> findByIsbn(String isbn);
}
