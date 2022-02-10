package com.spring.libaryapi.ModelRepository;

import com.spring.libaryapi.ModelEntity.Book;
import com.spring.libaryapi.ModelEntity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {


    boolean existsByBookAndNotReturned(Book book);
}
