package com.spring.libaryapi.ServiceTest;


import com.spring.libaryapi.Dto.LoanFilterDTO;
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
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    private Loan CreateValid() {
        return Loan.builder().customer("Pablo").loanDate(LocalDate.now()).build();
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

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID")
    public void getLoanDataIsTest() {
        // Cenario
        Long id = 1l;
        Book book = Book.builder().build();
        String customer = "Pablo";

        Loan savingLoan =
                Loan.builder()
                        .id(id)
                        .book(book)
                        .customer(customer)
                        .loanDate(LocalDate.now())
                        .build();

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(savingLoan));

        //execução
        Optional<Loan> result = service.getById(id);

        //Verificação
        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
        Assertions.assertThat(result.get().getCustomer()).isEqualTo(savingLoan.getCustomer());
        Assertions.assertThat(result.get().getBook()).isEqualTo(savingLoan.getBook());
        Assertions.assertThat(result.get().getLoanDate()).isEqualTo(savingLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoanTest() {
        // Cenario
        Loan loan = CreateValid();
        loan.setId(1l);
        loan.setReturned(true);
        Mockito.when(repository.save(loan)).thenReturn(loan);

        //Execução
        Loan updatedLoan = service.update(loan);

        //Verificação
        Mockito.verify(repository).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar empréstimos pelas propriedades")
    public void findLoanTest() {
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Teste").isbn("fdfdsfds").build();

        Loan loan = CreateValid();
        loan.setId(1l);

        PageRequest pageRequest = PageRequest.of(0,10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, 1);
        BDDMockito.when(repository.findByBookIsbnOrCustomer(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(PageRequest.class))
                )
                .thenReturn(page);

        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(lista);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
