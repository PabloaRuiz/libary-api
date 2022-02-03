package com.spring.libaryapi.BookControllerTeste;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.libaryapi.Dto.BookDTO;
import com.spring.libaryapi.Exception.BusinessException;
import com.spring.libaryapi.ModelEntity.Book;
import com.spring.libaryapi.Service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTeste {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve Criar um livro com sucesso.")
    public void createBookTeste() throws Exception {

        BookDTO dto = BookDTO.builder().author("Arthur").title("As aventuras do rei Thiago").isbn("001").build();
            Book savedBook = Book.builder().id(10L).author("Arthur").title("As aventuras do rei Thiago").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    // Validação de integridade
    @Test
    @DisplayName("Deve lançar um erro de validação quando não houver dados suficiente para criação do livro.")
    public void createInvalidBookTeste() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(3)));



    }

    //Validação de regra de negocio
    @Test
    @DisplayName("Deve lançar erro quando ao tentar cadastrar um livro com isbn já ultilizado por outro livro")
    public void createBookWithDuplicateIsbn() throws Exception {

       BookDTO dto = createNewBook();

        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "isbn já cadastrado";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));


    }

    private BookDTO createNewBook() {
        return  BookDTO.builder().author("Arthur").title("As aventuras do rei Thiago").isbn("001").build();
    }
}
