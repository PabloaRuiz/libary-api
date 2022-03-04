package com.spring.libaryapi.ModelRepository;


import com.spring.libaryapi.ModelEntity.Book;
import com.spring.libaryapi.ModelEntity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
     public void existsByBookAndNotReturnedTest() {
        // cenário
        Book book = Book.builder().author("Arthur").title("As aventuras do rei Thiago").isbn("001").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        // execução
        boolean exists = repository.existsByBookAndNotReturned(book);

        Assertions.assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    public void findByBookIsbnOrCustomerTest() {
       Loan loan =  createAndPersistLoan(LocalDate.now());

        Page<Loan> result = repository.findByBookIsbnOrCustomer(
                "1234", "Fulano", PageRequest.of(0, 10));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);

    }

    @Test
    @DisplayName("Deve obter empréstimos cuja data emprestimo for menor ou igual a tres dias não retornados")
    public void findByLoanDateLessThanAndNotReturnedTest() {

        Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));


        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));


        Assertions.assertThat(result).hasSize(1).contains(loan);

    }


    @Test
    @DisplayName("Deve retornar vazio quando não houver emprestimos atrasados.")
    public void notfindByLoanDateLessThanAndNotReturnedTest() {

        Loan loan = createAndPersistLoan(LocalDate.now());


        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));


        Assertions.assertThat(result).isEmpty();

    }

    public Loan createAndPersistLoan(LocalDate loanDate) {
        Book book = Book.builder().author("Arthur").title("As aventuras do rei Thiago").isbn("1234").build();

        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();

        entityManager.persist(loan);

        return loan;
    }
}
