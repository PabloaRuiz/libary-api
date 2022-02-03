package com.spring.libaryapi.ModelRepository;

import com.spring.libaryapi.ModelEntity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;


    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o ISBN informado")
    public void returnTrueWhenIsbnExists() {
        // Cenario
        String isbn = "1234";

        Book book = Book.builder().isbn(isbn).author("Pablo").title("As Aventuras de Pablo").build();

        entityManager.persist(book);

        //Execução
         boolean exists =  repository.existsByIsbn(isbn);

        //Verificação
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando não existir um livro na base com o ISBN informado")
    public void returnFalseWhenIsbnDoesntExists() {
        // Cenario
        String isbn = "1234";

        Book book = Book.builder().isbn("12345678").author("Pablo").title("As Aventuras de Pablo").build();

        entityManager.persist(book);

        //Execução
        boolean exists =  repository.existsByIsbn(isbn);

        //Verificação
        Assertions.assertThat(exists).isFalse();
    }


}
