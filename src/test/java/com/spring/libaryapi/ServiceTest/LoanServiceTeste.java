package com.spring.libaryapi.ServiceTest;


import com.spring.libaryapi.Exception.BusinessException;
import com.spring.libaryapi.ModelEntity.Book;
import com.spring.libaryapi.ModelEntity.Loan;
import com.spring.libaryapi.ModelRepository.LoanRepository;
import com.spring.libaryapi.Service.LoanService;
import com.spring.libaryapi.Service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTeste {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() {
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";
        Loan savingLoan = Loan.builder()
                .id(1l)
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1l)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(book))
                .thenReturn(false);

        Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);
        Loan loan = service.save(savedLoan);

        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        Assertions.assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
    }

    @Test
    @DisplayName("Deve lançar um erro de negócio ao salvar um emprestimo com pra um livro já emprestado")
    public void LoanBookSaveTeste() {
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .id(1l)
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(book))
                .thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(savingLoan));

        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(repository, Mockito.never()).save(savingLoan);
    }
}
