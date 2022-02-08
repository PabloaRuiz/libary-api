package com.spring.libaryapi.BookServiceTest;

import com.spring.libaryapi.Exception.BusinessException;
import com.spring.libaryapi.ModelEntity.Book;
import com.spring.libaryapi.ModelRepository.BookRepository;
import com.spring.libaryapi.Service.BookService;
import com.spring.libaryapi.Service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.hibernate.mapping.Array;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {
        // Cenario
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
        // Execução
        Optional<Book> foundBook = service.getById(id);
        // Verificação
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não estiver na base.")
    public void bookNotFoundByIdTest() {
        // Cenario
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        // Execução
        Optional<Book> book = service.getById(id);
        // Verificação
        Assertions.assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar vazio ao deletar um livro")
    public void bookDeteleById() {
        // Cenario
        Book book = Book.builder().id(1l).build();
        //Execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));
        //verificação
        Mockito.verify(repository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve retornar um erro ao deletar um livro sem id")
    public void bookExceptionDeletById() {
        // Cenario
        Book book = new Book();
        // Execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.delete(book));
        //Verificação
        Mockito.verify(repository, Mockito.times(0)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar atualizar um livro inxistente")
    public void bookUpdateExcepetionById() {
        // Cenario
        Book book = new Book();
        // Execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.update(book));
        // Verificação
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void bookUpdateById() {
        Long id = 1l;
        //Livro que vai ser tualziado
        Book updatingBook = Book.builder().id(id).build();

        //Simulação de atualização
        Book updatedBook = createValidBook();
        updatedBook.setId(id);
        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        //Execução
        Book book = service.update(updatingBook);

        //verficiação
        Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve filtrar livros pela propriedades")
    public void findBookTest() {

        Book book = createValidBook();

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, PageRequest.of(0, 10), 100);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

       Page<Book> result =  service.find(book, PageRequest.of(0, 10));


        Assertions.assertThat(result.getTotalElements()).isEqualTo(100);
        Assertions.assertThat(result.getContent()).isEqualTo(lista);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }
    
    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookIsbnTest() {

        String isbn = "1234";

        Mockito.when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1l).isbn(isbn).build()));

       Optional<Book> book = service.getBookByIsbn(isbn);

       Assertions.assertThat(book.isPresent()).isTrue();
       Assertions.assertThat(book.get().getId()).isEqualTo(1l);
       Assertions.assertThat(book.get().getIsbn()).isEqualTo(isbn);

       Mockito.verify(repository, Mockito.times(1)).findByIsbn(isbn);




    }

}
