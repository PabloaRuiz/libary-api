package com.spring.libaryapi.BookServiceTest;

import com.spring.libaryapi.Exception.BusinessException;
import com.spring.libaryapi.ModelEntity.Book;
import com.spring.libaryapi.ModelRepository.BookRepository;
import com.spring.libaryapi.Service.BookService;
import com.spring.libaryapi.Service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    private Book createValidBook() {
        return Book.builder().isbn("12").author("Pablo").title("As Aventuras de Pablo").build();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        // Cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(Book.builder()
                .id(1l)
                .isbn("12")
                .author("Pablo")
                .title("As avesturas de Pablo").build());

        //execução
        Book savedBook = service.save(book);

        //Verificação
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("12");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Pablo");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("As avesturas de Pablo");

    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicateISBN() {
        //Cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //Execução
      Throwable exception =  Assertions.catchThrowable(() -> service.save(book));

      //Verificação
     Assertions.assertThat(exception)
             .isInstanceOf(BusinessException.class)
             .hasMessage("ISBN já cadastrado.");

     Mockito.verify(repository, Mockito.never()).save(book);
    }

}
