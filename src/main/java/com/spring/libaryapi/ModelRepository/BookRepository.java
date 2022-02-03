package com.spring.libaryapi.ModelRepository;

import com.spring.libaryapi.ModelEntity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
